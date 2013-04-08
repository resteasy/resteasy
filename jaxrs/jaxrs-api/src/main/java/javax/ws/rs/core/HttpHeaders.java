/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.ws.rs.core;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An injectable interface that provides access to HTTP header information.
 * All methods throw {@link java.lang.IllegalStateException} if called outside the
 * scope of a request (e.g. from a provider constructor).
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @see Context
 * @since 1.0
 */
public interface HttpHeaders {

    /**
     * Get the values of a HTTP request header. The returned List is read-only.
     * This is a shortcut for {@code getRequestHeaders().get(name)}.
     *
     * @param name the header name, case insensitive.
     * @return a read-only list of header values.
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request.
     */
    public List<String> getRequestHeader(String name);

    /**
     * Get a HTTP header as a single string value.
     * <p/>
     * Each single header value is converted to String using a
     * {@link javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate} if one is available
     * via {@link javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)}
     * for the header value class or using its {@code toString} method  if a header
     * delegate is not available.
     *
     * @param name the HTTP header.
     * @return the HTTP header value. If the HTTP header is not present then
     *         {@code null} is returned. If the HTTP header is present but has no
     *         value then the empty string is returned. If the HTTP header is present
     *         more than once then the values of joined together and separated by a ','
     *         character.
     * @see #getRequestHeader(java.lang.String)
     * @since 2.0
     */
    public String getHeaderString(String name);


    /**
     * Get the values of HTTP request headers. The returned Map is case-insensitive
     * wrt. keys and is read-only. The method never returns {@code null}.
     *
     * @return a read-only map of header names and values.
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request.
     */
    public MultivaluedMap<String, String> getRequestHeaders();

    /**
     * Get a list of media types that are acceptable for the response.
     * <p/>
     * If no acceptable media types are specified, a read-only list containing
     * a single {@link javax.ws.rs.core.MediaType#WILDCARD_TYPE wildcard media type}
     * instance is returned.
     *
     * @return a read-only list of requested response media types sorted according
     *         to their q-value, with highest preference first.
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request.
     */
    public List<MediaType> getAcceptableMediaTypes();

    /**
     * Get a list of languages that are acceptable for the response.
     * <p/>
     * If no acceptable languages are specified, a read-only list containing
     * a single wildcard {@link java.util.Locale} instance (with language field
     * set to "{@code *}") is returned.
     *
     * @return a read-only list of acceptable languages sorted according
     *         to their q-value, with highest preference first.
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request.
     */
    public List<Locale> getAcceptableLanguages();

    /**
     * Get the media type of the request entity.
     *
     * @return the media type or {@code null} if there is no request entity.
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request.
     */
    public MediaType getMediaType();

    /**
     * Get the language of the request entity.
     *
     * @return the language of the entity or {@code null} if not specified.
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request.
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
     * Get message date.
     *
     * @return the message date, otherwise {@code null} if not present.
     * @since 2.0
     */
    public Date getDate();

    /**
     * Get Content-Length value.
     *
     * @return Content-Length as integer if present and valid number. In other
     *         cases returns -1.
     * @since 2.0
     */
    public int getLength();

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
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.7">HTTP/1.1 documentation</a>}.
     */
    public static final String ALLOW = "Allow";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.8">HTTP/1.1 documentation</a>}.
     */
    public static final String AUTHORIZATION = "Authorization";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">HTTP/1.1 documentation</a>}.
     */
    public static final String CACHE_CONTROL = "Cache-Control";
    /**
     * See {@link <a href="http://tools.ietf.org/html/rfc2183">IETF RFC-2183</a>}.
     */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.11">HTTP/1.1 documentation</a>}.
     */
    public static final String CONTENT_ENCODING = "Content-Encoding";
    /**
     * See {@link <a href="http://tools.ietf.org/html/rfc2392">IETF RFC-2392</a>}.
     */
    public static final String CONTENT_ID = "Content-ID";
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
     * See {@link <a href="http://tools.ietf.org/html/rfc5988#page-6">Web Linking (IETF RFC-5988) documentation</a>}.
     */
    public static final String LINK = "Link";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.37">HTTP/1.1 documentation</a>}.
     */
    public static final String RETRY_AFTER = "Retry-After";
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
