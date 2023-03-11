package org.jboss.resteasy.specimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.util.CookieParser;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.WeightedLanguage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyHttpHeaders implements HttpHeaders {
    private static final List<MediaType> MEDIA_WILDCARD = List.of(MediaType.WILDCARD_TYPE);
    private static final List<Locale> LANGUAGE_WILDCARD = List.of(Locale.ROOT);

    private static final Map<String, List<MediaType>> mediaTypeCache;
    private static final Map<String, List<Locale>> languageCache;
    static {
        mediaTypeCache = Map.ofEntries(
                Map.entry(MediaType.APPLICATION_ATOM_XML_TYPE.toString(), List.of(MediaType.APPLICATION_ATOM_XML_TYPE)),
                Map.entry(MediaType.APPLICATION_FORM_URLENCODED_TYPE.toString(),
                        List.of(MediaType.APPLICATION_FORM_URLENCODED_TYPE)),
                Map.entry(MediaType.APPLICATION_JSON_PATCH_JSON_TYPE.toString(),
                        List.of(MediaType.APPLICATION_JSON_PATCH_JSON_TYPE)),
                Map.entry(MediaType.APPLICATION_JSON_TYPE.toString(), List.of(MediaType.APPLICATION_JSON_TYPE)),
                Map.entry(MediaType.APPLICATION_OCTET_STREAM_TYPE.toString(), List.of(MediaType.APPLICATION_OCTET_STREAM_TYPE)),
                Map.entry(MediaType.APPLICATION_SVG_XML_TYPE.toString(), List.of(MediaType.APPLICATION_SVG_XML_TYPE)),
                Map.entry(MediaType.APPLICATION_XHTML_XML_TYPE.toString(), List.of(MediaType.APPLICATION_XHTML_XML_TYPE)),
                Map.entry(MediaType.APPLICATION_XML_TYPE.toString(), List.of(MediaType.APPLICATION_XML_TYPE)),
                Map.entry(MediaType.MULTIPART_FORM_DATA_TYPE.toString(), List.of(MediaType.MULTIPART_FORM_DATA_TYPE)),
                Map.entry(MediaType.SERVER_SENT_EVENTS_TYPE.toString(), List.of(MediaType.SERVER_SENT_EVENTS_TYPE)),
                Map.entry(MediaType.TEXT_HTML_TYPE.toString(), List.of(MediaType.TEXT_HTML_TYPE)),
                Map.entry(MediaType.TEXT_PLAIN_TYPE.toString(), List.of(MediaType.TEXT_PLAIN_TYPE)),
                Map.entry(MediaType.TEXT_XML_TYPE.toString(), List.of(MediaType.TEXT_XML_TYPE)),
                Map.entry(MediaType.WILDCARD_TYPE.toString(), MEDIA_WILDCARD));

        languageCache = Map.ofEntries(
                Map.entry(Locale.CHINESE.toString(), List.of(Locale.CHINESE)),
                Map.entry(Locale.ENGLISH.toString(), List.of(Locale.ENGLISH)),
                Map.entry(Locale.FRENCH.toString(), List.of(Locale.FRENCH)),
                Map.entry(Locale.GERMAN.toString(), List.of(Locale.GERMAN)),
                Map.entry(Locale.ITALIAN.toString(), List.of(Locale.ITALIAN)),
                Map.entry(Locale.JAPANESE.toString(), List.of(Locale.JAPANESE)),
                Map.entry(Locale.KOREAN.toString(), List.of(Locale.KOREAN)),
                Map.entry(Locale.SIMPLIFIED_CHINESE.toString(), List.of(Locale.SIMPLIFIED_CHINESE)),
                Map.entry(Locale.TRADITIONAL_CHINESE.toString(), List.of(Locale.TRADITIONAL_CHINESE)),
                Map.entry("", LANGUAGE_WILDCARD));
    }

    private final MultivaluedMap<String, String> requestHeaders;
    private final MultivaluedMap<String, String> unmodifiableRequestHeaders;
    private Map<String, Cookie> cookies;

    public ResteasyHttpHeaders(final MultivaluedMap<String, String> requestHeaders) {
        this(requestHeaders, new HashMap<String, Cookie>());
    }

    public ResteasyHttpHeaders(final MultivaluedMap<String, String> requestHeaders, final boolean eagerlyInitializeEntrySet) {
        this(requestHeaders, new HashMap<String, Cookie>(), eagerlyInitializeEntrySet);
    }

    public ResteasyHttpHeaders(final MultivaluedMap<String, String> requestHeaders, final Map<String, Cookie> cookies) {
        this(requestHeaders, cookies, true);
    }

    public ResteasyHttpHeaders(final MultivaluedMap<String, String> requestHeaders, final Map<String, Cookie> cookies,
            final boolean eagerlyInitializeEntrySet) {
        this.requestHeaders = requestHeaders;
        this.unmodifiableRequestHeaders = new UnmodifiableMultivaluedMap<>(requestHeaders, eagerlyInitializeEntrySet);
        this.cookies = (cookies == null ? new HashMap<>() : cookies);
    }

    @Override
    public MultivaluedMap<String, String> getRequestHeaders() {
        return unmodifiableRequestHeaders;
    }

    public MultivaluedMap<String, String> getMutableHeaders() {
        return requestHeaders;
    }

    public void testParsing() {
        // test parsing should throw an exception on error
        getAcceptableMediaTypes();
        getMediaType();
        getLanguage();
        getAcceptableLanguages();

    }

    @Override
    public List<String> getRequestHeader(String name) {
        List<String> vals = unmodifiableRequestHeaders.get(name);
        return vals == null ? Collections.<String> emptyList() : vals;
    }

    @Override
    public Map<String, Cookie> getCookies() {
        mergeCookies();
        return Collections.unmodifiableMap(cookies);
    }

    public Map<String, Cookie> getMutableCookies() {
        mergeCookies();
        return cookies;
    }

    public void setCookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
    }

    @Override
    public Date getDate() {
        String date = requestHeaders.getFirst(DATE);
        if (date == null)
            return null;
        return DateUtil.parseDate(date);
    }

    @Override
    public String getHeaderString(String name) {
        List<String> vals = requestHeaders.get(name);
        if (vals == null)
            return null;
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String val : vals) {
            if (first)
                first = false;
            else
                builder.append(",");
            builder.append(val);
        }
        return builder.toString();
    }

    @Override
    public Locale getLanguage() {
        String obj = requestHeaders.getFirst(HttpHeaders.CONTENT_LANGUAGE);
        if (obj == null)
            return null;
        return new Locale(obj);
    }

    @Override
    public int getLength() {
        String obj = requestHeaders.getFirst(HttpHeaders.CONTENT_LENGTH);
        if (obj == null)
            return -1;
        return Integer.parseInt(obj);
    }

    // because header string map is mutable, we only cache the parsed media type
    // and still do hash lookup
    private String cachedMediaTypeString;
    private MediaType cachedMediaType;

    @Override
    public MediaType getMediaType() {
        String obj = requestHeaders.getFirst(HttpHeaders.CONTENT_TYPE);
        if (obj == null)
            return null;
        if (obj == cachedMediaTypeString)
            return cachedMediaType;
        cachedMediaTypeString = obj;
        cachedMediaType = MediaType.valueOf(obj);
        return cachedMediaType;
    }

    @Override
    public List<MediaType> getAcceptableMediaTypes() {
        List<String> vals = requestHeaders.get(ACCEPT);
        if (vals == null || vals.isEmpty()) {
            return MEDIA_WILDCARD;
        }
        // Choose a standard entry if available
        if (vals.size() == 1) {
            final String type = vals.get(0).trim();
            if (MediaType.WILDCARD.equals(type)) {
                return MEDIA_WILDCARD;
            } else {
                List<MediaType> standard = mediaTypeCache.get(type);
                if (standard != null) {
                    return standard;
                }
            }
        }
        final List<MediaType> list = new ArrayList<>();
        for (String v : vals) {
            final StringTokenizer tokenizer = new StringTokenizer(v, ",");
            while (tokenizer.hasMoreElements()) {
                final String item = tokenizer.nextToken().trim();
                list.add(MediaType.valueOf(item));
            }
        }
        MediaTypeHelper.sortByWeight(list);
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<Locale> getAcceptableLanguages() {
        List<String> vals = requestHeaders.get(ACCEPT_LANGUAGE);
        if (vals == null || vals.isEmpty()) {
            return LANGUAGE_WILDCARD;
        }
        // Check for standard entries
        if (vals.size() == 1) {
            final String type = vals.get(0);
            if (type.isBlank()) {
                return LANGUAGE_WILDCARD;
            } else {
                final List<Locale> standard = languageCache.get(type);
                if (standard != null) {
                    return standard;
                }
            }
        }
        List<WeightedLanguage> languages = new ArrayList<WeightedLanguage>();
        for (String v : vals) {
            StringTokenizer tokenizer = new StringTokenizer(v, ",");
            while (tokenizer.hasMoreElements()) {
                String item = tokenizer.nextToken().trim();
                languages.add(WeightedLanguage.parse(item));
            }
        }
        Collections.sort(languages);
        List<Locale> list = new ArrayList<Locale>(languages.size());
        for (WeightedLanguage language : languages)
            list.add(language.getLocale());
        return Collections.unmodifiableList(list);
    }

    private void mergeCookies() {
        List<String> cookieHeader = requestHeaders.get(HttpHeaders.COOKIE);
        if (cookieHeader != null && !cookieHeader.isEmpty()) {
            for (String s : cookieHeader) {
                List<Cookie> list = CookieParser.parseCookies(s);
                for (Cookie cookie : list) {
                    cookies.put(cookie.getName(), cookie);
                }
            }
        }
    }
}
