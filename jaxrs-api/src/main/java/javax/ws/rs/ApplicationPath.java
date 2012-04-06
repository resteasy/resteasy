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
 * ApplicationPath.java
 *
 * Created on August 21, 2009
 *
 */

package javax.ws.rs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies the application path that serves as the base URI
 * for all resource URIs provided by {@link javax.ws.rs.Path}. May only be
 * applied to a subclass of {@link javax.ws.rs.core.Application}.
 * <p/>
 * <p>When published in a Servlet container, the value of the application path
 * may be overridden using a servlet-mapping element in the web.xml.</p>
 *
 * @see javax.ws.rs.core.Application
 * @see Path
 * @since 1.1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationPath
{
   /**
    * Defines the base URI for all resource URIs. A trailing '/' character will
    * be automatically appended if one is not present.
    * <p/>
    * <p>The supplied value is automatically percent
    * encoded to conform to the {@code path} production of
    * {@link <a href="http://tools.ietf.org/html/rfc3986#section-3.3">RFC 3986 section 3.3</a>}.
    * Note that percent encoded values are allowed in the value, an
    * implementation will recognize such values and will not double
    * encode the '%' character.</p>
    */
   String value();

}
