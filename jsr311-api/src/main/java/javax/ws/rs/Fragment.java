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
package javax.ws.rs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds the URI fragment to a Java method parameter.
 * The value is URL decoded unless this is disabled using the Encoded
 * annotation. A default value can be specified using the DefaultValue annotation.
 * The class of the annotated parameter must have a constructor
 * that accepts a single String or List<String> argument, or a static method
 * named <code>valueOf</code> that accepts a single String argument
 * (see, for example, {@link Integer#valueOf(String)}).
 *
 * @see javax.ws.rs.DefaultValue
 * @see javax.ws.rs.Encoded
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Fragment {

}