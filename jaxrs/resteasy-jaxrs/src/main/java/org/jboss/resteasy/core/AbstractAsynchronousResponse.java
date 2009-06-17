package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.AsynchronousResponse;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractAsynchronousResponse implements AsynchronousResponse
{
   protected PostProcessInterceptor[] postProcessInterceptors;
   protected MessageBodyWriterInterceptor[] messageBodyWriterInterceptors;
   protected Annotation[] annotations;

   public PostProcessInterceptor[] getPostProcessInterceptors()
   {
      return postProcessInterceptors;
   }

   public void setPostProcessInterceptors(PostProcessInterceptor[] postProcessInterceptors)
   {
      this.postProcessInterceptors = postProcessInterceptors;
   }

   public MessageBodyWriterInterceptor[] getMessageBodyWriterInterceptors()
   {
      return messageBodyWriterInterceptors;
   }

   public void setMessageBodyWriterInterceptors(MessageBodyWriterInterceptor[] messageBodyWriterInterceptors)
   {
      this.messageBodyWriterInterceptors = messageBodyWriterInterceptors;
   }

   public Annotation[] getAnnotations()
   {
      return annotations;
   }

   public void setAnnotations(Annotation[] annotations)
   {
      this.annotations = annotations;
   }

   public void setupResponse(ServerResponse response)
   {
      response.setMessageBodyWriterInterceptors(messageBodyWriterInterceptors);
      response.setPostProcessInterceptors(postProcessInterceptors);
      response.setAnnotations(annotations);
   }
}
