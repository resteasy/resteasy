package org.jboss.resteasy.plugins.providers.jackson;

import org.codehaus.jackson.jaxrs.Annotations;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.jboss.resteasy.annotations.providers.NoJackson;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

/**
 * Only different from Jackson one is *+json in @Produces/@Consumes
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes({"application/*+json", "text/json"})
@Produces({"application/*+json", "text/json"})
public class ResteasyJacksonProvider extends JacksonJsonProvider
{
    public ResteasyJacksonProvider() {
        super(Annotations.JACKSON, Annotations.JAXB);
    }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.ResteasyJacksonProvider , method call : isReadable .")
   public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
   {
      if (FindAnnotation.findAnnotation(aClass, annotations, NoJackson.class) != null) return false;
      return super.isReadable(aClass, type, annotations, mediaType);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.ResteasyJacksonProvider , method call : isWriteable .")
   public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
   {
      if (FindAnnotation.findAnnotation(aClass, annotations, NoJackson.class) != null) return false;
      return super.isWriteable(aClass, type, annotations, mediaType);
   }
}
