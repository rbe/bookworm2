/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.wildcardQuery;

@Slf4j
class ElasticsearchClient {

    private static final String INDEX_NAME = "hoerbuecher";

    private final RestHighLevelClient restHighLevelClient;

    private final ObjectMapper objectMapper;

    @Inject
    ElasticsearchClient(final RestHighLevelClient restHighLevelClient, final ObjectMapper objectMapper) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    boolean indexExists() {
        final GetIndexRequest indexRequest = new GetIndexRequest();
        indexRequest.indices(INDEX_NAME);
        try {
            return restHighLevelClient.indices().exists(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchClientException(e);
        }
    }

    boolean createIndex() {
        if (!indexExists()) {
            final CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX_NAME);
            /*createIndexRequest.settings(Settings.builder()
                .loadFromStream("/index-mappings.json", )
                .build());*/
            try {
                restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new ElasticsearchClientException(e);
            }
        }
        final PutMappingRequest putMappingRequest = new PutMappingRequest(INDEX_NAME);
        final byte[] bytes;
        try (final InputStream resourceAsStream = getClass().getResourceAsStream("/index-mappings.json")) {
            bytes = resourceAsStream.readAllBytes();
        } catch (IOException e) {
            throw new ElasticsearchClientException(e);
        }
        putMappingRequest.source(new String(bytes, StandardCharsets.UTF_8), XContentType.JSON);
        try {
            final AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices()
                    .putMapping(putMappingRequest, RequestOptions.DEFAULT);
            return acknowledgedResponse.isAcknowledged();
        } catch (IOException e) {
            throw new ElasticsearchClientException(e);
        }
    }

    boolean index(String json) {
        try {
            final IndexRequest indexRequest = new IndexRequest()
                    .index(INDEX_NAME)
                    .id(UUID.randomUUID().toString())
                    .type("_doc")
                    .source(json, XContentType.JSON);
            //.source(objectMapper.writeValueAsString(indexable), XContentType.JSON);
            final IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            return 201 == indexResponse.status().getStatus();
        } catch (JsonProcessingException e) {
            log.error("Kann '{}' nicht indizieren: {}", json, e);
            return false;
        } catch (IOException e) {
            throw new ElasticsearchClientException(e);
        }
    }

    boolean bulkIndex(final List<String> jsonStrings) {
        final BulkRequest bulkRequest = new BulkRequest();
        jsonStrings.forEach(json -> bulkRequest.add(new IndexRequest()
                .index(INDEX_NAME)
                .id(UUID.randomUUID().toString())
                .type("_doc")
                .source(json, XContentType.JSON)));
        try {
            final BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            return !bulkResponse.hasFailures();
        } catch (IOException e) {
            throw new ElasticsearchClientException(e);
        }
    }

    SearchHit[] findAll(String keyword) {
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(INDEX_NAME);
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        final String k = "*"+keyword+"*";
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .should(wildcardQuery("title", k))
                .should(wildcardQuery("author", k))
        );
        searchRequest.source(searchSourceBuilder);
        try {
            final SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return searchResponse.getHits().getHits();
        } catch (IOException e) {
            throw new ElasticsearchClientException(e);
        }
    }

}
