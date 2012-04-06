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
 * FormParam.java
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
 * Binds the value(s) of a form parameter contained within a request entity body
 * to a resource method parameter. Values are URL decoded unless this is
 * disabled using the {@link Encoded} annotation. A default value can be
 * specified using the {@link DefaultValue} annotation.
 * If the request entity body is absent or is an unsupported media type, the
 * default value is used.
 * <p/>
 * The type <code>T</code> of the annotated parameter must either:
 * <ol>
 * <li>Be a primitive type</li>
 * <li>Have a constructor that accepts a single <code>String</code> argument</li>
 * <li>Have a static method named <code>valueOf</code> or <code>fromString</code>
 * that accepts a single
 * <code>String</code> argument (see, for example, {@link Integer#valueOf(String)})</li>
 * <li>Be <code>List&lt;T&gt;</code>, <code>Set&lt;T&gt;</code> or
 * <code>SortedSet&lt;T&gt;</code>, where <code>T</code> satisfies 2 or 3 above.
 * The resulting collection is read-only.</li>
 * </ol>
 * <p/>
 * <p>If the type is not one of those listed in 4 above then the first value
 * (lexically) of the parameter is used.</p>
 * <p/>
 * <p>Note that, whilst the annotation target permits use on fields and methods,
 * this annotation is only required to be supported on resource method
 * parameters.</p>
 *
 * @see DefaultValue
 * @see Encoded
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FormParam
{
   /**
    * Defines the name of the form parameter whose value will be used
    * to initialize the value of the annotated method argument. The name is
    * specified in decoded form, any percent encoded literals within the value
    * will not be decoded and will instead be treated as literal text. E.g. if
    * the parameter name is "a b" then the value of the annotation is "a b",
    * <i>not</i> "a+b" or "a%20b".
    */
   String value();
}
