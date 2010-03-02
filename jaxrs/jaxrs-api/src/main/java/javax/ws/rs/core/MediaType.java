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
 * MediaType.java
 *
 * Created on March 22, 2007, 2:35 PM
 *
 */

package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * An abstraction for a media type. Instances are immutable.
 *
 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7">HTTP/1.1 section 3.7</a>
 */
public class MediaType
{

   private String type;
   private String subtype;
   private Map<String, String> parameters;

   /**
    * Empty immutable map used for all instances without parameters
    */
   private static final Map<String, String> emptyMap = Collections.emptyMap();

   private static final HeaderDelegate<MediaType> delegate =
           RuntimeDelegate.getInstance().createHeaderDelegate(MediaType.class);

   /**
    * The value of a type or subtype wildcard: "*"
    */
   public static final String MEDIA_TYPE_WILDCARD = "*";

   // Common media type constants
   /**
    * "*&#47;*"
    */
   public final static String WILDCARD = "*/*";
   /**
    * "*&#47;*"
    */
   public final static MediaType WILDCARD_TYPE = new MediaType();

   /**
    * "application/xml"
    */
   public final static String APPLICATION_XML = "application/xml";
   /**
    * "application/xml"
    */
   public final static MediaType APPLICATION_XML_TYPE = new MediaType("application", "xml");

   /**
    * "application/atom+xml"
    */
   public final static String APPLICATION_ATOM_XML = "application/atom+xml";
   /**
    * "application/atom+xml"
    */
   public final static MediaType APPLICATION_ATOM_XML_TYPE = new MediaType("application", "atom+xml");

   /**
    * "application/xhtml+xml"
    */
   public final static String APPLICATION_XHTML_XML = "application/xhtml+xml";
   /**
    * "application/xhtml+xml"
    */
   public final static MediaType APPLICATION_XHTML_XML_TYPE = new MediaType("application", "xhtml+xml");

   /**
    * "application/svg+xml"
    */
   public final static String APPLICATION_SVG_XML = "application/svg+xml";
   /**
    * "application/svg+xml"
    */
   public final static MediaType APPLICATION_SVG_XML_TYPE = new MediaType("application", "svg+xml");

   /**
    * "application/json"
    */
   public final static String APPLICATION_JSON = "application/json";
   /**
    * "application/json"
    */
   public final static MediaType APPLICATION_JSON_TYPE = new MediaType("application", "json");

   /**
    * "application/x-www-form-urlencoded"
    */
   public final static String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
   /**
    * "application/x-www-form-urlencoded"
    */
   public final static MediaType APPLICATION_FORM_URLENCODED_TYPE = new MediaType("application", "x-www-form-urlencoded");

   /**
    * "multipart/form-data"
    */
   public final static String MULTIPART_FORM_DATA = "multipart/form-data";
   /**
    * "multipart/form-data"
    */
   public final static MediaType MULTIPART_FORM_DATA_TYPE = new MediaType("multipart", "form-data");

   /**
    * "application/octet-stream"
    */
   public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";
   /**
    * "application/octet-stream"
    */
   public final static MediaType APPLICATION_OCTET_STREAM_TYPE = new MediaType("application", "octet-stream");

   /**
    * "text/plain"
    */
   public final static String TEXT_PLAIN = "text/plain";
   /**
    * "text/plain"
    */
   public final static MediaType TEXT_PLAIN_TYPE = new MediaType("text", "plain");

   /**
    * "text/xml"
    */
   public final static String TEXT_XML = "text/xml";
   /**
    * "text/xml"
    */
   public final static MediaType TEXT_XML_TYPE = new MediaType("text", "xml");

   /**
    * "text/html"
    */
   public final static String TEXT_HTML = "text/html";
   /**
    * "text/html"
    */
   public final static MediaType TEXT_HTML_TYPE = new MediaType("text", "html");

   /**
    * Creates a new instance of MediaType by parsing the supplied string.
    *
    * @param type the media type string
    * @return the newly created MediaType
    * @throws IllegalArgumentException if the supplied string cannot be parsed
    *                                  or is null
    */
   public static MediaType valueOf(String type) throws IllegalArgumentException
   {
      return delegate.fromString(type);
   }

   /**
    * Creates a new instance of MediaType with the supplied type, subtype and
    * parameters.
    *
    * @param type       the primary type, null is equivalent to
    *                   {@link #MEDIA_TYPE_WILDCARD}.
    * @param subtype    the subtype, null is equivalent to
    *                   {@link #MEDIA_TYPE_WILDCARD}.
    * @param parameters a map of media type parameters, null is the same as an
    *                   empty map.
    */
   public MediaType(String type, String subtype, Map<String, String> parameters)
   {
      this.type = type == null ? MEDIA_TYPE_WILDCARD : type;
      this.subtype = subtype == null ? MEDIA_TYPE_WILDCARD : subtype;
      if (parameters == null)
      {
         this.parameters = emptyMap;
      }
      else
      {
         Map<String, String> map = new TreeMap<String, String>(new Comparator<String>()
         {
            public int compare(String o1, String o2)
            {
               return o1.compareToIgnoreCase(o2);
            }
         });
         for (Map.Entry<String, String> e : parameters.entrySet())
         {
            map.put(e.getKey().toLowerCase(), e.getValue());
         }
         this.parameters = Collections.unmodifiableMap(map);
      }
   }

   /**
    * Creates a new instance of MediaType with the supplied type and subtype.
    *
    * @param type    the primary type, null is equivalent to
    *                {@link #MEDIA_TYPE_WILDCARD}
    * @param subtype the subtype, null is equivalent to
    *                {@link #MEDIA_TYPE_WILDCARD}
    */
   public MediaType(String type, String subtype)
   {
      this(type, subtype, emptyMap);
   }

   /**
    * Creates a new instance of MediaType, both type and subtype are wildcards.
    * Consider using the constant {@link #WILDCARD_TYPE} instead.
    */
   public MediaType()
   {
      this(MEDIA_TYPE_WILDCARD, MEDIA_TYPE_WILDCARD);
   }

   /**
    * Getter for primary type.
    *
    * @return value of primary type.
    */
   public String getType()
   {
      return this.type;
   }

   /**
    * Checks if the primary type is a wildcard.
    *
    * @return true if the primary type is a wildcard
    */
   public boolean isWildcardType()
   {
      return this.getType().equals(MEDIA_TYPE_WILDCARD);
   }

   /**
    * Getter for subtype.
    *
    * @return value of subtype.
    */
   public String getSubtype()
   {
      return this.subtype;
   }

   /**
    * Checks if the subtype is a wildcard
    *
    * @return true if the subtype is a wildcard
    */
   public boolean isWildcardSubtype()
   {
      return this.getSubtype().equals(MEDIA_TYPE_WILDCARD);
   }

   /**
    * Getter for a read-only parameter map. Keys are case-insensitive.
    *
    * @return an immutable map of parameters.
    */
   public Map<String, String> getParameters()
   {
      return parameters;
   }

   /**
    * Check if this media type is compatible with another media type. E.g.
    * image/* is compatible with image/jpeg, image/png, etc. Media type
    * parameters are ignored. The function is commutative.
    *
    * @param other the media type to compare with
    * @return true if the types are compatible, false otherwise.
    */
   public boolean isCompatible(MediaType other)
   {
      if (other == null)
         return false;
      if (type.equals(MEDIA_TYPE_WILDCARD) || other.type.equals(MEDIA_TYPE_WILDCARD))
         return true;
      else if (type.equalsIgnoreCase(other.type) && (subtype.equals(MEDIA_TYPE_WILDCARD) || other.subtype.equals(MEDIA_TYPE_WILDCARD)))
         return true;
      else
         return this.type.equalsIgnoreCase(other.type)
                 && this.subtype.equalsIgnoreCase(other.subtype);
   }

   /**
    * Compares obj to this media type to see if they are the same by comparing
    * type, subtype and parameters. Note that the case-sensitivity of parameter
    * values is dependent on the semantics of the parameter name, see
    * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7">HTTP/1.1</a>}.
    * This method assumes that values are case-sensitive.
    *
    * @param obj the object to compare to
    * @return true if the two media types are the same, false otherwise.
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj == null)
         return false;
      if (!(obj instanceof MediaType))
         return false;
      MediaType other = (MediaType) obj;
      return (this.type.equalsIgnoreCase(other.type)
              && this.subtype.equalsIgnoreCase(other.subtype)
              && this.parameters.equals(other.parameters));
   }

   /**
    * Generate a hashcode from the type, subtype and parameters.
    *
    * @return a hashcode
    */
   @Override
   public int hashCode()
   {
      return (this.type.toLowerCase() + this.subtype.toLowerCase()).hashCode() + this.parameters.hashCode();
   }

   /**
    * Convert the media type to a string suitable for use as the value of a
    * corresponding HTTP header.
    *
    * @return a stringified media type
    */
   @Override
   public String toString()
   {
      return delegate.toString(this);
   }
}
