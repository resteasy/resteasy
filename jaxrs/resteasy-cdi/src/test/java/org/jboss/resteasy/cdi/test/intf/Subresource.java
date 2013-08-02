package org.jboss.resteasy.cdi.test.intf;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class Subresource implements SubresourceLocal
{
   public void foo()
   {
   }
}
