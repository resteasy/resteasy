package org.jboss.resteasy.resteasy801;

import org.jboss.resteasy.annotations.providers.Jackson2;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Type1.class, name = "type1"),
        @JsonSubTypes.Type(value = Type2.class, name = "type2")})
@Jackson2
public abstract class AbstractParent {
    
    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

