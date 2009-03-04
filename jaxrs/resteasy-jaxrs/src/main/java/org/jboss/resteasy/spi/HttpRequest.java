package org.jboss.resteasy.spi;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;

/**
 * Bridge interface between the base Resteasy JAX-RS implementation and the actual HTTP transport (i.e. a servlet container)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpRequest
{
   HttpHeaders getHttpHeaders();

   InputStream getInputStream();

   UriInfo getUri();

   String getHttpMethod();

   /**
    * Encoded preprocessed path with extension mappings and matrix parameters removed
    *
    * @return
    */
   String getPreprocessedPath();

   public void setPreprocessedPath(String path);

   /**
    * application/x-www-form-urlencoded parameters
    * <p/>
    * This is here because @FormParam needs it and for when there are servlet filters that eat up the input stream
    *
    * @return null if no parameters, this is encoded map
    */
   MultivaluedMap<String, String> getFormParameters();

   MultivaluedMap<String, String> getDecodedFormParameters();

   /**
    * Map of contextual data.  Similar to HttpServletRequest attributes
    *
    * @return
    */
   Object getAttribute(String attribute);

   void setAttribute(String name, Object value);

   void removeAttribute(String name);


   /**
    * Asynchronous HTTP support.  Mirrors Servlet 3.0 API
    */
   public boolean isInitial();

   /**
    * Asynchronous HTTP support.  Mirrors Servlet 3.0 API
    */
   public boolean isSuspended();

   /**
    * This method will create an asynchronous response and prepare the
    * request to be issued asynchronously
    *
    * @return
    */
   public AsynchronousResponse createAsynchronousResponse(long suspendTimeout);

   /**
    * Returns the AsynchronousResponse created by createAsynchronousResponse
    *
    * @return
    */
   public AsynchronousResponse getAsynchronousResponse();

   /**
    * Callback by the initial calling thread.  This callback will probably do nothing in an asynchronous environment
    * but will be used to simulate AsynchronousResponse in vanilla Servlet containers that do not support
    * asychronous HTTP.
    */
   public void initialRequestThreadFinished();
}
