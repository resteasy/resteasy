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
 * CacheControl.java
 *
 * Created on March 5, 2007, 3:36 PM
 */

package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstraction for the value of a HTTP Cache-Control response header.
 *
 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">HTTP/1.1 section 14.9</a>
 */
public class CacheControl
{
   private boolean _public;
   private boolean _private;
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

   private static final HeaderDelegate<CacheControl> delegate =
           RuntimeDelegate.getInstance().createHeaderDelegate(CacheControl.class);


   /**
    * Create a new instance of CacheControl. The new instance will have the
    * following default settings:
    * <p/>
    * <ul>
    * <li>public = true</li>
    * <li>private = false</li>
    * <li>noCache = false</li>
    * <li>noStore = false</li>
    * <li>noTransform = true</li>
    * <li>mustRevalidate = false</li>
    * <li>proxyRevalidate = false</li>
    * <li>An empty list of private fields</li>
    * <li>An empty list of no-cache fields</li>
    * <li>An empty list of cache extensions</li>
    * </ul>
    */
   public CacheControl()
   {
      _public = true;
      _private = false;
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
    */
   public static CacheControl parse(String value) throws IllegalArgumentException
   {
      return delegate.fromString(value);
   }

   /**
    * Corresponds to the must-revalidate cache control directive.
    *
    * @return true if the must-revalidate cache control directive will be included in the
    *         response, false otherwise.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1 section 14.9.4</a>
    */
   public boolean isMustRevalidate()
   {
      return mustRevalidate;
   }

   /**
    * Corresponds to the must-revalidate cache control directive.
    *
    * @param mustRevalidate true if the must-revalidate cache control directive should be included in the
    *                       response, false otherwise.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1 section 14.9.4</a>
    */
   public void setMustRevalidate(boolean mustRevalidate)
   {
      this.mustRevalidate = mustRevalidate;
   }

   /**
    * Corresponds to the proxy-revalidate cache control directive.
    *
    * @return true if the proxy-revalidate cache control directive will be included in the
    *         response, false otherwise.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1 section 14.9.4</a>
    */
   public boolean isProxyRevalidate()
   {
      return proxyRevalidate;
   }

   /**
    * Corresponds to the must-revalidate cache control directive.
    *
    * @param proxyRevalidate true if the proxy-revalidate cache control directive should be included in the
    *                        response, false otherwise.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1 section 14.9.4</a>
    */
   public void setProxyRevalidate(boolean proxyRevalidate)
   {
      this.proxyRevalidate = proxyRevalidate;
   }

   /**
    * Corresponds to the max-age cache control directive.
    *
    * @return the value of the max-age cache control directive, -1 if the directive is disabled.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1 section 14.9.3</a>
    */
   public int getMaxAge()
   {
      return maxAge;
   }

   /**
    * Corresponds to the max-age cache control directive.
    *
    * @param maxAge the value of the max-age cache control directive, a value of -1 will disable the directive.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1 section 14.9.3</a>
    */
   public void setMaxAge(int maxAge)
   {
      this.maxAge = maxAge;
   }

   /**
    * Corresponds to the s-maxage cache control directive.
    *
    * @return the value of the s-maxage cache control directive, -1 if the directive is disabled.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1 section 14.9.3</a>
    */
   public int getSMaxAge()
   {
      return sMaxAge;
   }

   /**
    * Corresponds to the s-maxage cache control directive.
    *
    * @param sMaxAge the value of the s-maxage cache control directive, a value of -1 will disable the directive.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1 section 14.9.3</a>
    */
   public void setSMaxAge(int sMaxAge)
   {
      this.sMaxAge = sMaxAge;
   }

   /**
    * Corresponds to the value of the no-cache cache control directive.
    *
    * @return a mutable list of field-names that will form the value of the no-cache cache control directive.
    *         An empty list results in a bare no-cache directive.
    * @see #isNoCache
    * @see #setNoCache
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
    */
   public List<String> getNoCacheFields()
   {
      if (noCacheFields == null)
         noCacheFields = new ArrayList<String>();
      return noCacheFields;
   }

   /**
    * Corresponds to the no-cache cache control directive.
    *
    * @param noCache true if the no-cache cache control directive should be included in the
    *                response, false otherwise.
    * @see #getNoCacheFields
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
    */
   public void setNoCache(boolean noCache)
   {
      this.noCache = noCache;
   }

   /**
    * Corresponds to the no-cache cache control directive.
    *
    * @return true if the no-cache cache control directive will be included in the
    *         response, false otherwise.
    * @see #getNoCacheFields
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
    */
   public boolean isNoCache()
   {
      return noCache;
   }

   /**
    * Corresponds to the public cache control directive.
    *
    * @return true if the public cache control directive will be included in the
    *         response, false otherwise.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
    */
   public boolean isPublic()
   {
      return _public;
   }

   /**
    * Corresponds to the public cache control directive.
    *
    * @param _public true if the public cache control directive should be included in the
    *                response, false otherwise.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
    */
   public void setPublic(boolean _public)
   {
      this._public = _public;
   }

   /**
    * Corresponds to the private cache control directive.
    *
    * @return true if the private cache control directive will be included in the
    *         response, false otherwise.
    * @see #getPrivateFields
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
    */
   public boolean isPrivate()
   {
      return _private;
   }

   /**
    * Corresponds to the value of the private cache control directive.
    *
    * @return a mutable list of field-names that will form the value of the private cache control directive.
    *         An empty list results in a bare no-cache directive.
    * @see #isPrivate
    * @see #setPrivate
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
    */
   public List<String> getPrivateFields()
   {
      if (privateFields == null)
         privateFields = new ArrayList<String>();
      return privateFields;
   }

   /**
    * Corresponds to the private cache control directive.
    *
    * @param _private true if the private cache control directive should be included in the
    *                 response, false otherwise.
    * @see #getPrivateFields
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
    */
   public void setPrivate(boolean _private)
   {
      this._private = _private;
   }

   /**
    * Corresponds to the no-transform cache control directive.
    *
    * @return true if the no-transform cache control directive will be included in the
    *         response, false otherwise.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.5">HTTP/1.1 section 14.9.5</a>
    */
   public boolean isNoTransform()
   {
      return noTransform;
   }

   /**
    * Corresponds to the no-transform cache control directive.
    *
    * @param noTransform true if the no-transform cache control directive should be included in the
    *                    response, false otherwise.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.5">HTTP/1.1 section 14.9.5</a>
    */
   public void setNoTransform(boolean noTransform)
   {
      this.noTransform = noTransform;
   }

   /**
    * Corresponds to the no-store cache control directive.
    *
    * @return true if the no-store cache control directive will be included in the
    *         response, false otherwise.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.2">HTTP/1.1 section 14.9.2</a>
    */
   public boolean isNoStore()
   {
      return noStore;
   }

   /**
    * Corresponds to the no-store cache control directive.
    *
    * @param noStore true if the no-store cache control directive should be included in the
    *                response, false otherwise.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.2">HTTP/1.1 section 14.9.2</a>
    */
   public void setNoStore(boolean noStore)
   {
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
   public Map<String, String> getCacheExtension()
   {
      if (cacheExtension == null)
         cacheExtension = new HashMap<String, String>();
      return cacheExtension;
   }

   /**
    * Convert the cache control to a string suitable for use as the value of the
    * corresponding HTTP header.
    *
    * @return a stringified cache control
    */
   @Override
   public String toString()
   {
      return delegate.toString(this);
   }
}
