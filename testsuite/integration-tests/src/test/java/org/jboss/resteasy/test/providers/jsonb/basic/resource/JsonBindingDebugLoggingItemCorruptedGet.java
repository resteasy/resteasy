package org.jboss.resteasy.test.providers.jsonb.basic.resource;

public class JsonBindingDebugLoggingItemCorruptedGet {
    Integer a;

    public Integer getA() {
        if (true) throw new RuntimeException(JsonBindingDebugLoggingItemCorruptedGet.class.getSimpleName());
        return a;
    }

    public JsonBindingDebugLoggingItemCorruptedGet setA(Integer a) {
        this.a = a;
        return this;
    }
}
