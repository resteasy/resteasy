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

/**
 * Contract for a provider that supplies context information to resource
 * classes and other providers. An implementation of this interface must be
 * annotated with {@link Provider}.
 * <p/>
 * A <code>ContextResolver</code> implementation may be annotated
 * with {@link javax.ws.rs.Produces} to restrict the media types for
 * which it will be considered suitable.
 *
 * @see javax.ws.rs.core.Context
 * @see Providers#getContextResolver(java.lang.Class, javax.ws.rs.core.MediaType)
 * @see Provider
 * @see javax.ws.rs.Produces
 */
public interface ContextResolver<T>
{

   /**
    * Get a context of type <code>T</code> that is applicable to the supplied
    * type.
    *
    * @param type the class of object for which a context is desired
    * @return a context for the supplied type or <code>null</code> if a
    *         context for the supplied type is not available from this provider.
    */
   T getContext(Class<?> type);
}
