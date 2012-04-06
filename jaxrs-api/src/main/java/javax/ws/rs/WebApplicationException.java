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

import javax.ws.rs.core.Response;

/**
 * Runtime exception for applications.
 * <p/>
 * This exception may be thrown by a resource method, provider or
 * {@link javax.ws.rs.core.StreamingOutput} implementation if a specific
 * HTTP error response needs to be produced. Only effective if thrown prior to
 * the response being committed.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class WebApplicationException extends RuntimeException
{

   private static final long serialVersionUID = 11660101L;

   private Response response;

   /**
    * Construct a new instance with a blank message and default HTTP status code of 500
    */
   public WebApplicationException()
   {
      this(null, Response.Status.INTERNAL_SERVER_ERROR);
   }

   /**
    * Construct a new instance using the supplied response
    *
    * @param response the response that will be returned to the client, a value
    *                 of null will be replaced with an internal server error response (status
    *                 code 500)
    */
   public WebApplicationException(Response response)
   {
      this(null, response);
   }

   /**
    * Construct a new instance with a blank message and specified HTTP status code
    *
    * @param status the HTTP status code that will be returned to the client
    */
   public WebApplicationException(int status)
   {
      this(null, status);
   }

   /**
    * Construct a new instance with a blank message and specified HTTP status code
    *
    * @param status the HTTP status code that will be returned to the client
    * @throws IllegalArgumentException if status is null
    */
   public WebApplicationException(Response.Status status)
   {
      this(null, status);
   }

   /**
    * Construct a new instance with a blank message and default HTTP status code of 500
    *
    * @param cause the underlying cause of the exception
    */
   public WebApplicationException(Throwable cause)
   {
      this(cause, Response.Status.INTERNAL_SERVER_ERROR);
   }

   /**
    * Construct a new instance using the supplied response
    *
    * @param response the response that will be returned to the client, a value
    *                 of null will be replaced with an internal server error response (status
    *                 code 500)
    * @param cause    the underlying cause of the exception
    */
   public WebApplicationException(Throwable cause, Response response)
   {
      super(cause);
      if (response == null)
         this.response = Response.serverError().build();
      else
         this.response = response;
   }

   /**
    * Construct a new instance with a blank message and specified HTTP status code
    *
    * @param status the HTTP status code that will be returned to the client
    * @param cause  the underlying cause of the exception
    */
   public WebApplicationException(Throwable cause, int status)
   {
      this(cause, Response.status(status).build());
   }

   /**
    * Construct a new instance with a blank message and specified HTTP status code
    *
    * @param status the HTTP status code that will be returned to the client
    * @param cause  the underlying cause of the exception
    * @throws IllegalArgumentException if status is null
    */
   public WebApplicationException(Throwable cause, Response.Status status)
   {
      this(cause, Response.status(status).build());
   }

   /**
    * Get the HTTP response.
    *
    * @return the HTTP response.
    */
   public Response getResponse()
   {
      return response;
   }
}
