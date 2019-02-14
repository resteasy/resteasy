package org.jboss.resteasy.test.providers.jsonb.basic.resource;

public class JsonBindingDebugLoggingItemCorruptedSet {
    Integer a;

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        if (true) throw new RuntimeException(JsonBindingDebugLoggingItemCorruptedSet.class.getSimpleName());
        this.a = a;
    }
}
