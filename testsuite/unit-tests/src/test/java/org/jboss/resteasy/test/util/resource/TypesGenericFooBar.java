package org.jboss.resteasy.test.util.resource;

public class TypesGenericFooBar implements TypesGenericFoo<Integer, Float> {
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
