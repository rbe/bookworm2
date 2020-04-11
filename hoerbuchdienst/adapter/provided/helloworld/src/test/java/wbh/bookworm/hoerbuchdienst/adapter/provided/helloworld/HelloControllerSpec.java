/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.helloworld;

import javax.inject.Inject;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class HelloControllerSpec {

    private final EmbeddedServer server;

    @Inject
    @Client("/")
    private HttpClient client;

    private final HelloClient helloClient;

    @Inject
    public HelloControllerSpec(final EmbeddedServer server, final HelloClient helloClient) {
        this.server = server;
        this.helloClient = helloClient;
    }

    @Test
    void testHelloWorldResponse() {
        String response = client.toBlocking().retrieve(HttpRequest.GET("/hello/plain"));
        assertEquals("Hello World", response);
    }

    @Test
    void testHelloWorldJsonResponse() {
        /*HelloWorldDTO response = client.toBlocking().retrieve(HttpRequest.GET("/hello/json"),
                HelloWorldDTO.class);*/
        HelloWorldDTO response = helloClient.helloJson("Ralf");
        assertEquals(new HelloWorldDTO("Ralf"), response);
    }

}
