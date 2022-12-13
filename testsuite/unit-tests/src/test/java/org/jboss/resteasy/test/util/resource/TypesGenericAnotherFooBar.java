package org.jboss.resteasy.test.util.resource;

public class TypesGenericAnotherFooBar implements TypesGenericAnotherFoo<Integer> {
    @Override
    public void foo(Integer integer) {
    }

    @Override
    public void bar(int i) {
    }
}
