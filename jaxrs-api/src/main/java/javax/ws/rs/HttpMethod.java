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
 * HttpMethod.java
 *
 * Created on October 25, 2006, 2:02 PM
 *
 */

package javax.ws.rs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Associates the name of a HTTP method with an annotation. A Java method annotated
 * with a runtime annotation that is itself annotated with this annotation will
 * be used to handle HTTP requests of the indicated HTTP method.
 * Such Java methods must satisfy the following constraints:
 * <ul>
 * <li>Methods must have a return type of <code>void</code>,
 * <code>Response</code> or <code>T</code>. Return values will be serialized
 * in the HTTP response. A <code>Response</code> return allows the application
 * to supply additional metadata that will accompany the response entity.
 * <li>Methods may have a single optional parameter
 * that is not annotated. The parameter provides access to
 * the contents of the HTTP request entity body. The parameter will be null if
 * the HTTP request entity body is of zero length.</li>
 * <li>Methods may have zero or more additional method arguments, each of which
 * must be annotated with either <code>@PathParam</code>,
 * <code>@HeaderParam</code>,
 * <code>@MatrixParam</code>, <code>@QueryParam</code> or
 * <code>@Context</code></li>
 * </ul>
 *
 * @see GET
 * @see POST
 * @see PUT
 * @see DELETE
 * @see HEAD
 * @see PathParam
 * @see QueryParam
 * @see MatrixParam
 * @see HeaderParam
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpMethod
{

   /**
    * HTTP GET method
    */
   public static final String GET = "GET";
   /**
    * HTTP POST method
    */
   public static final String POST = "POST";
   /**
    * HTTP PUT method
    */
   public static final String PUT = "PUT";
   /**
    * HTTP DELETE method
    */
   public static final String DELETE = "DELETE";
   /**
    * HTTP HEAD method
    */
   public static final String HEAD = "HEAD";

   /**
    * HTTP HEAD method
    */
   public static final String OPTIONS = "OPTIONS";

   /**
    * Specifies the name of a HTTP method. E.g. "GET".
    */
   String value();
}
