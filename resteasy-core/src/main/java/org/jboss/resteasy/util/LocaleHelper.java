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
        // This is somewhat of a hack to fix an issue in the TCK, see https://github.com/jakartaee/rest/issues/1239.
        // However, if the Locale was created incorrectly, we should likely continue to return the string value of the
        // locale.
        String languageTag = value.toLanguageTag();
        if ("und".equals(languageTag)) {
            languageTag = value.getLanguage().toLowerCase(Locale.ROOT);
            if (value.getCountry() != null && !value.getCountry().isBlank()) {
                languageTag += "-" + value.getCountry().toLowerCase(Locale.ROOT);
            }
        }
        return languageTag;
    }
}
