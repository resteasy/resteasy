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
 * DefaultValue.java
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
 * Defines the default value of request metadata that is bound using one of the
 * following annotations:
 * {@link javax.ws.rs.PathParam},
 * {@link javax.ws.rs.QueryParam},
 * {@link javax.ws.rs.MatrixParam},
 * {@link javax.ws.rs.CookieParam},
 * {@link javax.ws.rs.FormParam},
 * or {@link javax.ws.rs.HeaderParam}.
 * The default value is used if the corresponding metadata is not present in the
 * request.
 * <p/>
 * <p>If the type of the annotated parameter is
 * <code>List</code>, <code>Set</code> or <code>SortedSet</code> then the
 * resulting collection will have a single entry mapped from the supplied
 * default value.</p>
 * <p/>
 * <p>If this annotation is not used and the corresponding metadata is not
 * present in the request, the value will be an empty collection for
 * <code>List</code>, <code>Set</code> or <code>SortedSet</code>, null for
 * other object types, and the Java-defined default for primitive types.</p>
 *
 * @see PathParam
 * @see QueryParam
 * @see FormParam
 * @see HeaderParam
 * @see MatrixParam
 * @see CookieParam
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue
{
   /**
    * The default value.
    */
   String value();
}
