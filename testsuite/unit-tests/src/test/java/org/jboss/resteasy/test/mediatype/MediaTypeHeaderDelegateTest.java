package org.jboss.resteasy.test.mediatype;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MediaTypeHeaderDelegateTest {

    private MediaTypeHeaderDelegate delegate;

    @BeforeEach
    public void setUp() throws Exception {
        delegate = new MediaTypeHeaderDelegate();

        // We need to clear the cache here since the cache is static and will be reused between tests.
        // This will not work if these tests are run in parallel.
        MediaTypeHeaderDelegate.clearCache();
    }

    @Test
    public void testParseStripsTrailingSemicolonWhenParsingDodgyContentType() {
        MediaTypeHeaderDelegate.parse("application/json;");

        assertEquals("application/json", delegate.toString(new MediaType("application", "json")));
    }

    @Test
    public void testParseHandlesMediaTypeWithCharset() {
        MediaTypeHeaderDelegate.parse("application/json; charset=utf-8");

        assertEquals("application/json;charset=utf-8", delegate.toString(new MediaType("application", "json", "utf-8")));
    }

    @Test
    public void testParseHandlesMediaTypeWithCharsetAndAnotherParameter() {
        MediaTypeHeaderDelegate.parse("application/json; charset=utf-8; a=bobbytables");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("charset", "utf-8");
        parameters.put("a", "bobbytables");
        assertEquals("application/json;a=bobbytables;charset=utf-8",
                delegate.toString(new MediaType("application", "json", parameters)));
    }

    @Test
    public void testParseHandlesMediaTypeWithCharsetAndAnotherParameterAndTrailingSemiColon() {
        MediaTypeHeaderDelegate.parse("application/json; charset=utf-8; a=bobbytables;");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("charset", "utf-8");
        parameters.put("a", "bobbytables");
        assertEquals("application/json;a=bobbytables;charset=utf-8",
                delegate.toString(new MediaType("application", "json", parameters)));
    }

    @Test
    public void initialisedDelegateCacheShouldNotBePoisonedByMediaTypeWithTrailingSemiColon() {
        // Seed the cache by asking to parse a valid
        MediaTypeHeaderDelegate.parse("application/json");

        // Verify that getting retrieving the media type returns the expected media type
        assertEquals("application/json", delegate.toString(new MediaType("application", "json")));

        // Poison the cache with a trailing semi-colon
        MediaTypeHeaderDelegate.parse("application/json;");

        // Verify that getting retrieving the media type returns the expected media type
        assertEquals("application/json", delegate.toString(new MediaType("application", "json")));
    }
}
