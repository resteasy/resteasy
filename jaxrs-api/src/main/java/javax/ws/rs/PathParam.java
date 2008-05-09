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
 * PathParam.java
 *
 * Created on November 16, 2006, 2:04 PM
 *
 */
package javax.ws.rs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds the value of a URI template parameter or a path segment
 * containing the template parameter to a resource method parameter, resource
 * class field, or resource class
 * bean property. The value is URL decoded unless this
 * is disabled using the {@link Encoded} annotation.
 * A default value can be specified using the {@link DefaultValue}
 * annotation.
 * <p/>
 * The type of the annotated parameter, field or property must either:
 * <ul>
 * <li>Be {@link javax.ws.rs.core.PathSegment}, the value will be a PathSegment
 * corresponding to the path segment that contains the named template parameter.
 * See {@link javax.ws.rs.core.UriInfo} for a means of retrieving all request
 * path segments.</li>
 * <li>Be a primitive type.</li>
 * <li>Have a constructor that accepts a single String argument.</li>
 * <li>Have a static method named <code>valueOf</code> that accepts a single
 * String argument (see, for example, {@link Integer#valueOf(String)}).
 * </ul>
 * <p/>
 * <p>Because injection occurs at object creation time, use of this annotation
 * on resource class fields and bean properties is only supported for the
 * default per-request resource class lifecycle. Resource classes using
 * other lifecycles should only use this annotation on resource method
 * parameters.</p>
 *
 * @see Encoded
 * @see DefaultValue
 * @see javax.ws.rs.core.PathSegment
 * @see javax.ws.rs.core.UriInfo
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathParam
{
   /**
    * Defines the name of the URI template parameter who value will be used
    * to initialize the value of the annotated method parameter, class field or
    * property.
    * <p/>
    * <p>E.g. a class annotated with: <code>&#64;Path("widgets/{id}")</code>
    * can have methods annotated with a HTTP method annotation whose arguments are annotated
    * with <code>&#64;PathParam("id")</code>.
    */
   String value();
}
