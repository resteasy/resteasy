package org.jboss.resteasy.test.util.resource;

public interface TypesGenericFoo<R, T> extends TypesGenericBar<T> {
    void foo(R r);

    void bar(Double d);
}
