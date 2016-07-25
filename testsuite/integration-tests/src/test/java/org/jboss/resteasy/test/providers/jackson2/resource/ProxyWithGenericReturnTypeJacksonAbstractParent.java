package org.jboss.resteasy.test.providers.jackson2.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProxyWithGenericReturnTypeJacksonType1.class, name = "type1"),
        @JsonSubTypes.Type(value = ProxyWithGenericReturnTypeJacksonType2.class, name = "type2")})
public abstract class ProxyWithGenericReturnTypeJacksonAbstractParent {

    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
