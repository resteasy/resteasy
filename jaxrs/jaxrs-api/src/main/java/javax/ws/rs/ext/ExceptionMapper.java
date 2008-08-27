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

package javax.ws.rs.ext;

import javax.ws.rs.core.Response;

/**
 * Contract for a provider that maps Java exceptions to
 * {@link javax.ws.rs.core.Response}. An implementation of this interface must
 * be annotated with {@link Provider}.
 *
 * @see Provider
 * @see javax.ws.rs.core.Response
 */
public interface ExceptionMapper<E extends Throwable>
{

   /**
    * Map an exception to a {@link javax.ws.rs.core.Response}. Returning
    * {@code null} results in a {@link javax.ws.rs.core.Response.Status#NO_CONTENT}
    * response. Throwing a runtime exception results in a
    * {@link javax.ws.rs.core.Response.Status#INTERNAL_SERVER_ERROR} response
    *
    * @param exception the exception to map to a response
    * @return a response mapped from the supplied exception
    */
   Response toResponse(E exception);
}
