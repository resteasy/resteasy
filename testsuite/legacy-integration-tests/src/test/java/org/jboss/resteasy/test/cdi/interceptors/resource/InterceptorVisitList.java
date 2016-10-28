package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.util.ArrayList;

public class InterceptorVisitList {
    private static ArrayList<Object> visitList = new ArrayList<Object>();

    public static void add(Object interceptor) {
        visitList.add(interceptor);
    }

    public static ArrayList<Object> getList() {
        return new ArrayList<Object>(visitList);
    }
}

