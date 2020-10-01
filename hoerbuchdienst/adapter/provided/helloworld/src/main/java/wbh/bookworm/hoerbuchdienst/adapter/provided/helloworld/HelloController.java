/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.helloworld;

import javax.inject.Inject;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.codec.MediaTypeCodec;
import io.micronaut.http.codec.MediaTypeCodecRegistry;

@Controller("/hello")
public class HelloController {

    private final MediaTypeCodecRegistry mediaTypeCodecRegistry;

    @Inject
    public HelloController(final MediaTypeCodecRegistry mediaTypeCodecRegistry) {
        this.mediaTypeCodecRegistry = mediaTypeCodecRegistry;
    }

    @Get(uri = "/plain", produces = MediaType.TEXT_PLAIN)
    public String helloWorld() {
        return "Hello World";
    }

    @Get(uri = "/json", produces = MediaType.APPLICATION_JSON)
    public HelloWorldDTO helloWorldJson() {
        return new HelloWorldDTO("Ralf");
    }

    @Get(uri = "/json/string", produces = MediaType.APPLICATION_JSON)
    public String jsonString() {
        final MediaTypeCodec codec = mediaTypeCodecRegistry
                .findCodec(MediaType.APPLICATION_JSON_TYPE)
                .orElseThrow();
        return new String(codec.encode("Test"));
    }

}
