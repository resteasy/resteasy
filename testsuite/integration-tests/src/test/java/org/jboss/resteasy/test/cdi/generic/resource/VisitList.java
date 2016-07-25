package org.jboss.resteasy.test.cdi.generic.resource;

import java.util.ArrayList;

public class VisitList {
    public static final  String CONCRETE_DECORATOR_ENTER = "concreteDecoratorEnter";
    public static final String CONCRETE_DECORATOR_LEAVE = "concreteDecoratorLeave";
    public static final String UPPER_BOUND_DECORATOR_ENTER = "upperBoundDecoratorEnter";
    public static final String UPPER_BOUND_DECORATOR_LEAVE = "upperBoundDecoratorLeave";
    public static final String LOWER_BOUND_DECORATOR_ENTER = "lowerBoundDecoratorEnter";
    public static final String LOWER_BOUND_DECORATOR_LEAVE = "lowerBoundDecoratorLeave";

    private static ArrayList<String> visitList = new ArrayList<String>();

    public static void add(String o) {
        visitList.add(o);
    }

    public static ArrayList<String> getList() {
        return new ArrayList<String>(visitList);
    }

    public static void clear() {
        visitList.clear();
    }
}

