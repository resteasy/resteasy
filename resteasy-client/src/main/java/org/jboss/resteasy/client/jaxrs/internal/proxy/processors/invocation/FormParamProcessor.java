package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormParamProcessor extends AbstractInvocationCollectionProcessor
{

   public FormParamProcessor(final String paramName)
   {
      super(paramName);
   }

   public FormParamProcessor(final String paramName, final Type type, final Annotation[] annotations, final ClientConfiguration config)
   {
      super(paramName, type, annotations, config);
   }

   @Override
   protected ClientInvocation apply(ClientInvocation invocation, Object... objects)
   {
      for (Object object : objects) {
         Form form = null;
         Object entity = invocation.getEntity();
         if (entity != null)
         {
            if (entity instanceof Form)
            {
               form = (Form) entity;
            }
            else
            {
               throw new RuntimeException(Messages.MESSAGES.cannotSetFormParameter());
            }
         }
         else
         {
            form = new Form();
            invocation.setEntity(Entity.form(form));
         }
         String value = invocation.getClientConfiguration().toString(object);
         form.param(paramName, value);
      }
      return invocation;
   }

}
