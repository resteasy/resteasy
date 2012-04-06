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
 * Context.java
 *
 * Created on November 16, 2006, 3:37 PM
 *
 */

package javax.ws.rs.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to inject information into a class
 * field, bean property or method parameter.
 *
 * @see Application
 * @see UriInfo
 * @see Request
 * @see HttpHeaders
 * @see SecurityContext
 * @see javax.ws.rs.ext.Providers
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Context
{
}
