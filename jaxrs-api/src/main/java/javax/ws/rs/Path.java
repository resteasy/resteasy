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
 * Path.java
 *
 * Created on September 15, 2006, 2:33 PM
 *
 */

package javax.ws.rs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies the URI path that a resource class or class method
 * will serve requests for. Root
 * resource classes require an &#64;Path annotation.
 * Classes may also be annotated with
 * {@link ConsumeMime} and {@link ProduceMime} to filter the
 * requests they will receive.
 *
 * @see ConsumeMime
 * @see ProduceMime
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Path
{
   /**
    * Defines a URI template for the resource. Requests to URIs that match
    * the template will be served by the annotated class or class method.
    * Embedded template variables are allowed and are of the form {name} where
    * name is the template variable name. Paths are relative to the base URI
    * of the container and must not include matrix parameters.
    * <p/>
    * <p>E.g.: &#64;Path("widgets/{id}")</p>
    */
   String value();

   /**
    * Controls whether the literal part of the supplied value (those characters
    * that are not part of a template variable) are URL encoded. If true, any
    * characters in the URI template that are not valid URI character will be
    * automatically encoded. If false then all characters must be valid URI
    * characters.
    */
   boolean encode() default true;

   /**
    * Controls whether a trailing template variable is limited to a single path
    * segment (<code>true</code>) or not (<code>false</code>). E.g.
    * <code>&#64;Path("widgets/{id}")</code> would
    * match widgets/foo but not widgets/foo/bar whereas
    * <code>&#64;Path(value="widgets/{id}", limit=false)</code> would match
    * both.
    */
   boolean limited() default true;
}
