module aoc.platform {
    requires javax.faces.api;
    requires javax.servlet.api;
    requires jackson.annotations;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires lucene.core;
    requires slf4j.api;
    requires spring.beans;
    requires spring.boot;
    requires spring.context;
    requires spring.core;

    exports aoc.ddd.event;
    exports aoc.ddd.model;
    exports aoc.ddd.repository;
    exports aoc.ddd.search;
    exports aoc.ddd.specification;
    exports aoc.ddd.spring;
    exports aoc.fs;
    exports aoc.jsf;
    exports aoc.tools;
    exports aoc.tools.datatransfer;
    exports aoc.transaction;
}
