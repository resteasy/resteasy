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
 * Response.java
 *
 * Created on April 18, 2007, 9:00 AM
 *
 */

package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Defines the contract between a returned instance and the runtime when
 * an application needs to provide metadata to the runtime. An application
 * class can extend this class directly or can use one of the static
 * methods to create an instance using a ResponseBuilder.
 * <p/>
 * Several methods have parameters of type URI, {@link UriBuilder} provides
 * convenient methods to create such values as does
 * {@link <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/net/URI.html#create(java.lang.String)">URI.create()</a>}.
 *
 * @see Response.ResponseBuilder
 */
public abstract class Response
{

   /**
    * Protected constructor, use one of the static methods to obtain a
    * {@link ResponseBuilder} instance and obtain a Response from that.
    */
   protected Response()
   {
   }


   /**
    * Return the response entity. The response will be serialized using a
    * MessageBodyWriter for either the class of the entity or, in the case of
    * {@link GenericEntity}, the value of {@link GenericEntity#getRawType()}.
    *
    * @return an object instance or null if there is no entity
    * @see javax.ws.rs.ext.MessageBodyWriter
    */
   public abstract Object getEntity();

   /**
    * Get the status code associated with the response.
    *
    * @return the response status code or -1 if the status was not set.
    */
   public abstract int getStatus();

   /**
    * Get metadata associated with the response as a map. The returned map
    * may be subsequently modified by the JAX-RS runtime. Values will be
    * serialized using a {@link javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate}
    * if one is available via
    * {@link javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)}
    * for the class of the value or using the values {@code toString} method if a
    * header delegate is not available.
    *
    * @return response metadata as a map
    */
   public abstract MultivaluedMap<String, Object> getMetadata();

   /**
    * Create a new ResponseBuilder by performing a shallow copy of an
    * existing Response. The returned builder has its own metadata map but
    * entries are simply references to the keys and values contained in the
    * supplied Response metadata map.
    *
    * @param response a Response from which the status code, entity and metadata
    *                 will be copied
    * @return a new ReponseBuilder
    */
   public static ResponseBuilder fromResponse(Response response)
   {
      ResponseBuilder b = status(response.getStatus());
      b.entity(response.getEntity());
      for (String headerName : response.getMetadata().keySet())
      {
         List<Object> headerValues = response.getMetadata().get(headerName);
         for (Object headerValue : headerValues)
         {
            b.header(headerName, headerValue);
         }
      }
      return b;
   }

   /**
    * Create a new ResponseBuilder with the supplied status.
    *
    * @param status the response status
    * @return a new ResponseBuilder
    * @throws IllegalArgumentException if status is null
    */
   public static ResponseBuilder status(StatusType status)
   {
      ResponseBuilder b = ResponseBuilder.newInstance();
      b.status(status);
      return b;
   }

   /**
    * Create a new ResponseBuilder with the supplied status.
    *
    * @param status the response status
    * @return a new ResponseBuilder
    * @throws IllegalArgumentException if status is null
    */
   public static ResponseBuilder status(Status status)
   {
      return status((StatusType) status);
   }

   /**
    * Create a new ResponseBuilder with the supplied status.
    *
    * @param status the response status
    * @return a new ResponseBuilder
    * @throws IllegalArgumentException if status is less than 100 or greater
    *                                  than 599.
    */
   public static ResponseBuilder status(int status)
   {
      ResponseBuilder b = ResponseBuilder.newInstance();
      b.status(status);
      return b;
   }

   /**
    * Create a new ResponseBuilder with an OK status.
    *
    * @return a new ResponseBuilder
    */
   public static ResponseBuilder ok()
   {
      ResponseBuilder b = status(Status.OK);
      return b;
   }

   /**
    * Create a new ResponseBuilder that contains a representation. It is the
    * callers responsibility to wrap the actual entity with
    * {@link GenericEntity} if preservation of its generic type is required.
    *
    * @param entity the representation entity data
    * @return a new ResponseBuilder
    */
   public static ResponseBuilder ok(Object entity)
   {
      ResponseBuilder b = ok();
      b.entity(entity);
      return b;
   }

   /**
    * Create a new ResponseBuilder that contains a representation. It is the
    * callers responsibility to wrap the actual entity with
    * {@link GenericEntity} if preservation of its generic type is required.
    *
    * @param entity the representation entity data
    * @param type   the media type of the entity
    * @return a new ResponseBuilder
    */
   public static ResponseBuilder ok(Object entity, MediaType type)
   {
      ResponseBuilder b = ok();
      b.entity(entity);
      b.type(type);
      return b;
   }

   /**
    * Create a new ResponseBuilder that contains a representation. It is the
    * callers responsibility to wrap the actual entity with
    * {@link GenericEntity} if preservation of its generic type is required.
    *
    * @param entity the representation entity data
    * @param type   the media type of the entity
    * @return a new ResponseBuilder
    */
   public static ResponseBuilder ok(Object entity, String type)
   {
      ResponseBuilder b = ok();
      b.entity(entity);
      b.type(type);
      return b;
   }

   /**
    * Create a new ResponseBuilder that contains a representation. It is the
    * callers responsibility to wrap the actual entity with
    * {@link GenericEntity} if preservation of its generic type is required.
    *
    * @param entity  the representation entity data
    * @param variant representation metadata
    * @return a new ResponseBuilder
    */
   public static ResponseBuilder ok(Object entity, Variant variant)
   {
      ResponseBuilder b = ok();
      b.entity(entity);
      b.variant(variant);
      return b;
   }

   /**
    * Create a new ResponseBuilder with an server error status.
    *
    * @return a new ResponseBuilder
    */
   public static ResponseBuilder serverError()
   {
      ResponseBuilder b = status(Status.INTERNAL_SERVER_ERROR);
      return b;
   }

   /**
    * Create a new ResponseBuilder for a created resource, set the location
    * header using the supplied value.
    *
    * @param location the URI of the new resource. If a relative URI is
    *                 supplied it will be converted into an absolute URI by resolving it
    *                 relative to the request URI (see {@link UriInfo#getRequestUri}).
    * @return a new ResponseBuilder
    * @throws java.lang.IllegalArgumentException
    *          if location is null
    */
   public static ResponseBuilder created(URI location)
   {
      ResponseBuilder b = status(Status.CREATED).location(location);
      return b;
   }

   /**
    * Create a new ResponseBuilder for an empty response.
    *
    * @return a new ResponseBuilder
    */
   public static ResponseBuilder noContent()
   {
      ResponseBuilder b = status(Status.NO_CONTENT);
      return b;
   }

   /**
    * Create a new ResponseBuilder with a not-modified status.
    *
    * @return a new ResponseBuilder
    */
   public static ResponseBuilder notModified()
   {
      ResponseBuilder b = status(Status.NOT_MODIFIED);
      return b;
   }

   /**
    * Create a new ResponseBuilder with a not-modified status.
    *
    * @param tag a tag for the unmodified entity
    * @return a new ResponseBuilder
    * @throws java.lang.IllegalArgumentException
    *          if tag is null
    */
   public static ResponseBuilder notModified(EntityTag tag)
   {
      ResponseBuilder b = notModified();
      b.tag(tag);
      return b;
   }

   /**
    * Create a new ResponseBuilder with a not-modified status
    * and a strong entity tag. This is a shortcut
    * for <code>notModified(new EntityTag(<i>value</i>))</code>.
    *
    * @param tag the string content of a strong entity tag. The JAX-RS
    *            runtime will quote the supplied value when creating the header.
    * @return a new ResponseBuilder
    * @throws java.lang.IllegalArgumentException
    *          if tag is null
    */
   public static ResponseBuilder notModified(String tag)
   {
      ResponseBuilder b = notModified();
      b.tag(tag);
      return b;
   }

   /**
    * Create a new ResponseBuilder for a redirection. Used in the
    * redirect-after-POST (aka POST/redirect/GET) pattern.
    *
    * @param location the redirection URI. If a relative URI is
    *                 supplied it will be converted into an absolute URI by resolving it
    *                 relative to the base URI of the application (see
    *                 {@link UriInfo#getBaseUri}).
    * @return a new ResponseBuilder
    * @throws java.lang.IllegalArgumentException
    *          if location is null
    */
   public static ResponseBuilder seeOther(URI location)
   {
      ResponseBuilder b = status(Status.SEE_OTHER).location(location);
      return b;
   }

   /**
    * Create a new ResponseBuilder for a temporary redirection.
    *
    * @param location the redirection URI. If a relative URI is
    *                 supplied it will be converted into an absolute URI by resolving it
    *                 relative to the base URI of the application (see
    *                 {@link UriInfo#getBaseUri}).
    * @return a new ResponseBuilder
    * @throws java.lang.IllegalArgumentException
    *          if location is null
    */
   public static ResponseBuilder temporaryRedirect(URI location)
   {
      ResponseBuilder b = status(Status.TEMPORARY_REDIRECT).location(location);
      return b;
   }

   /**
    * Create a new ResponseBuilder for a not acceptable response.
    *
    * @param variants list of variants that were available, a null value is
    *                 equivalent to an empty list.
    * @return a new ResponseBuilder
    */
   public static ResponseBuilder notAcceptable(List<Variant> variants)
   {
      ResponseBuilder b = status(Status.NOT_ACCEPTABLE).variants(variants);
      return b;
   }

   /**
    * A class used to build Response instances that contain metadata instead
    * of or in addition to an entity. An initial instance may be obtained via
    * static methods of the Response class, instance methods provide the
    * ability to set metadata. E.g. to create a response that indicates the
    * creation of a new resource:
    * <pre>&#64;POST
    * Response addWidget(...) {
    *   Widget w = ...
    *   URI widgetId = UriBuilder.fromResource(Widget.class)...
    *   return Response.created(widgetId).build();
    * }</pre>
    * <p/>
    * <p>Several methods have parameters of type URI, {@link UriBuilder} provides
    * convenient methods to create such values as does <code>URI.create()</code>.</p>
    * <p/>
    * <p>Where multiple variants of the same method are provided, the type of
    * the supplied parameter is retained in the metadata of the built
    * {@code Response}.</p>
    */
   public static abstract class ResponseBuilder
   {

      /**
       * Protected constructor, use one of the static methods of
       * <code>Response</code> to obtain an instance.
       */
      protected ResponseBuilder()
      {
      }

      /**
       * Create a new builder instance.
       *
       * @return a new ResponseBuilder
       */
      protected static ResponseBuilder newInstance()
      {
         ResponseBuilder b = RuntimeDelegate.getInstance().createResponseBuilder();
         return b;
      }

      /**
       * Create a Response instance from the current ResponseBuilder. The builder
       * is reset to a blank state equivalent to calling the ok method.
       *
       * @return a Response instance
       */
      public abstract Response build();

      /**
       * Create a copy of the ResponseBuilder preserving its state.
       *
       * @return a copy of the ResponseBuilder
       */
      @Override
      public abstract ResponseBuilder clone();

      /**
       * Set the status on the ResponseBuilder.
       *
       * @param status the response status
       * @return the updated ResponseBuilder
       * @throws IllegalArgumentException if status is less than 100 or greater
       *                                  than 599.
       */
      public abstract ResponseBuilder status(int status);

      /**
       * Set the status on the ResponseBuilder.
       *
       * @param status the response status
       * @return the updated ResponseBuilder
       * @throws IllegalArgumentException if status is null
       */
      public ResponseBuilder status(StatusType status)
      {
         if (status == null)
            throw new IllegalArgumentException();
         return status(status.getStatusCode());
      }

      ;

      /**
       * Set the status on the ResponseBuilder.
       *
       * @param status the response status
       * @return the updated ResponseBuilder
       * @throws IllegalArgumentException if status is null
       */
      public ResponseBuilder status(Status status)
      {
         return status((StatusType) status);
      }

      ;

      /**
       * Set the entity on the ResponseBuilder. It is the
       * callers responsibility to wrap the actual entity with
       * {@link GenericEntity} if preservation of its generic type is required.
       *
       * @param entity the response entity
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder entity(Object entity);

      /**
       * Set the response media type on the ResponseBuilder.
       *
       * @param type the media type of the response entity, if null any
       *             existing value for type will be removed
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder type(MediaType type);

      /**
       * Set the response media type on the ResponseBuilder.
       *
       * @param type the media type of the response entity, if null any
       *             existing value for type will be removed
       * @return the updated ResponseBuilder
       * @throws IllegalArgumentException if type cannot be parsed
       */
      public abstract ResponseBuilder type(String type);

      /**
       * Set representation metadata on the ResponseBuilder. Equivalent to
       * setting the values of content type, content language, and content
       * encoding separately using the values of the variant properties.
       *
       * @param variant metadata of the response entity, a null value is
       *                equivalent to a variant with all null properties.
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder variant(Variant variant);

      /**
       * Add a Vary header that lists the available variants.
       *
       * @param variants a list of available representation variants, a null
       *                 value will remove an existing value for vary.
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder variants(List<Variant> variants);

      /**
       * Set the language on the ResponseBuilder.
       *
       * @param language the language of the response entity, if null any
       *                 existing value for language will be removed
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder language(String language);

      /**
       * Set the language on the ResponseBuilder.
       *
       * @param language the language of the response entity, if null any
       *                 existing value for type will be removed
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder language(Locale language);

      /**
       * Set the location on the ResponseBuilder.
       *
       * @param location the location. If a relative URI is
       *                 supplied it will be converted into an absolute URI by resolving it
       *                 relative to the base URI of the application (see
       *                 {@link UriInfo#getBaseUri}). If null any
       *                 existing value for location will be removed.
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder location(URI location);

      /**
       * Set the content location on the ResponseBuilder.
       *
       * @param location the content location. Relative or absolute URIs
       *                 may be used for the value of content location. If null any
       *                 existing value for content location will be removed.
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder contentLocation(URI location);

      /**
       * Set an entity tag on the ResponseBuilder.
       *
       * @param tag the entity tag, if null any
       *            existing entity tag value will be removed.
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder tag(EntityTag tag);

      /**
       * Set a strong entity tag on the ResponseBuilder. This is a shortcut
       * for <code>tag(new EntityTag(<i>value</i>))</code>.
       *
       * @param tag the string content of a strong entity tag. The JAX-RS
       *            runtime will quote the supplied value when creating the header. If
       *            null any existing entity tag value will be removed.
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder tag(String tag);

      /**
       * Set the last modified date on the ResponseBuilder.
       *
       * @param lastModified the last modified date, if null any existing
       *                     last modified value will be removed.
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder lastModified(Date lastModified);

      /**
       * Set the cache control data on the ResponseBuilder.
       *
       * @param cacheControl the cache control directives, if null removes any
       *                     existing cache control directives.
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder cacheControl(CacheControl cacheControl);

      /**
       * Set the expires date on the ResponseBuilder.
       *
       * @param expires the expiration date, if null removes any existing
       *                expires value.
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder expires(Date expires);

      /**
       * Add a header to the ResponseBuilder.
       *
       * @param name  the name of the header
       * @param value the value of the header, the header will be serialized
       *              using a {@link javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate} if
       *              one is available via
       *              {@link javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)}
       *              for the class of {@code value} or using its {@code toString} method if a
       *              header delegate is not available. If {@code value} is null then all
       *              current headers of the same name will be removed.
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder header(String name, Object value);

      /**
       * Add cookies to the ResponseBuilder.
       *
       * @param cookies new cookies that will accompany the response. A null
       *                value will remove all cookies, including those added via the
       *                {@link #header(java.lang.String, java.lang.Object)} method.
       * @return the updated ResponseBuilder
       */
      public abstract ResponseBuilder cookie(NewCookie... cookies);
   }

   /**
    * Base interface for statuses used in responses.
    */
   public interface StatusType
   {
      /**
       * Get the associated status code
       *
       * @return the status code
       */
      public int getStatusCode();

      /**
       * Get the class of status code
       *
       * @return the class of status code
       */
      public Status.Family getFamily();

      /**
       * Get the reason phrase
       *
       * @return the reason phrase
       */
      public String getReasonPhrase();
   }

   /**
    * Commonly used status codes defined by HTTP, see
    * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10">HTTP/1.1 documentation</a>}
    * for the complete list. Additional status codes can be added by applications
    * by creating an implementation of {@link StatusType}.
    */
   public enum Status implements StatusType
   {
      /**
       * 200 OK, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.1">HTTP/1.1 documentation</a>}.
       */
      OK(200, "OK"),
      /**
       * 201 Created, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.2">HTTP/1.1 documentation</a>}.
       */
      CREATED(201, "Created"),
      /**
       * 202 Accepted, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.3">HTTP/1.1 documentation</a>}.
       */
      ACCEPTED(202, "Accepted"),
      /**
       * 204 No Content, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.5">HTTP/1.1 documentation</a>}.
       */
      NO_CONTENT(204, "No Content"),
      /**
       * 301 Moved Permanently, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.2">HTTP/1.1 documentation</a>}.
       */
      MOVED_PERMANENTLY(301, "Moved Permanently"),
      /**
       * 303 See Other, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.4">HTTP/1.1 documentation</a>}.
       */
      SEE_OTHER(303, "See Other"),
      /**
       * 304 Not Modified, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5">HTTP/1.1 documentation</a>}.
       */
      NOT_MODIFIED(304, "Not Modified"),
      /**
       * 307 Temporary Redirect, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.8">HTTP/1.1 documentation</a>}.
       */
      TEMPORARY_REDIRECT(307, "Temporary Redirect"),
      /**
       * 400 Bad Request, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.1">HTTP/1.1 documentation</a>}.
       */
      BAD_REQUEST(400, "Bad Request"),
      /**
       * 401 Unauthorized, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2">HTTP/1.1 documentation</a>}.
       */
      UNAUTHORIZED(401, "Unauthorized"),
      /**
       * 403 Forbidden, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.4">HTTP/1.1 documentation</a>}.
       */
      FORBIDDEN(403, "Forbidden"),
      /**
       * 404 Not Found, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.5">HTTP/1.1 documentation</a>}.
       */
      NOT_FOUND(404, "Not Found"),
      /**
       * 406 Not Acceptable, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.7">HTTP/1.1 documentation</a>}.
       */
      NOT_ACCEPTABLE(406, "Not Acceptable"),
      /**
       * 409 Conflict, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.10">HTTP/1.1 documentation</a>}.
       */
      CONFLICT(409, "Conflict"),
      /**
       * 410 Gone, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.11">HTTP/1.1 documentation</a>}.
       */
      GONE(410, "Gone"),
      /**
       * 412 Precondition Failed, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.13">HTTP/1.1 documentation</a>}.
       */
      PRECONDITION_FAILED(412, "Precondition Failed"),
      /**
       * 415 Unsupported Media Type, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.16">HTTP/1.1 documentation</a>}.
       */
      UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
      /**
       * 500 Internal Server Error, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.1">HTTP/1.1 documentation</a>}.
       */
      INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
      /**
       * 503 Service Unavailable, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.4">HTTP/1.1 documentation</a>}.
       */
      SERVICE_UNAVAILABLE(503, "Service Unavailable");

      private final int code;
      private final String reason;
      private Family family;

      /**
       * An enumeration representing the class of status code. Family is used
       * here since class is overloaded in Java.
       */
      public enum Family
      {
         INFORMATIONAL, SUCCESSFUL, REDIRECTION, CLIENT_ERROR, SERVER_ERROR, OTHER
      }

      ;

      Status(final int statusCode, final String reasonPhrase)
      {
         this.code = statusCode;
         this.reason = reasonPhrase;
         switch (code / 100)
         {
            case 1:
               this.family = Family.INFORMATIONAL;
               break;
            case 2:
               this.family = Family.SUCCESSFUL;
               break;
            case 3:
               this.family = Family.REDIRECTION;
               break;
            case 4:
               this.family = Family.CLIENT_ERROR;
               break;
            case 5:
               this.family = Family.SERVER_ERROR;
               break;
            default:
               this.family = Family.OTHER;
               break;
         }
      }

      /**
       * Get the class of status code
       *
       * @return the class of status code
       */
      public Family getFamily()
      {
         return family;
      }

      /**
       * Get the associated status code
       *
       * @return the status code
       */
      public int getStatusCode()
      {
         return code;
      }

      /**
       * Get the reason phrase
       *
       * @return the reason phrase
       */
      public String getReasonPhrase()
      {
         return toString();
      }

      /**
       * Get the reason phrase
       *
       * @return the reason phrase
       */
      @Override
      public String toString()
      {
         return reason;
      }

      /**
       * Convert a numerical status code into the corresponding Status
       *
       * @param statusCode the numerical status code
       * @return the matching Status or null is no matching Status is defined
       */
      public static Status fromStatusCode(final int statusCode)
      {
         for (Status s : Status.values())
         {
            if (s.code == statusCode)
            {
               return s;
            }
         }
         return null;
      }
   }
}
