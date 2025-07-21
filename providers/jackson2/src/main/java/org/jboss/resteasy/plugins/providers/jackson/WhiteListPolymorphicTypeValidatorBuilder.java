package org.jboss.resteasy.plugins.providers.jackson;

import java.util.StringTokenizer;

import org.jboss.resteasy.spi.config.Configuration;
import org.jboss.resteasy.spi.config.ConfigurationFactory;

import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

public class WhiteListPolymorphicTypeValidatorBuilder extends BasicPolymorphicTypeValidator.Builder {
    private static final long serialVersionUID = 464558058341488449L;
    // The documentation does not indicate the ".prefix" part of the property, see RESTEASY-3174. For this reason we're
    // going to allow both the .prefix and non-".prefix" versions.
    private static final String BASE_TYPE_PROP = "resteasy.jackson.deserialization.whitelist.allowIfBaseType";
    private static final String SUB_TYPE_PROP = "resteasy.jackson.deserialization.whitelist.allowIfSubType";

    public WhiteListPolymorphicTypeValidatorBuilder() {
        super();
        String allowIfBaseType = getProperty(BASE_TYPE_PROP);
        if (allowIfBaseType != null) {
            StringTokenizer st = new StringTokenizer(allowIfBaseType, ",", false);
            while (st.hasMoreTokens()) {
                String t = st.nextToken();
                allowIfBaseType("*".equals(t) ? "" : t);
            }
        }
        String allowIfSubType = getProperty(SUB_TYPE_PROP);
        if (allowIfSubType != null) {
            StringTokenizer st = new StringTokenizer(allowIfSubType, ",", false);
            while (st.hasMoreTokens()) {
                String t = st.nextToken();
                allowIfSubType("*".equals(t) ? "" : t);
            }
        }
    }

    private static String getProperty(final String name) {
        final Configuration config = ConfigurationFactory.getInstance().getConfiguration();
        return config.getOptionalValue(name, String.class)
                .or(() -> config.getOptionalValue(name + ".prefix", String.class))
                .orElse(null);
    }
}
