package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormParamProcessor extends AbstractInvocationCollectionProcessor
{

   public FormParamProcessor(String paramName)
   {
      super(paramName);
   }

   @Override
   protected ClientInvocationBuilder apply(ClientInvocationBuilder target, Object object)
   {
      Form form = null;
      Object entity = target.getInvocation().getEntity();
      if (entity != null)
      {
         if (entity instanceof Form)
         {
            form = (Form) entity;
         }
         else
         {
            throw new RuntimeException("Cannot set a form parameter if entity body already set");
         }
      }
      else
      {
         form = new Form();
         target.getInvocation().setEntity(Entity.form(form));
      }
      form.param(paramName, target.getInvocation().getConfiguration().toString(object));
      return target;
   }

}