/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.helloworld;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;

@Client(value = "/hello", path = "/hello")
public interface HelloClient {

    @Get("/json")
    HelloWorldDTO helloJson(String name);

    @Get("/json/string")
    String jsonString();

}
