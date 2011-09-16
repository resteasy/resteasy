package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpResponse;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@SuppressWarnings("rawtypes")
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

   public void render(Map model, HttpServletRequest request,
                      HttpServletResponse response) throws Exception
   {
      final Failure failure = getFailure(model);
      new ResteasyWebHandlerTemplate<Void>(dispatcher.getProviderFactory())
      {
         protected Void handle(ResteasyRequestWrapper requestWrapper,
                               HttpResponse response) throws Exception
         {
            dispatcher.handleException(requestWrapper.getHttpRequest(), response, failure);
            return null;
         }

      }.handle(new ResteasyRequestWrapper(request), response);
   }

   private Failure getFailure(Map model)
   {
      for (Object value : model.values())
         if (value instanceof Failure)
            return (Failure) value;
      return null;
   }
}
