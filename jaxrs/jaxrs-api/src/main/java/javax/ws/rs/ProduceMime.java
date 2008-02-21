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
 * ProduceMime.java
 *
 * Created on September 15, 2006, 2:40 PM
 *
 */

package javax.ws.rs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the MIME type(s) that the methods of a resource class or
 * MessageBodyWriter can produce.
 * If not specified then a container will assume that any type can be produced.
 * Method level annotations override a class level annotation. A container
 * is responsible for ensuring that the method invoked is capable of producing
 * one of the MIME types requested in the HTTP request. If no such method is
 * available the container must respond with a HTTP "406 Not Acceptable" as
 * specified by RFC 2616.
 *
 * <p>A method for which there is a single-valued <code>ProduceMime</code>
 * is not required to set the media type of representations that it produces:
 * the container will use the value of the <code>ProduceMime</code> when
 * sending a response.</p>
 *
 * @see javax.ws.rs.ext.MessageBodyWriter
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProduceMime {
    /**
     * A list of MIME types. E.g. "image/jpeg,image/gif"
     */
    String[] value() default "*/*";    
}
