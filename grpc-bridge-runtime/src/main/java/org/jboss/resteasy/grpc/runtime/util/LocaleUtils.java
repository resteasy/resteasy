package org.jboss.resteasy.grpc.runtime.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Utility methods for getting the locale from a request.
 *
 * @author Stuart Douglas (from undertow)
 */
public class LocaleUtils {

    public static Locale getLocaleFromString(String localeString) {
        if (localeString == null) {
            return null;
        }
        return Locale.forLanguageTag(localeString);
    }

    /**
     * Parse a header string and return the list of locales that were found.
     *
     * If the header is empty or null then an empty list will be returned.
     *
     * @param acceptLanguage The Accept-Language header
     * @return The list of locales, in order of preference
     */
    public static List<Locale> getLocalesFromHeader(final String acceptLanguage) {
        if(acceptLanguage == null) {
            return Collections.emptyList();
        }
        return getLocalesFromHeader(Collections.singletonList(acceptLanguage));
    }

    /**
     * Parse a header string and return the list of locales that were found.
     *
     * If the header is empty or null then an empty list will be returned.
     *
     * @param acceptLanguage The Accept-Language header
     * @return The list of locales, in order of preference
     */
    public static List<Locale> getLocalesFromHeader(final List<String> acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Locale> ret = new ArrayList<>();
        final List<List<QValueParser.QValueResult>> parsedResults = QValueParser.parse(acceptLanguage);
        for (List<QValueParser.QValueResult> qvalueResult : parsedResults) {
            for (QValueParser.QValueResult res : qvalueResult) {
                if (!res.isQValueZero()) {
                    Locale e = LocaleUtils.getLocaleFromString(res.getValue());
                    ret.add(e);
                }
            }
        }
        return ret;
    }
}
