package org.resteasy;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpHeadersImpl implements HttpHeaders {

    private MultivaluedMap<String, String> requestHeaders;
    private List<MediaType> acceptableMediaTypes;
    private MediaType mediaType;
    private String language;
    private List<Cookie> cookies;

    public MultivaluedMap<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(MultivaluedMap<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public List<MediaType> getAcceptableMediaTypes() {
        return acceptableMediaTypes;
    }

    public void setAcceptableMediaTypes(List<MediaType> acceptableMediaTypes) {
        this.acceptableMediaTypes = acceptableMediaTypes;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

}
