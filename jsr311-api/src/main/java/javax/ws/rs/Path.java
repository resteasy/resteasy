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
 * resource classes require an @Path annotation.
 * Classes may also be annotated with 
 * <code>@ConsumeMime</code> and <code>@ProduceMime</code> to filter the
 * requests they will receive.
 *
 * The container must honour annotations from the javax.annotation package. In
 * particular, resource class instance lifecycle can be managed using the
 * javax.annotation.PostConstruct and java.annotation.PreDestroy annotations
 * and a class can obtain access to container context information using 
 * javax.annotation.Resource as specified in JSR 250.
 *
 * @see ConsumeMime
 * @see ProduceMime
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    /**
     * Defines a URI template for the resource. Requests to URIs that match
     * the template will be served by the annotated class or class method.
     * Embedded template variables are allowed and are of the form {name} where
     * name is the template variable name. Paths are relative to the base URI
     * of the container.
     * 
     * <p>E.g.: @Path("widgets/{id}")</p>
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
     * <code>@Path("widgets/{id}")</code> would
     * match widgets/foo but not widgets/foo/bar whereas 
     * <code>@Path(value="widgets/{id}", limit=false)</code> would match
     * both.
     */
    boolean limited() default true;
}
