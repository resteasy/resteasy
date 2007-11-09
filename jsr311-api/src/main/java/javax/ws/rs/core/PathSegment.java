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
 * PathSegment.java
 *
 * Created on January 30, 2007, 4:35 PM
 *
 */

package javax.ws.rs.core;

/**
 * Represents a URI path segment and any associated matrix parameters.
 * <p>
 * All sequences of escaped octets for the path segment and matrix 
 * parameters are decoded.
 *
 * @author Marc.Hadley@Sun.Com
 */
public interface PathSegment {

    /**
     * Get the path segment.
     * <p>
     * @return the path segment
     */
    String getPath();
    /**
     * Get a map of the matrix parameters associated with the path segment
     * @return the map of matrix parameters
     */
    MultivaluedMap<String, String> getMatrixParameters();
    
}
