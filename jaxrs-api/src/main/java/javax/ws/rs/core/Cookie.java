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
 * Cookie.java
 *
 * Created on March 12, 2007, 5:01 PM
 *
 */

package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

/**
 * Represents the value of a HTTP cookie, transferred in a request.
 * RFC 2109 specifies the legal characters for name,
 * value, path and domain. The default version of 1 corresponds to RFC 2109.
 *
 * @see <a href="http://www.ietf.org/rfc/rfc2109.txt">IETF RFC 2109</a>
 */
public class Cookie
{

   /**
    * Cookies using the default version correspond to RFC 2109.
    */
   public static final int DEFAULT_VERSION = 1;

   private static final HeaderDelegate<Cookie> delegate =
           RuntimeDelegate.getInstance().createHeaderDelegate(Cookie.class);

   private String name;
   private String value;
   private int version = DEFAULT_VERSION;
   private String path = null;
   private String domain = null;

   /**
    * Create a new instance.
    *
    * @param name    the name of the cookie
    * @param value   the value of the cookie
    * @param path    the URI path for which the cookie is valid
    * @param domain  the host domain for which the cookie is valid
    * @param version the version of the specification to which the cookie complies
    */
   public Cookie(String name, String value, String path, String domain, int version)
   {
      this.name = name;
      this.value = value;
      this.version = version;
      this.domain = domain;
      this.path = path;
   }

   /**
    * Create a new instance.
    *
    * @param name   the name of the cookie
    * @param value  the value of the cookie
    * @param path   the URI path for which the cookie is valid
    * @param domain the host domain for which the cookie is valid
    */
   public Cookie(String name, String value, String path, String domain)
   {
      this.name = name;
      this.value = value;
      this.domain = domain;
      this.path = path;
   }

   /**
    * Create a new instance.
    *
    * @param name  the name of the cookie
    * @param value the value of the cookie
    */
   public Cookie(String name, String value)
   {
      this.name = name;
      this.value = value;
   }

   /**
    * Creates a new instance of Cookie by parsing the supplied string.
    *
    * @param value the cookie string
    * @return the newly created Cookie
    * @throws IllegalArgumentException if the supplied string cannot be parsed
    */
   public static Cookie valueOf(String value) throws IllegalArgumentException
   {
      return delegate.fromString(value);
   }

   /**
    * Get the name of the cookie
    *
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * Get the value of the cookie
    *
    * @return the value
    */
   public String getValue()
   {
      return value;
   }

   /**
    * Get the version of the cookie
    *
    * @return the version
    */
   public int getVersion()
   {
      return version;
   }

   /**
    * Get the domain of the cookie
    *
    * @return the domain
    */
   public String getDomain()
   {
      return domain;
   }

   /**
    * Get the path of the cookie
    *
    * @return the path
    */
   public String getPath()
   {
      return path;
   }

   /**
    * Convert the cookie to a string suitable for use as the value of the
    * corresponding HTTP header.
    *
    * @return a stringified cookie
    */
   @Override
   public String toString()
   {
      return delegate.toString(this);
   }
}
