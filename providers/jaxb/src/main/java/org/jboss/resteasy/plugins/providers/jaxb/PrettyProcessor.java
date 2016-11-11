package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.jboss.resteasy.spi.DecoratorProcessor;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.Marshaller;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@DecorateTypes({"text/*+xml", "application/*+xml"})
public class PrettyProcessor implements DecoratorProcessor<Marshaller, Formatted>
{
   public Marshaller decorate(Marshaller target, Formatted annotation,
                              Class type, Annotation[] annotations, MediaType mediaType)
   {
      try
      {
         target.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
         return target;
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }
}

