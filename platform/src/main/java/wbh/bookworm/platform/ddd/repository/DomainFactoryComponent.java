/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository;

import wbh.bookworm.platform.ddd.spring.Singleton;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Inherited;

@Inherited
@Singleton
@Configuration
public @interface DomainFactoryComponent {
}
