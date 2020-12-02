package org.jboss.resteasy.test.providers;

import org.jboss.resteasy.plugins.providers.jsonb.JsonBindingProvider;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import jakarta.ws.rs.core.MediaType;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Check when ResteasyConfiguration.class is null in JsonBindingProvider
 * system property is checked and the property is set accordingly.
 * There are integration tests that check the properly setting when
 * a ResteasyConfiguration.class is provided.
 */
public class JsonBindingTest {
    private MediaType mediaType = new MediaType("application", "json");

    @Test
    public void testUseJackson2() throws Exception
    {
        String origValue = System.getProperty(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB);

        System.setProperty(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true");
        JsonBindingProvider jbp = new JsonBindingProvider();
        boolean result = jbp.isReadable(null, null, null, mediaType);

        if (origValue == null) {
            System.setProperty(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "");
        } else {
            System.setProperty(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, origValue);
        }
        assertFalse(result);
    }

    @Test
    public void testUseJSONB() throws Exception
    {
        JsonBindingProvider jbp = new JsonBindingProvider();
        boolean result = jbp.isReadable(null, null, null, mediaType);
        assertTrue(result);
    }
}
