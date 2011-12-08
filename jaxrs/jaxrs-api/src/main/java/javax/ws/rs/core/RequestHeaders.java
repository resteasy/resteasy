/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Set;

/**
 * general-header =
 *      * Cache-Control            ; Section 14.9
 *        Connection               ; Section 14.10
 *        a? Date                  ; Section 14.18
 *        Pragma                   ; Section 14.32
 *        Trailer                  ; Section 14.40
 *        Transfer-Encoding        ; Section 14.41
 *        Upgrade                  ; Section 14.42
 *        Via                      ; Section 14.45
 *        Warning                  ; Section 14.46
 *
 * entity-header  =                                    Req     Res
 *      *  Allow                    ; Section 14.7      +       +
 *      *  Content-Encoding         ; Section 14.11     +       +
 *      *  Content-Language         ; Section 14.12     +       +
 *      a  Content-Length           ; Section 14.13     +       +
 *      -  Content-Location         ; Section 14.14     ?       +
 *         Content-MD5              ; Section 14.15     +       +
 *         Content-Range            ; Section 14.16     -       +
 *      *  Content-Type             ; Section 14.17     +       +
 *      -  Expires                  ; Section 14.21     -       +
 *      -  Last-Modified            ; Section 14.29     -       +
 *      *  extension-header = message-header
 *
 * request-header =
 *         Accept                   ; Section 14.1
 *         Accept-Charset           ; Section 14.2
 *         Accept-Encoding          ; Section 14.3
 *         Accept-Language          ; Section 14.4
 *         Authorization            ; Section 14.8
 *         Expect                   ; Section 14.20
 *         From                     ; Section 14.22
 *         Host                     ; Section 14.23
 *         If-Match                 ; Section 14.24
 *         If-Modified-Since        ; Section 14.25
 *         If-None-Match            ; Section 14.26
 *         If-Range                 ; Section 14.27
 *         If-Unmodified-Since      ; Section 14.28
 *         Max-Forwards             ; Section 14.31
 *         Proxy-Authorization      ; Section 14.34
 *         Range                    ; Section 14.35
 *         Referer                  ; Section 14.36
 *         TE                       ; Section 14.39
 *         User-Agent               ; Section 14.43
 */
/**
 * An injectable interface that provides access to HTTP request header information.
 * This interface can only be injected as part of the request or response processing
 * scope. Alternatively, the interface can be retrieved from a {@link Request} instance
 * via its {@link Request#getHeaders() getHeaders()} method.
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface RequestHeaders {

    // General header getters
    /**
     * Get the allowed HTTP methods from the Allow HTTP header.
     *
     * @return the allowed HTTP methods, all methods will returned as upper case
     *     strings.
     * @since 2.0
     */
    public Set<String> getAllowedMethods();

    /**
     * Get message date.
     *
     * @return the message date, otherwise {@code null} if not present.
     * @since 2.0
     */
    public Date getDate();

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
     *     {@code null} is returned. If the HTTP header is present but has no
     *     value then the empty string is returned. If the HTTP header is present
     *     more than once then the values of joined together and separated by a ','
     *     character.
     * @see #asMap()
     * @see #getHeaderValues(java.lang.String)
     * @since 2.0
     */
    public String getHeader(String name);

    /**
     * Get the map of HTTP message header names to their respective values.
     * The returned map is case-insensitive wrt. keys and is read-only.
     * <p/>
     * Each single header value is converted to String using a
     * {@link javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate} if one is available
     * via {@link javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)}
     * for the header value class or using its {@code toString} method  if a header
     * delegate is not available.
     *
     * @return a read-only map of header names and values.
     * @throws java.lang.IllegalStateException if called outside of the message
     *     processing scope.
     * @see #getHeader(java.lang.String)
     * @see #getHeaderValues(java.lang.String)
     * @since 2.0
     */
    public MultivaluedMap<String, String> asMap();

    /**
     * Get the values of a single HTTP message header. The returned List is read-only.
     * This is a convenience shortcut for {@code asMap().get(name)}.
     * <p/>
     * Each single header value is converted to String using a
     * {@link javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate} if one is available
     * via {@link javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)}
     * for the header value class or using its {@code toString} method  if a header
     * delegate is not available.
     *
     * @param name the header name, case insensitive.
     * @return a read-only list of header values.
     * @throws java.lang.IllegalStateException if called outside of the message
     *     processing scope.
     * @see #asMap()
     * @see #getHeader(java.lang.String)
     * @since 2.0
     */
    public List<String> getHeaderValues(String name);

    /**
     * Get the language of the entity
     * @return the language of the entity or null if not specified
     * @since 2.0
     */
    public Locale getLanguage();

    /**
     * Get Content-Length value.
     *
     * @return Content-Length as integer if present and valid number. In other
     * cases returns -1.
     * @since 2.0
     */
    public int getLength();

    /**
     * Get the media type of the entity
     * @return the media type or null if there is no request entity.
     * @since 2.0
     */
    public MediaType getMediaType();

    // Request-specific header getters
    /**
     * Get a list of media types that are acceptable for the response.
     *
     * @return a read-only list of requested response media types sorted according
     * to their q-value, with highest preference first.
     * @since 2.0
     */
    public List<MediaType> getAcceptableMediaTypes();

    /**
     * Get a list of languages that are acceptable for the response.
     *
     * @return a read-only list of acceptable languages sorted according
     * to their q-value, with highest preference first.
     * @since 2.0
     */
    public List<Locale> getAcceptableLanguages();

    /**
     * Get any cookies that accompanied the request.
     * @return a read-only map of cookie name (String) to Cookie.
     * @since 2.0
     */
    public Map<String, Cookie> getCookies();
}
