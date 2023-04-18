package org.jboss.resteasy.test.util.resource;

public class TypesGenericClassFooBar extends TypesGenericClassFoo<Integer, Float> {
    @Override
    public void foo(Integer integer) {
    }

    @Override
    public void bar(Float aFloat) {
    }

    @Override
    public void bar(Double d) {
    }
}
