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

package javax.ws.rs.ext;

import java.text.ParseException;

/**
 * A provider that supports the conversion of an HTTP header, of type T, to and
 * from a {@link String}. To add a HeaderProvider implementation, annotate the
 * implementation class with @Provider.
 *
 * @see Provider
 *
 * @author Paul.Sandoz@Sun.Com
 */
@Contract
public interface HeaderProvider<T> {
    /**
     * Ascertain if the Provider supports a particular type.
     *
     * @param type the type that is to be supported.
     * @return true if the type is supported, otherwise false.
     */
    boolean supports(Class<?> type);
    
    /**
     * Convert a HTTP header of type T to a {@link String}.
     * 
     * @param header the HTTP header of type T
     * @return the HTTP header as a {@link String}
     */
    String toString(T header);
    
    /**
     * Convert a {@link String} to a HTTP header of type T.
     * 
     * @return the HTTP header of type T
     * @param header the HTTP header as a {@link String}
     * @throws java.text.ParseException if the header cannot be parsed
     */
    T fromString(String header) throws ParseException;
}