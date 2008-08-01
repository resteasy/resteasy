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
 * EntityTag.java
 *
 * Created on March 21, 2007, 3:14 PM
 *
 */

package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

/**
 * An abstraction for the value of a HTTP Entity Tag, used as the value
 * of an ETag response header.
 *
 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.11">HTTP/1.1 section 3.11</a>
 */
public class EntityTag
{

   private String value;
   private boolean weak;

   private static final HeaderDelegate<EntityTag> delegate =
           RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class);

   /**
    * Creates a new instance of a strong EntityTag.
    *
    * @param value the value of the tag, quotes not included.
    * @throws IllegalArgumentException if value is null
    */
   public EntityTag(String value)
   {
      this(value, false);
   }

   /**
    * Creates a new instance of an EntityTag
    *
    * @param value the value of the tag, quotes not included.
    * @param weak  true if this represents a weak tag, false otherwise
    * @throws IllegalArgumentException if value is null
    */
   public EntityTag(String value, boolean weak)
   {
      if (value == null)
         throw new IllegalArgumentException("value==null");
      this.value = value;
      this.weak = weak;
   }

   /**
    * Creates a new instance of EntityTag by parsing the supplied string.
    *
    * @param value the entity tag string
    * @return the newly created EntityTag
    * @throws IllegalArgumentException if the supplied string cannot be parsed
    *                                  or is null
    */
   public static EntityTag valueOf(String value) throws IllegalArgumentException
   {
      return delegate.fromString(value);
   }

   /**
    * Check the strength of an EntityTag
    *
    * @return true if this represents a weak tag, false otherwise
    */
   public boolean isWeak()
   {
      return weak;
   }

   /**
    * Get the value of an EntityTag
    *
    * @return the value of the tag
    */
   public String getValue()
   {
      return value;
   }

   /**
    * Compares obj to this tag to see if they are the same considering weakness and
    * value.
    *
    * @param obj the object to compare to
    * @return true if the two tags are the same, false otherwise.
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj == null)
         return false;
      if (!(obj instanceof EntityTag))
         return super.equals(obj);
      EntityTag other = (EntityTag) obj;
      if (value.equals(other.getValue()) && weak == other.isWeak())
         return true;
      return false;
   }

   /**
    * Generate hashCode based on value and weakness.
    *
    * @return the hashCode
    */
   @Override
   public int hashCode()
   {
      int hash = 3;
      hash = 17 * hash + (this.value != null ? this.value.hashCode() : 0);
      hash = 17 * hash + (this.weak ? 1 : 0);
      return hash;
   }

   /**
    * Convert the entity tag to a string suitable for use as the value of the
    * corresponding HTTP header.
    *
    * @return a stringified entity tag
    */
   @Override
   public String toString()
   {
      return delegate.toString(this);
   }
}
