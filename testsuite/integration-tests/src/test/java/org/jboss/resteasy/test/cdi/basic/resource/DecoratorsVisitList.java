package org.jboss.resteasy.test.cdi.basic.resource;

import java.util.ArrayList;

public class DecoratorsVisitList {
    public static final String REQUEST_FILTER_DECORATOR_ENTER = "requestFilterDecoratorEnter";
    public static final String REQUEST_FILTER_DECORATOR_LEAVE = "requestFilterDecoratorLeave";
    public static final String RESPONSE_FILTER_DECORATOR_ENTER = "responseFilterDecoratorEnter";
    public static final String RESPONSE_FILTER_DECORATOR_LEAVE = "responseFilterDecoratorLeave";

    public static final String READER_INTERCEPTOR_DECORATOR_ENTER = "readerInterceptorDecoratorEnter";
    public static final String READER_INTERCEPTOR_DECORATOR_LEAVE = "readerInterceptorDecoratorLeave";
    public static final String READER_INTERCEPTOR_ENTER = "readerInterceptorEnter";
    public static final String READER_INTERCEPTOR_LEAVE = "readerInterceptorLeave";
    public static final String READER_DECORATOR_ENTER = "readerDecoratorEnter";
    public static final String READER_DECORATOR_LEAVE = "readerDecoratorLeave";

    public static final String WRITER_INTERCEPTOR_DECORATOR_ENTER = "writerInterceptorDecoratorEnter";
    public static final String WRITER_INTERCEPTOR_DECORATOR_LEAVE = "writerInterceptorDecoratorLeave";
    public static final String WRITER_INTERCEPTOR_ENTER = "writerInterceptorEnter";
    public static final String WRITER_INTERCEPTOR_LEAVE = "writerInterceptorLeave";
    public static final String WRITER_DECORATOR_ENTER = "writerDecoratorEnter";
    public static final String WRITER_DECORATOR_LEAVE = "writerDecoratorLeave";

    public static final String RESOURCE_INTERCEPTOR_ENTER = "resourceInterceptorEnter";
    public static final String RESOURCE_INTERCEPTOR_LEAVE = "resourceInterceptorLeave";
    public static final String RESOURCE_DECORATOR_ENTER = "resourceDecoratorEnter";
    public static final String RESOURCE_DECORATOR_LEAVE = "resourceDecoratorLeave";
    public static final String RESOURCE_ENTER = "resourceEnter";
    public static final String RESOURCE_LEAVE = "resourceLeave";

    private static ArrayList<String> visitList = new ArrayList<String>();

    public static void add(String o) {
        visitList.add(o);
    }

    public static ArrayList<String> getList() {
        return new ArrayList<String>(visitList);
    }
}

