/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.helloworld;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(title = "wbh.sds", version = "0.0")
)
@Controller("/hello")
public class HelloController {

    @Get(uri = "/plain", produces = MediaType.TEXT_PLAIN)
    public String helloWorld() {
        return "Hello World";
    }

    @Get(uri = "/json", produces = MediaType.APPLICATION_JSON)
    public HelloWorldDTO helloWorldJson() {
        return new HelloWorldDTO("Ralf");
    }

}
