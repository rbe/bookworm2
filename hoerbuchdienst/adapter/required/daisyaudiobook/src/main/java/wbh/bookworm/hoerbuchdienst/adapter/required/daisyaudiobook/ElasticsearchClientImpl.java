/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class ElasticsearchClientImpl implements ElasticsearchClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchClientImpl.class);

    private static final String INDEX_NAME = "hoerbuecher";

    private final RestHighLevelClient restHighLevelClient;

    @Inject
    ElasticsearchClientImpl(final RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public boolean indexExists() {
        final GetIndexRequest indexRequest = new GetIndexRequest();
        indexRequest.indices(INDEX_NAME);
        try {
            return restHighLevelClient.indices().exists(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchClientException(e);
        }
    }

    @Override
    public boolean createIndex() {
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

    @Override
    public boolean index(String json) {
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
            LOGGER.error("Kann '{}' nicht indizieren: {}", json, e);
            return false;
        } catch (IOException e) {
            throw new ElasticsearchClientException(e);
        }
    }

    @Override
    public boolean bulkIndex(final List<String> jsonStrings) {
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

    @Override
    public SearchHit[] findAll(final String[] keyword) {
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(INDEX_NAME);
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        final String k = String.format("*%s*", keyword[0]);
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .should(QueryBuilders.wildcardQuery("title", k))
                .should(QueryBuilders.wildcardQuery("author", k))
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
