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
 * MatrixParam.java
 *
 * Created on January 24, 2007, 2:40 PM
 *
 */

package javax.ws.rs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a URI matrix parameter to a Java method parameter.
 * The value is URL decoded unless this is disabled using the {@link Encoded}
 * annotation. A default value can be specified using the {@link DefaultValue}
 * annotation.
 * <p/>
 * The type of the annotated parameter must either:
 * <ul>
 * <li>Be a primitive type</li>
 * <li>Have a constructor that accepts a single String argument</li>
 * <li>Have a static method named <code>valueOf</code> that accepts a single
 * String argument (see, for example, {@link Integer#valueOf(String)})
 * </ul>
 *
 * @see DefaultValue
 * @see Encoded
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MatrixParam
{
   /**
    * Defines the name of the URI matrix parameter whose value will be used
    * to initialize the value of the annotated method argument, class field or
    * bean property.
    */
   String value();
}
