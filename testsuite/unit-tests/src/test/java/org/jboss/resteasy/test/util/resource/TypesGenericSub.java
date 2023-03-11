package org.jboss.resteasy.test.util.resource;

public class TypesGenericSub extends TypesGeneric<Integer, Float> {
    @Override
    public void foo(Integer integer) {
    }

    @Override
    public void bar(Double d) {
    }

    @Override
    public void bar(Float t) {
    }
}
