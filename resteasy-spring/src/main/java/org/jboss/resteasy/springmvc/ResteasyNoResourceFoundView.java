package org.jboss.resteasy.springmvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpResponse;
import org.springframework.web.servlet.View;

public class ResteasyNoResourceFoundView implements View
{
   private SynchronousDispatcher dispatcher;

   public SynchronousDispatcher getDispatcher()
   {
      return dispatcher;
   }

   public void setDispatcher(SynchronousDispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   public String getContentType()
   {
      return null;
   }

   @SuppressWarnings("unchecked")
   public void render(Map model, HttpServletRequest request,
         HttpServletResponse response) throws Exception
   {
      final Failure failure = getFailure(model);
      new ResteasyWebHandlerTemplate<Void>(dispatcher){
         protected Void handle(ResteasyRequestWrapper requestWrapper,
               HttpResponse response) throws Exception
         {
            dispatcher.handleFailure(requestWrapper.getHttpRequest(), response, failure);
            return null;
         }
         
      };
   }

   @SuppressWarnings("unchecked")
   private Failure getFailure(Map model)
   {
      for(Object value: model.values())
         if (value instanceof Failure)
            return (Failure) value;
      return null;
   }
}
