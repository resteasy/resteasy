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
 * Request.java
 *
 * Created on September 27, 2007, 5:39 PM
 *
 */

package javax.ws.rs.core;

import javax.ws.rs.core.Response.ResponseBuilder;
import java.util.Date;
import java.util.List;

/**
 * An injectable helper for request processing, all methods throw
 * java.lang.IllegalStateException if called outside the scope of a request
 * (e.g. from a provider constructor).
 * <p/>
 * Precondition processing (see the <code>evaluatePreconditions</code> methods)
 * can result in either a <code>null</code> return value to indicate that
 * preconditions have been met and that the request should continue, or
 * a non-null return value to indicate that preconditions were not met. In the
 * event that preconditions were not met, the returned <code>ResponseBuilder</code>
 * instance will have an appropriate status and will also include a <code>Vary</code>
 * header if the {@link #selectVariant} method was called prior to to calling
 * <code>evaluatePreconditions</code>. It is the responsibility of the caller
 * to check the status and add additional metadata if required. E.g., see
 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5">HTTP/1.1, section 10.3.5</a>
 * for details of the headers that are expected to accompany a <code>304 Not Modified</code>
 * response.
 */
public interface Request
{

   /**
    * Get the request method, e.g. GET, POST, etc.
    *
    * @return the request method
    * @see javax.ws.rs.HttpMethod
    */
   String getMethod();

   /**
    * Select the representation variant that best matches the request. More
    * explicit variants are chosen ahead of less explicit ones. A vary header
    * is computed from the supplied list and automatically added to the
    * response.
    *
    * @param variants a list of Variant that describe all of the
    *                 available representation variants.
    * @return the variant that best matches the request.
    * @throws java.lang.IllegalArgumentException
    *          if variants is empty or null
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    * @see Variant.VariantListBuilder
    */
   Variant selectVariant(List<Variant> variants) throws IllegalArgumentException;

   /**
    * Evaluate request preconditions based on the passed in value.
    *
    * @param eTag an ETag for the current state of the resource
    * @return null if the preconditions are met or a ResponseBuilder set with
    *         the appropriate status if the preconditions are not met. A returned
    *         ResponseBuilder will include an ETag header set with the value of eTag.
    * @throws java.lang.IllegalArgumentException
    *          if eTag is null
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   ResponseBuilder evaluatePreconditions(EntityTag eTag);

   /**
    * Evaluate request preconditions based on the passed in value.
    *
    * @param lastModified a date that specifies the modification date of the resource
    * @return null if the preconditions are met or a ResponseBuilder set with
    *         the appropriate status if the preconditions are not met.
    * @throws java.lang.IllegalArgumentException
    *          if lastModified is null
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   ResponseBuilder evaluatePreconditions(Date lastModified);

   /**
    * Evaluate request preconditions based on the passed in value.
    *
    * @param lastModified a date that specifies the modification date of the resource
    * @param eTag         an ETag for the current state of the resource
    * @return null if the preconditions are met or a ResponseBuilder set with
    *         the appropriate status if the preconditions are not met.  A returned
    *         ResponseBuilder will include an ETag header set with the value of eTag.
    * @throws java.lang.IllegalArgumentException
    *          if lastModified or eTag is null
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag);

   /**
    * Evaluate request preconditions for a resource that does not currently
    * exist. The primary use of this method is to support the {@link <a
    * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.24">
    * If-Match: *</a>} and {@link <a
    * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.26">
    * If-None-Match: *</a>} preconditions.
    * <p/>
    * <p>Note that both preconditions <code>If-None-Match: *</code> and
    * <code>If-None-Match: <i>something</i></code> will always be considered to
    * have been met and it is the applications responsibility
    * to enforce any additional method-specific semantics. E.g. a
    * <code>PUT</code> on a resource that does not exist might succeed whereas
    * a <code>GET</code> on a resource that does not exist would likely result
    * in a 404 response. It would be the responsibility of the application to
    * generate the 404 response.</p>
    *
    * @return null if the preconditions are met or a ResponseBuilder set with
    *         the appropriate status if the preconditions are not met.
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of
    *          a request
    */
   ResponseBuilder evaluatePreconditions();
}
