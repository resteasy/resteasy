/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 * 
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.php
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */
/*
 * HttpHeaders.java
 *
 * Created on April 13, 2007, 3:00 PM
 *
 */

package javax.ws.rs.core;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An injectable interface that provides access to HTTP header information.
 * All methods throw java.lang.IllegalStateException if called outside the scope of a request
 * (e.g. from a provider constructor).
 *
 * @see Context
 */
public interface HttpHeaders
{

   /**
    * Get the values of a HTTP request header. The returned List is read-only.
    * This is a shortcut for <code>getRequestHeaders().get(name)</code>.
    *
    * @param name the header name, case insensitive
    * @return a read-only list of header values.
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public List<String> getRequestHeader(String name);

   /**
    * Get the values of HTTP request headers. The returned Map is case-insensitive
    * wrt keys and is read-only.
    *
    * @return a read-only map of header names and values.
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public MultivaluedMap<String, String> getRequestHeaders();

   /**
    * Get a list of media types that are acceptable for the response.
    *
    * @return a read-only list of requested response media types sorted according
    *         to their q-value, with highest preference first.
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public List<MediaType> getAcceptableMediaTypes();

   /**
    * Get a list of languages that are acceptable for the response.
    *
    * @return a read-only list of acceptable languages sorted according
    *         to their q-value, with highest preference first.
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public List<Locale> getAcceptableLanguages();

   /**
    * Get the media type of the request entity
    *
    * @return the media type or null if there is no request entity.
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public MediaType getMediaType();

   /**
    * Get the language of the request entity
    *
    * @return the language of the entity or null if not specified
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public Locale getLanguage();

   /**
    * Get any cookies that accompanied the request.
    *
    * @return a read-only map of cookie name (String) to Cookie.
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public Map<String, Cookie> getCookies();

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1">HTTP/1.1 documentation</a>}.
    */
   public static final String ACCEPT = "Accept";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.2">HTTP/1.1 documentation</a>}.
    */
   public static final String ACCEPT_CHARSET = "Accept-Charset";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.3">HTTP/1.1 documentation</a>}.
    */
   public static final String ACCEPT_ENCODING = "Accept-Encoding";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4">HTTP/1.1 documentation</a>}.
    */
   public static final String ACCEPT_LANGUAGE = "Accept-Language";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.8">HTTP/1.1 documentation</a>}.
    */
   public static final String AUTHORIZATION = "Authorization";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">HTTP/1.1 documentation</a>}.
    */
   public static final String CACHE_CONTROL = "Cache-Control";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.11">HTTP/1.1 documentation</a>}.
    */
   public static final String CONTENT_ENCODING = "Content-Encoding";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.12">HTTP/1.1 documentation</a>}.
    */
   public static final String CONTENT_LANGUAGE = "Content-Language";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.13">HTTP/1.1 documentation</a>}.
    */
   public static final String CONTENT_LENGTH = "Content-Length";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.14">HTTP/1.1 documentation</a>}.
    */
   public static final String CONTENT_LOCATION = "Content-Location";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">HTTP/1.1 documentation</a>}.
    */
   public static final String CONTENT_TYPE = "Content-Type";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.18">HTTP/1.1 documentation</a>}.
    */
   public static final String DATE = "Date";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19">HTTP/1.1 documentation</a>}.
    */
   public static final String ETAG = "ETag";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.21">HTTP/1.1 documentation</a>}.
    */
   public static final String EXPIRES = "Expires";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.23">HTTP/1.1 documentation</a>}.
    */
   public static final String HOST = "Host";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.24">HTTP/1.1 documentation</a>}.
    */
   public static final String IF_MATCH = "If-Match";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.25">HTTP/1.1 documentation</a>}.
    */
   public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.26">HTTP/1.1 documentation</a>}.
    */
   public static final String IF_NONE_MATCH = "If-None-Match";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.28">HTTP/1.1 documentation</a>}.
    */
   public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.29">HTTP/1.1 documentation</a>}.
    */
   public static final String LAST_MODIFIED = "Last-Modified";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.30">HTTP/1.1 documentation</a>}.
    */
   public static final String LOCATION = "Location";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.43">HTTP/1.1 documentation</a>}.
    */
   public static final String USER_AGENT = "User-Agent";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.44">HTTP/1.1 documentation</a>}.
    */
   public static final String VARY = "Vary";

   /**
    * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.47">HTTP/1.1 documentation</a>}.
    */
   public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

   /**
    * See {@link <a href="http://www.ietf.org/rfc/rfc2109.txt">IETF RFC 2109</a>}.
    */
   public static final String COOKIE = "Cookie";

   /**
    * See {@link <a href="http://www.ietf.org/rfc/rfc2109.txt">IETF RFC 2109</a>}.
    */
   public static final String SET_COOKIE = "Set-Cookie";

}
