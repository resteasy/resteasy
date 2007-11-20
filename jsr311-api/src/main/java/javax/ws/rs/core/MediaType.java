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

import javax.ws.rs.ext.HeaderProvider;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import javax.ws.rs.ext.ProviderFactory;

/**
 * An abstraction for a media type. Instances are immutable.
 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7">HTTP/1.1 section 3.7</a>
 */
public class MediaType {
    
    private String type;
    private String subtype;
    private Map<String, String> parameters;

    /**
     * The value of a type or subtype wildcard.
     */
    public static final String MEDIA_TYPE_WILDCARD = "*";
    
    /**
     * Empty immutable map used for all instances without parameters
     */
    private static final Map<String, String> emptyMap = Collections.emptyMap();
    
    /**
     * Creates a new instance of MediaType by parsing the supplied string.
     * @param type the media type string
     * @return the newly created MediaType
     * @throws IllegalArgumentException if the supplied string cannot be parsed
     */
    public static MediaType parse(String type) throws IllegalArgumentException {
        String[] paths = type.split("/");
        return new MediaType(paths[0], paths[1]);
    }

    /**
     * Creates a new instance of MediaType with the supplied type, subtype and parameters.
     * @param type the primary type
     * @param subtype the subtype
     * @param parameters a map of media type parameters
     */
    public MediaType(String type, String subtype, Map<String, String> parameters) {
        this.type = type;
        this.subtype = subtype;
        this.parameters = parameters==null ? emptyMap : Collections.unmodifiableMap(parameters);
    }
    
    /**
     * Creates a new instance of MediaType with the supplied type and subtype.
     * @param type the primary type
     * @param subtype the subtype
     */
    public MediaType(String type, String subtype) {
        this(type,subtype,null);
    }

    /**
     * Creates a new instance of MediaType, both type and subtype are wildcards.
     */
    public MediaType() {
        this(MEDIA_TYPE_WILDCARD, MEDIA_TYPE_WILDCARD);
    }

    /**
     * Getter for primary type.
     * @return value of primary type.
     */
    public String getType() {
        return this.type;
    }
    
    /**
     * Checks if the primary type is a wildcard.
     * @return true if the primary type is a wildcard
     */
    public boolean isWildcardType() {
        return this.getType().equals(MEDIA_TYPE_WILDCARD);
    }
    
    /**
     * Getter for subtype.
     * @return value of subtype.
     */
    public String getSubtype() {
        return this.subtype;
    }

    /**
     * Checks if the subtype is a wildcard
     * @return true if the subtype is a wildcard  
     */
    public boolean isWildcardSubtype() {
        return this.getSubtype().equals(MEDIA_TYPE_WILDCARD);
    }
    
    /**
     * Getter for parameter map.
     * @return an immutable map of parameters.
     */
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    /**
     * Check if this media type is compatible with another media type. E.g.
     * image/* is compatible with image/jpeg, image/png, etc. Media type
     * parameters are ignored.
     * @return true if other is a subtype of this media type, false otherwise.
     * @param other the media type to compare with
     */
    public boolean isCompatible(MediaType other) {
        if (other == null)
            return false;
        if (type.equals(MEDIA_TYPE_WILDCARD) || other.type.equals(MEDIA_TYPE_WILDCARD))
            return true;
        else if (type.equalsIgnoreCase(other.type) && (subtype.equals(MEDIA_TYPE_WILDCARD) || other.subtype.equals(MEDIA_TYPE_WILDCARD)))
            return true;
        else
            return this.equals(other);
    }
    
    /**
     * Compares obj to this media type to see if they are the same by comparing
     * type and subtype only - parameters are ignored.
     * @param obj the object to compare to
     * @return true if the two media types are the same, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof MediaType))
            return false;
        MediaType other = (MediaType)obj;
        return (this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype));
    }
    
    /**
     * Generate a hashcode from the type and subtype.
     * @return a hashcode
     */
    @Override
    public int hashCode() {
        return (this.type.toLowerCase()+this.subtype.toLowerCase()).hashCode();
    }
    
    /**
     * Convert the media type to a string suitable for use as the value of a
     * corresponding HTTP header.
     * @return a stringified media type
     */
    @Override
    public String toString() {
        return type.toLowerCase()+"/"+subtype.toLowerCase();
    }
}
