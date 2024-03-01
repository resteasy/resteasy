package org.jboss.resteasy.util;

import java.util.Locale;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LocaleHelper {
    public static Locale extractLocale(String lang) {
        int q = lang.indexOf(';');
        if (q > -1)
            lang = lang.substring(0, q);
        return Locale.forLanguageTag(lang);
    }

    /**
     * HTTP 1.1 has different String format for language than what java.util.Locale does '-' instead of '_'
     * as a separator
     *
     * @param value locale
     * @return converted language format string
     */
    public static String toLanguageString(Locale value) {
        return value.toLanguageTag();
    }
}
