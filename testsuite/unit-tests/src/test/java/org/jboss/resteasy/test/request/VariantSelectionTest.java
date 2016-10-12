package org.jboss.resteasy.test.request;

import org.jboss.resteasy.core.request.ServerDrivenNegotiation;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @tpSubChapter Requests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for ServerDrivenNegotiation class.
 * @tpSince RESTEasy 3.0.16
 */
public class VariantSelectionTest {

    final String ERROR_MSG = "Wrong media type";

    @Test
    public void mostSpecific() {
        ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptHeaders(Arrays.asList("text/plain"));
        negotiation.setAcceptCharsetHeaders(Arrays.asList(StandardCharsets.UTF_8.name()));
        negotiation.setAcceptEncodingHeaders(Arrays.asList("gzip"));
        negotiation.setAcceptLanguageHeaders(Arrays.asList("en-gb"));

        MediaType mediaTypeWithCharset = MediaType.valueOf("text/plain; charset=UTF-8");
        MediaType mediaType = MediaType.valueOf("text/plain");
        String encoding = "gzip";
        Locale locale = Locale.UK;

        List<Variant> available = new ArrayList<Variant>();
        available.add(new Variant(mediaTypeWithCharset, (String) null, (String) null));
        available.add(new Variant(mediaTypeWithCharset, locale, null));
        available.add(new Variant(mediaTypeWithCharset, (String) null, encoding));
        available.add(new Variant(mediaTypeWithCharset, locale, encoding));
        available.add(new Variant(mediaType, (String) null, (String) null));
        available.add(new Variant(mediaType, locale, null));
        available.add(new Variant(mediaType, (String) null, encoding));
        available.add(new Variant(mediaType, locale, encoding));
        available.add(new Variant(null, locale, null));
        available.add(new Variant(null, locale, encoding));
        available.add(new Variant((MediaType) null, (String) null, encoding));

        // Assert all acceptable:
        for (Variant variant : available) {
            assertEquals(variant, negotiation.getBestMatch(Arrays.asList(variant)));
        }

        Variant best = negotiation.getBestMatch(available);
        assertNotNull("Variant should not be null", best);
        assertEquals("Wrong media type", mediaTypeWithCharset, best.getMediaType());
        assertEquals("Wrong encoding", encoding, best.getEncoding());
        assertEquals("Wrong locale", locale, best.getLanguage());
    }


    @Test
    public void mostSpecificMediaType() {
        String header = "text/*, text/html, text/html;level=1, */*";
        ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptHeaders(Arrays.asList(header));

        Variant o1 = new Variant(MediaType.valueOf("text/html;level=1"), (String) null, null);
        Variant o2 = new Variant(MediaType.valueOf("text/html"), (String) null, null);
        Variant o3 = new Variant(MediaType.valueOf("text/*"), (String) null, null);
        Variant o4 = new Variant(MediaType.valueOf("*/*"), (String) null, null);

        List<Variant> available = new ArrayList<>();
        available.add(o4);
        assertEquals(ERROR_MSG, o4, negotiation.getBestMatch(available));
        available.add(o3);
        assertEquals(ERROR_MSG, o3, negotiation.getBestMatch(available));
        available.add(o2);
        assertEquals(ERROR_MSG, o2, negotiation.getBestMatch(available));
        available.add(o1);
        assertEquals(ERROR_MSG, o1, negotiation.getBestMatch(available));
    }


    @Test
    public void mediaTypeQualityFactor() {

        String header1 = "text/*;q=0.3, text/html;q=0.7, text/html;level=1";
        String header2 = "text/html;level=2;q=0.4, */*;q=0.5";
        ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptHeaders(Arrays.asList(header1, header2));
        negotiation.setAcceptLanguageHeaders(Arrays.asList("en"));

        Variant q03 = new Variant(MediaType.valueOf("text/plain"), (String) null, null);
        Variant q04 = new Variant(MediaType.valueOf("text/html;level=2"), (String) null, null);
        Variant q05 = new Variant(MediaType.valueOf("image/jpeg"), (String) null, null);
        Variant q07 = new Variant(MediaType.valueOf("text/html"), (String) null, null);
        Variant q07plus = new Variant(MediaType.valueOf("text/html;level=3"), (String) null, null);
        Variant q10 = new Variant(MediaType.valueOf("text/html;level=1"), (String) null, null);

        List<Variant> available = new ArrayList<Variant>();
        available.add(q03);
        assertEquals(ERROR_MSG, q03, negotiation.getBestMatch(available));
        available.add(q04);
        assertEquals(ERROR_MSG, q04, negotiation.getBestMatch(available));
        available.add(q05);
        assertEquals(ERROR_MSG, q05, negotiation.getBestMatch(available));
        available.add(q07);
        assertEquals(ERROR_MSG, q07, negotiation.getBestMatch(available));
        available.add(q07plus);
        assertEquals(ERROR_MSG, q07plus, negotiation.getBestMatch(available));
        available.add(q10);
        assertEquals(ERROR_MSG, q10, negotiation.getBestMatch(available));
    }

}
