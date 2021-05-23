package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.springframework.web.servlet.View;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ResteasyNoResourceFoundView implements View
{
   private ResteasyDeployment deployment;

   public ResteasyDeployment getDeployment()
   {
      return deployment;
   }

   public void setDeployment(ResteasyDeployment deployment)
   {
      this.deployment = deployment;
   }

   public String getContentType()
   {
      return null;
   }

   public void render(Map model, HttpServletRequest request,
                      HttpServletResponse response) throws Exception
   {
      final Failure failure = getFailure(model);
      new ResteasyWebHandlerTemplate<Void>(deployment.getProviderFactory())
      {
         protected Void handle(ResteasyRequestWrapper requestWrapper,
                               HttpResponse response) throws Exception
         {
            SynchronousDispatcher dispatcher = (SynchronousDispatcher)deployment.getDispatcher();
            dispatcher.writeException(requestWrapper.getHttpRequest(), response, failure, t -> {});
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
