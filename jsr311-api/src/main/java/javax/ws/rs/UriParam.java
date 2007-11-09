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
 * UriParam.java
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
 * Binds a method parameter to a URI template
 * parameter value.  The value is URL decoded unless this is disabled using the Encoded
 * annotation.
 * The class of the annotated parameter 
 * must have a constructor that accepts a single String argument, or a static method 
 * named <code>valueOf</code> that accepts a single String argument
 * (see, for example, {@link Integer#valueOf(String)}).
 *
 * @see Encoded
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UriParam {
    /**
     * Defines the name of the URI template parameter who value will be used
     * to initialize the value of the annotated method parameter, class field or
     * property.
     * 
     * <p>E.g. a class annotated with: <code>@UriTemplate("widgets/{id}")</code>
     * can have methods annotated with @HttpMethod whose arguments are annotated
     * with <code>@UriParam("id")</code>.
     */
    String value();
}
