/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

final class DomainIdJacksonSupport {

    private DomainIdJacksonSupport() {
        throw new AssertionError();
    }

    public static class DomainIdSerializer extends StdSerializer<DomainId<String>> {

        public DomainIdSerializer() {
            this(DomainId.class, false);
        }

        public DomainIdSerializer(final Class<DomainId<String>> t) {
            super(t);
        }

        public DomainIdSerializer(final JavaType type) {
            super(type);
        }

        public DomainIdSerializer(final Class<?> t, final boolean dummy) {
            super(t, dummy);
        }

        public DomainIdSerializer(final StdSerializer<?> src) {
            super(src);
        }

        @Override
        public void serialize(final DomainId<String> value,
                              final JsonGenerator gen,
                              final SerializerProvider provider) throws IOException {
            gen.writeString(value.getValue());
        }

        @Override
        public void serializeWithType(final DomainId<String> value,
                                      final JsonGenerator gen,
                                      final SerializerProvider serializers,
                                      final TypeSerializer typeSer) throws IOException {
            final WritableTypeId typeId = typeSer.typeId(value, JsonToken.START_OBJECT);
            typeSer.writeTypePrefix(gen, typeId);
            gen.writeFieldName("value");
            gen.writeString(value.getValue());
            typeId.wrapperWritten = !gen.canWriteTypeId();
            typeSer.writeTypeSuffix(gen, typeId);
        }

    }

    public static class DomainIdDeserializer extends StdDeserializer<DomainId<String>> {

        public DomainIdDeserializer() {
            this(DomainId.class);
        }

        public DomainIdDeserializer(final Class<?> vc) {
            super(vc);
        }

        public DomainIdDeserializer(final JavaType valueType) {
            super(valueType);
        }

        public DomainIdDeserializer(final StdDeserializer<?> src) {
            super(src);
        }

        @Override
        public DomainId<String> deserialize(final JsonParser p, final DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            return new DomainId<>(p.getValueAsString());
        }

    }

}
