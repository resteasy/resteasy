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
 * be used to handle HTTP requests of the indicated HTTP method. It is an error
 * for a method to be annotated with more than one annotation that is annotated
 * with {@code HttpMethod}.
 *
 * @see GET
 * @see POST
 * @see PUT
 * @see DELETE
 * @see HEAD
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
    * HTTP OPTIONS method
    */
   public static final String OPTIONS = "OPTIONS";

   /**
    * Specifies the name of a HTTP method. E.g. "GET".
    */
   String value();
}
