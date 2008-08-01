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
 * Identifies the URI path that a resource class or class method will serve
 * requests for. Embedded template variables are allowed and are of the form
 * {name} where name is the template variable name. Values of template
 * variables may be extracted using {@link PathParam}.
 * <p/>
 * <p>Paths are relative. For an annotated class the base URI is the
 * application context. For an annotated method the base URI is the
 * effective URI of the containing class. For the purposes of absolutizing a
 * path against the base URI , a leading '/' in a path is
 * ignored and base URIs are treated as if they ended in '/'. E.g.:</p>
 * <p/>
 * <pre>&#64;Path("widgets")
 * public class WidgetsResource {
 *  &#64;GET
 *  String getList() {...}
 * <p/>
 *  &#64;GET &#64;Path("{id}")
 *  String getWidget(&#64;PathParam("id") String id) {...}
 * }</pre>
 * <p/>
 * <p>In the above, if the application context is
 * <code>http://example.com/catalogue</code>, then <code>GET</code> reguests for
 * <code>http://example.com/catalogue/widgets</code> will be handled by the
 * <code>getList</code> method while reguests for
 * <code>http://example.com/catalogue/widgets/<i>nnn</i></code> (where
 * <code><i>nnn</i></code> is some value) will be handled by the
 * <code>getWidget</code> method. The same would apply if the value of either
 * <code>&#64;Path</code> annotation started with '/'.
 * <p/>
 * <p>Classes may also be annotated with {@link Consumes} and
 * {@link Produces} to filter the requests they will receive.</p>
 *
 * @see Consumes
 * @see Produces
 * @see PathParam
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Path
{
   /**
    * Defines a URI template for the resource class or method, must not
    * include matrix parameters.
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
