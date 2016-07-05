package org.jboss.resteasy.test.injection.resource;

public class SessionBeanInterfaceFoo implements SessionBeanInterfaceFooLocal, SessionBeanInterfaceFooLocal2, SessionBeanInterfaceFooLocal3 {

    public void foo1() {
    }

    public void foo2() {
    }

    public String foo3() {
        return "foo";
    }
}
