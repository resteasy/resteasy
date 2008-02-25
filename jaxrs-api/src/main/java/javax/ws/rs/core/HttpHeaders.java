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
 * HttpHeaders.java
 *
 * Created on April 13, 2007, 3:00 PM
 *
 */

package javax.ws.rs.core;

import java.util.List;
import java.util.Map;

/**
 * An injectable interface that provides access to HTTP header information
 *
 * @see Context
 */
public interface HttpHeaders
{

   /**
    * Get the values of HTTP request headers. The returned Map is case-insensitive
    * wrt keys and is read-only.
    *
    * @return a map of header names and values.
    */
   public MultivaluedMap<String, String> getRequestHeaders();

   /**
    * Get a list of media types that are acceptable for the response.
    *
    * @return list of requested response media types
    */
   public List<MediaType> getAcceptableMediaTypes();

   /**
    * Get the media type of the request entity
    *
    * @return the media type or null if there is no request entity.
    */
   public MediaType getMediaType();

   /**
    * Get the language of the request entity
    *
    * @return the language of the entity or null if not specified
    */
   public String getLanguage();

   /**
    * Get any cookies that accompanied the request.
    *
    * @return a map of cookie name (String) to Cookie.
    */
   public Map<String, Cookie> getCookies();

}
