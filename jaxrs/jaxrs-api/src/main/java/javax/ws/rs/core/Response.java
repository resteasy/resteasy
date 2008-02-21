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

import java.net.URI;
import java.util.Date;
import java.util.List;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * Defines the contract between a returned instance and the runtime when
 * an application needs to provide metadata to the runtime. An application
 * class can extend this class directly or can use one the static 
 * methods to create an instance using a ResponseBuilder.
 * 
 * 
 * @see Response.ResponseBuilder
 */
public abstract class Response {
    
    /**
     * Return the entity for the response. The response will be serialized using a
     * MessageBodyWriter for the class of the entity.
     * @return an object instance or null if there is no entity
     * @see javax.ws.rs.ext.MessageBodyWriter
     */
    public abstract Object getEntity();
    
    /**
     * Get the status code associated with the response.
     * @return the response status code or -1 if the status was not set
     */
    public abstract int getStatus();

    /**
     * Get metadata associated with the response as a map. The returned map
     * may be subsequently modified by the JAX-RS runtime.
     * @return response metadata as a map
     */
    public abstract MultivaluedMap<String, Object> getMetadata();
    
    /**
     * Create a new ResponseBuilder with the supplied status.
     * @param status the response status
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder status(int status) {
        ResponseBuilder b = ResponseBuilder.newInstance();
        b.status(status);
        return b;
    }

    /**
     * Create a new ResponseBuilder with an OK status.
     * 
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder ok() {
        ResponseBuilder b = status(200);
        return b;
    }

    /**
     * Create a new ResponseBuilder that contains a representation.
     * 
     * @param entity the representation entity data
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder ok(Object entity) {
        ResponseBuilder b = ok();
        b.entity(entity);
        return b;
    }

    /**
     * Create a new ResponseBuilder that contains a representation.
     * 
     * @param entity the representation entity data
     * @param type the media type of the entity
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder ok(Object entity, MediaType type) {
        ResponseBuilder b = ok();
        b.entity(entity);
        b.type(type);
        return b;
    }

    /**
     * Create a new ResponseBuilder that contains a representation.
     * 
     * @param entity the representation entity data
     * @param type the media type of the entity
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder ok(Object entity, String type) {
        ResponseBuilder b = ok();
        b.entity(entity);
        b.type(type);
        return b;
    }

    /**
     * Create a new ResponseBuilder that contains a representation.
     * 
     * @param entity the representation entity data
     * @param variant representation metadata
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder ok(Object entity, Variant variant) {
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
    public static ResponseBuilder serverError() {
        ResponseBuilder b = status(500);
        return b;
    }

    /**
     * Create a new ResponseBuilder for a created resource.
     * 
     * @param location the URI of the new resource
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder created(URI location) {
        ResponseBuilder b = status(201).location(location);
        return b;
    }

    /**
     * Create a new ResponseBuilder for an empty response.
     * 
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder noContent() {
        ResponseBuilder b = status(204);
        return b;
    }

    /**
     * Create a new ResponseBuilder with a not-modified status.
     * 
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder notModified() {
        ResponseBuilder b = status(304);
        return b;
    }

    /**
     * Create a new ResponseBuilder with a not-modified status.
     * 
     * @param tag a tag for the unmodified entity
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder notModified(EntityTag tag) {
        ResponseBuilder b = notModified();
        b.tag(tag);
        return b;
    }

    /**
     * Create a new ResponseBuilder with a not-modified status.
     * 
     * @param tag a tag for the unmodified entity
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder notModified(String tag) {
        ResponseBuilder b = notModified();
        b.tag(tag);
        return b;
    }

    /**
     * Create a new ResponseBuilder for a temporary redirection.
     * 
     * @param location the redirection URI
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder temporaryRedirect(URI location) {
        ResponseBuilder b = status(307).location(location);
        return b;
    }

    /**
     * Create a new ResponseBuilder for a not acceptable response.
     * 
     * @param variants list of variants that were available
     * @return a new ResponseBuilder
     */
    public static ResponseBuilder notAcceptable(List<Variant> variants) {
        ResponseBuilder b = status(406).variants(variants);
        return b;
    }
        
    /**
     * A class used to build Response instances that contain metadata instead 
     * of or in addition to an entity. An initial instance may be obtained via
     * static methods of the Response class, instance methods provide the
     * ability to set metadata. E.g. to create a response that indicates the 
     * creation of a new resource:
     * <pre>@POST
     * Response addWidget(...) {
     *   Widget w = ...
     *   URI widgetId = ...
     *   return Response.created(w, widgetId).build();
     * }</pre>
     */
    public static abstract class ResponseBuilder {

        /**
         * Protected constructor, use one of the static methods of
         * <code>Response</code> to obtain an instance.
         */
        protected ResponseBuilder() {}
        
        /**
         * Create a new builder instance.
         * 
         * @return a new ResponseBuilder
         */
        protected static ResponseBuilder newInstance() {
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
         * Set the status on the ResponseBuilder.
         * 
         * @param status the response status
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder status(int status);
        
        /**
         * Set the entity on the ResponseBuilder.
         * 
         * 
         * @param entity the response entity
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder entity(Object entity);
        
        /**
         * Set the response media type on the ResponseBuilder.
         * 
         * 
         * @param type the media type of the response entity
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder type(MediaType type);
        
        /**
         * Set the response media type on the ResponseBuilder.
         * 
         * @param type the media type of the response entity
         * @return the updated ResponseBuilder
         * @throws IllegalArgumentException if type cannot be parsed
         */
        public abstract ResponseBuilder type(String type);
        
        /**
         * Set representation metadata on the ResponseBuilder.
         * 
         * 
         * @param variant metadata of the response entity
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder variant(Variant variant);
        
        /**
         * Add a Vary header that lists the available variants.
         * 
         * @param variants a list of available representation variants
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder variants(List<Variant> variants);

        /**
         * Set the language on the ResponseBuilder.
         * 
         * 
         * @param language the language of the response entity
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder language(String language);
        
        /**
         * Set the location on the ResponseBuilder.
         * 
         * 
         * @param location the location
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder location(URI location);
        
        /**
         * Set the content location on the ResponseBuilder.
         * 
         * 
         * @param location the content location
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder contentLocation(URI location);
        
        /**
         * Set an entity tag on the ResponseBuilder.
         * 
         * 
         * @param tag the entity tag
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder tag(EntityTag tag);
        
        /**
         * Set a strong entity tag on the ResponseBuilder.
         * 
         * 
         * @param tag the string content of a strong entity tag. The JAX-RS
         * runtime will quote the supplied value when creating the header.
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder tag(String tag);
        
        /**
         * Set the last modified date on the ResponseBuilder.
         * 
         * 
         * @param lastModified the last modified date
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder lastModified(Date lastModified);
        
        /**
         * Set the cache control data on the ResponseBuilder.
         * 
         * 
         * @param cacheControl the cache control directives
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder cacheControl(CacheControl cacheControl);
        
        /**
         * Set the value of a specific header on the ResponseBuilder.
         * 
         * @param name the name of the header
         * @param value the value of the header, the header will be serialized
         * using its toString method
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder header(String name, Object value);
        
        /**
         * Add cookies to the ResponseBuilder. If more than one cookie with
         * the same is supplied, later ones overwrite earlier ones.
         * 
         * @param cookies new cookies that will accompany the response.
         * @return the updated ResponseBuilder
         */
        public abstract ResponseBuilder cookie(NewCookie... cookies);
    }
}
