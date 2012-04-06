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
 * NewCookie.java
 *
 * Created on March 12, 2007, 5:08 PM
 *
 */

package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

/**
 * Used to create a new HTTP cookie, transferred in a response.
 *
 * @see <a href="http://www.ietf.org/rfc/rfc2109.txt">IETF RFC 2109</a>
 */
public class NewCookie extends Cookie
{

   /**
    * Specifies that the cookie expires with the current application/browser session.
    */
   public static final int DEFAULT_MAX_AGE = -1;

   private static final HeaderDelegate<NewCookie> delegate =
           RuntimeDelegate.getInstance().createHeaderDelegate(NewCookie.class);

   private String comment = null;
   private int maxAge = DEFAULT_MAX_AGE;
   private boolean secure = false;

   /**
    * Create a new instance.
    *
    * @param name  the name of the cookie
    * @param value the value of the cookie
    * @throws IllegalArgumentException if name is null
    */
   public NewCookie(String name, String value)
   {
      super(name, value);
   }

   /**
    * Create a new instance.
    *
    * @param name    the name of the cookie
    * @param value   the value of the cookie
    * @param path    the URI path for which the cookie is valid
    * @param domain  the host domain for which the cookie is valid
    * @param comment the comment
    * @param maxAge  the maximum age of the cookie in seconds
    * @param secure  specifies whether the cookie will only be sent over a secure connection
    * @throws IllegalArgumentException if name is null
    */
   public NewCookie(String name, String value, String path, String domain, String comment, int maxAge, boolean secure)
   {
      super(name, value, path, domain);
      this.comment = comment;
      this.maxAge = maxAge;
      this.secure = secure;
   }

   /**
    * Create a new instance.
    *
    * @param name    the name of the cookie
    * @param value   the value of the cookie
    * @param path    the URI path for which the cookie is valid
    * @param domain  the host domain for which the cookie is valid
    * @param version the version of the specification to which the cookie complies
    * @param comment the comment
    * @param maxAge  the maximum age of the cookie in seconds
    * @param secure  specifies whether the cookie will only be sent over a secure connection
    * @throws IllegalArgumentException if name is null
    */
   public NewCookie(String name, String value, String path, String domain, int version, String comment, int maxAge, boolean secure)
   {
      super(name, value, path, domain, version);
      this.comment = comment;
      this.maxAge = maxAge;
      this.secure = secure;
   }

   /**
    * Create a new instance copying the information in the supplied cookie.
    *
    * @param cookie the cookie to clone
    * @throws IllegalArgumentException if cookie is null
    */
   public NewCookie(Cookie cookie)
   {
      super(cookie == null ? null : cookie.getName(),
              cookie == null ? null : cookie.getValue(),
              cookie == null ? null : cookie.getPath(),
              cookie == null ? null : cookie.getDomain(),
              cookie == null ? Cookie.DEFAULT_VERSION : cookie.getVersion());
   }

   /**
    * Create a new instance supplementing the information in the supplied cookie.
    *
    * @param cookie  the cookie to clone
    * @param comment the comment
    * @param maxAge  the maximum age of the cookie in seconds
    * @param secure  specifies whether the cookie will only be sent over a secure connection
    * @throws IllegalArgumentException if cookie is null
    */
   public NewCookie(Cookie cookie, String comment, int maxAge, boolean secure)
   {
      this(cookie);
      this.comment = comment;
      this.maxAge = maxAge;
      this.secure = secure;
   }

   /**
    * Creates a new instance of NewCookie by parsing the supplied string.
    *
    * @param value the cookie string
    * @return the newly created NewCookie
    * @throws IllegalArgumentException if the supplied string cannot be parsed
    *                                  or is null
    */
   public static NewCookie valueOf(String value) throws IllegalArgumentException
   {
      return delegate.fromString(value);
   }

   /**
    * Get the comment associated with the cookie.
    *
    * @return the comment or null if none set
    */
   public String getComment()
   {
      return comment;
   }

   /**
    * Get the maximum age of the the cookie in seconds. Cookies older than
    * the maximum age are discarded. A cookie can be unset by sending a new
    * cookie with maximum age of 0 since it will overwrite any existing cookie
    * and then be immediately discarded. The default value of -1 indicates that the cookie
    * will be discarded at the end of the browser/application session.
    *
    * @return the maximum age in seconds
    */
   public int getMaxAge()
   {
      return maxAge;
   }

   /**
    * Whether the cookie will only be sent over a secure connection. Defaults
    * to false.
    *
    * @return true if the cookie will only be sent over a secure connection,
    *         false otherwise.
    */
   public boolean isSecure()
   {
      return secure;
   }

   /**
    * Obtain a new instance of a {@link Cookie} with the same name, value, path,
    * domain and version as this {@code NewCookie}. This method can be used to
    * obtain an object that can be compared for equality with another {@code Cookie};
    * since a {@code Cookie} will never compare equal to a {@code NewCookie}.
    *
    * @return a {@link Cookie}
    */
   public Cookie toCookie()
   {
      return new Cookie(this.getName(), this.getValue(), this.getPath(),
              this.getDomain(), this.getVersion());
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

   /**
    * Generate a hashcode by hashing all of the properties
    *
    * @return the hashcode
    */
   @Override
   public int hashCode()
   {
      int hash = super.hashCode();
      hash = 59 * hash + (this.comment != null ? this.comment.hashCode() : 0);
      hash = 59 * hash + this.maxAge;
      hash = 59 * hash + (this.secure ? 1 : 0);
      return hash;
   }

   /**
    * Compare for equality. Use {@link #toCookie()} to compare a
    * {@code NewCookie} to a {@code Cookie} considering only the common
    * properties.
    *
    * @param obj
    * @return true if the object is a {@code NewCookie} with the same value for
    *         all properties, false otherwise.
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj == null)
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      final NewCookie other = (NewCookie) obj;
      if (this.getName() != other.getName() && (this.getName() == null || !this.getName().equals(other.getName())))
      {
         return false;
      }
      if (this.getValue() != other.getValue() && (this.getValue() == null || !this.getValue().equals(other.getValue())))
      {
         return false;
      }
      if (this.getVersion() != other.getVersion())
      {
         return false;
      }
      if (this.getPath() != other.getPath() && (this.getPath() == null || !this.getPath().equals(other.getPath())))
      {
         return false;
      }
      if (this.getDomain() != other.getDomain() && (this.getDomain() == null || !this.getDomain().equals(other.getDomain())))
      {
         return false;
      }
      if (this.comment != other.comment && (this.comment == null || !this.comment.equals(other.comment)))
      {
         return false;
      }
      if (this.maxAge != other.maxAge)
      {
         return false;
      }
      if (this.secure != other.secure)
      {
         return false;
      }
      return true;
   }

}
