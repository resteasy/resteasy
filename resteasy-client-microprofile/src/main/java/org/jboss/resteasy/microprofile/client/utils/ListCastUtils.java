package org.jboss.resteasy.microprofile.client.utils;

import java.util.List;
import java.util.stream.Collectors;

public class ListCastUtils {

    private ListCastUtils() {
    }

    public static List<String> castToListOfStrings(List<Object> values) {
        return values.stream()
                .map(val -> val instanceof String
                        ? (String) val
                        : String.valueOf(val))
                .collect(Collectors.toList());
    }
}
