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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

/**
 * An abstraction for the value of a HTTP Cache-Control response header.
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">HTTP/1.1 section 14.9</a>
 * @since 1.0
 */
public class CacheControl {

    private static final HeaderDelegate<CacheControl> HEADER_DELEGATE =
            RuntimeDelegate.getInstance().createHeaderDelegate(CacheControl.class);
    private boolean privateFlag;
    private List<String> privateFields;
    private boolean noCache;
    private List<String> noCacheFields;
    private boolean noStore;
    private boolean noTransform;
    private boolean mustRevalidate;
    private boolean proxyRevalidate;
    private int maxAge = -1;
    private int sMaxAge = -1;
    private Map<String, String> cacheExtension;

    /**
     * Create a new instance of CacheControl. The new instance will have the
     * following default settings:
     *
     * <ul>
     * <li>private = false</li>
     * <li>noCache = false</li>
     * <li>noStore = false</li>
     * <li>noTransform = true</li>
     * <li>mustRevalidate = false</li>
     * <li>proxyRevalidate = false</li>
     * <li>An empty list of private fields</li>
     * <li>An empty list of no-cache fields</li>
     * <li>An empty map of cache extensions</li>
     * </ul>
     */
    public CacheControl() {
        privateFlag = false;
        noCache = false;
        noStore = false;
        noTransform = true;
        mustRevalidate = false;
        proxyRevalidate = false;
    }

    /**
     * Creates a new instance of CacheControl by parsing the supplied string.
     *
     * @param value the cache control string
     * @return the newly created CacheControl
     * @throws IllegalArgumentException if the supplied string cannot be parsed
     *                                  or is null
     */
    public static CacheControl valueOf(final String value) {
        return HEADER_DELEGATE.fromString(value);
    }

    /**
     * Corresponds to the must-revalidate cache control directive.
     *
     * @return true if the must-revalidate cache control directive will be included in the
     *         response, false otherwise.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1 section 14.9.4</a>
     */
    public boolean isMustRevalidate() {
        return mustRevalidate;
    }

    /**
     * Corresponds to the must-revalidate cache control directive.
     *
     * @param mustRevalidate true if the must-revalidate cache control directive should be included in the
     *                       response, false otherwise.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1 section 14.9.4</a>
     */
    public void setMustRevalidate(final boolean mustRevalidate) {
        this.mustRevalidate = mustRevalidate;
    }

    /**
     * Corresponds to the proxy-revalidate cache control directive.
     *
     * @return true if the proxy-revalidate cache control directive will be included in the
     *         response, false otherwise.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1 section 14.9.4</a>
     */
    public boolean isProxyRevalidate() {
        return proxyRevalidate;
    }

    /**
     * Corresponds to the must-revalidate cache control directive.
     *
     * @param proxyRevalidate true if the proxy-revalidate cache control directive should be included in the
     *                        response, false otherwise.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1 section 14.9.4</a>
     */
    public void setProxyRevalidate(final boolean proxyRevalidate) {
        this.proxyRevalidate = proxyRevalidate;
    }

    /**
     * Corresponds to the max-age cache control directive.
     *
     * @return the value of the max-age cache control directive, -1 if the directive is disabled.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1 section 14.9.3</a>
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * Corresponds to the max-age cache control directive.
     *
     * @param maxAge the value of the max-age cache control directive, a value of -1 will disable the directive.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1 section 14.9.3</a>
     */
    public void setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * Corresponds to the s-maxage cache control directive.
     *
     * @return the value of the s-maxage cache control directive, -1 if the directive is disabled.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1 section 14.9.3</a>
     */
    public int getSMaxAge() {
        return sMaxAge;
    }

    /**
     * Corresponds to the s-maxage cache control directive.
     *
     * @param sMaxAge the value of the s-maxage cache control directive, a value of -1 will disable the directive.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1 section 14.9.3</a>
     */
    public void setSMaxAge(final int sMaxAge) {
        this.sMaxAge = sMaxAge;
    }

    /**
     * Corresponds to the value of the no-cache cache control directive.
     *
     * @return a mutable list of field-names that will form the value of the no-cache cache control directive.
     *         An empty list results in a bare no-cache directive.
     * @see #isNoCache()
     * @see #setNoCache(boolean)
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
     */
    public List<String> getNoCacheFields() {
        if (noCacheFields == null) {
            noCacheFields = new ArrayList<String>();
        }
        return noCacheFields;
    }

    /**
     * Corresponds to the no-cache cache control directive.
     *
     * @param noCache true if the no-cache cache control directive should be included in the
     *                response, false otherwise.
     * @see #getNoCacheFields()
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
     */
    public void setNoCache(final boolean noCache) {
        this.noCache = noCache;
    }

    /**
     * Corresponds to the no-cache cache control directive.
     *
     * @return true if the no-cache cache control directive will be included in the
     *         response, false otherwise.
     * @see #getNoCacheFields()
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
     */
    public boolean isNoCache() {
        return noCache;
    }

    /**
     * Corresponds to the private cache control directive.
     *
     * @return true if the private cache control directive will be included in the
     *         response, false otherwise.
     * @see #getPrivateFields()
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
     */
    public boolean isPrivate() {
        return privateFlag;
    }

    /**
     * Corresponds to the value of the private cache control directive.
     *
     * @return a mutable list of field-names that will form the value of the private cache control directive.
     *         An empty list results in a bare no-cache directive.
     * @see #isPrivate()
     * @see #setPrivate(boolean)
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
     */
    public List<String> getPrivateFields() {
        if (privateFields == null) {
            privateFields = new ArrayList<String>();
        }
        return privateFields;
    }

    /**
     * Corresponds to the private cache control directive.
     *
     * @param flag true if the private cache control directive should be included in the
     *             response, false otherwise.
     * @see #getPrivateFields()
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
     */
    public void setPrivate(final boolean flag) {
        this.privateFlag = flag;
    }

    /**
     * Corresponds to the no-transform cache control directive.
     *
     * @return true if the no-transform cache control directive will be included in the
     *         response, false otherwise.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.5">HTTP/1.1 section 14.9.5</a>
     */
    public boolean isNoTransform() {
        return noTransform;
    }

    /**
     * Corresponds to the no-transform cache control directive.
     *
     * @param noTransform true if the no-transform cache control directive should be included in the
     *                    response, false otherwise.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.5">HTTP/1.1 section 14.9.5</a>
     */
    public void setNoTransform(final boolean noTransform) {
        this.noTransform = noTransform;
    }

    /**
     * Corresponds to the no-store cache control directive.
     *
     * @return true if the no-store cache control directive will be included in the
     *         response, false otherwise.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.2">HTTP/1.1 section 14.9.2</a>
     */
    public boolean isNoStore() {
        return noStore;
    }

    /**
     * Corresponds to the no-store cache control directive.
     *
     * @param noStore true if the no-store cache control directive should be included in the
     *                response, false otherwise.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.2">HTTP/1.1 section 14.9.2</a>
     */
    public void setNoStore(final boolean noStore) {
        this.noStore = noStore;
    }

    /**
     * Corresponds to a set of extension cache control directives.
     *
     * @return a mutable map of cache control extension names and their values.
     *         If a key has a null value, it will appear as a bare directive. If a key has
     *         a value that contains no whitespace then the directive will appear as
     *         a simple name=value pair. If a key has a value that contains whitespace
     *         then the directive will appear as a quoted name="value" pair.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.6">HTTP/1.1 section 14.9.6</a>
     */
    public Map<String, String> getCacheExtension() {
        if (cacheExtension == null) {
            cacheExtension = new HashMap<String, String>();
        }
        return cacheExtension;
    }

    /**
     * Convert the cache control to a string suitable for use as the value of the
     * corresponding HTTP header.
     *
     * @return a stringified cache control
     */
    @Override
    public String toString() {
        return HEADER_DELEGATE.toString(this);
    }

    /**
     * Generate hash code from cache control properties.
     *
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.privateFlag ? 1 : 0);
        hash = 41 * hash + (this.privateFields != null ? this.privateFields.hashCode() : 0);
        hash = 41 * hash + (this.noCache ? 1 : 0);
        hash = 41 * hash + (this.noCacheFields != null ? this.noCacheFields.hashCode() : 0);
        hash = 41 * hash + (this.noStore ? 1 : 0);
        hash = 41 * hash + (this.noTransform ? 1 : 0);
        hash = 41 * hash + (this.mustRevalidate ? 1 : 0);
        hash = 41 * hash + (this.proxyRevalidate ? 1 : 0);
        hash = 41 * hash + this.maxAge;
        hash = 41 * hash + this.sMaxAge;
        hash = 41 * hash + (this.cacheExtension != null ? this.cacheExtension.hashCode() : 0);
        return hash;
    }

    /**
     * Compares object argument to this cache control to see if they are the same
     * considering all property values.
     *
     * @param obj the object to compare to
     * @return true if the two cache controls are the same, false otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CacheControl other = (CacheControl) obj;
        if (this.privateFlag != other.privateFlag) {
            return false;
        }
        if (this.privateFields != other.privateFields
                && (this.privateFields == null || !this.privateFields.equals(other.privateFields))) {
            return false;
        }
        if (this.noCache != other.noCache) {
            return false;
        }
        if (this.noCacheFields != other.noCacheFields
                && (this.noCacheFields == null || !this.noCacheFields.equals(other.noCacheFields))) {
            return false;
        }
        if (this.noStore != other.noStore) {
            return false;
        }
        if (this.noTransform != other.noTransform) {
            return false;
        }
        if (this.mustRevalidate != other.mustRevalidate) {
            return false;
        }
        if (this.proxyRevalidate != other.proxyRevalidate) {
            return false;
        }
        if (this.maxAge != other.maxAge) {
            return false;
        }
        if (this.sMaxAge != other.sMaxAge) {
            return false;
        }
        if (this.cacheExtension != other.cacheExtension
                && (this.cacheExtension == null || !this.cacheExtension.equals(other.cacheExtension))) {
            return false;
        }
        return true;
    }
}
