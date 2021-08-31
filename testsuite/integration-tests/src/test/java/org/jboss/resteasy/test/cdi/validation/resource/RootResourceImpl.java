package org.jboss.resteasy.test.cdi.validation.resource;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class RootResourceImpl implements RootResource
{
   @Inject
   private SubResource subResource;

   @Override
   public SubResource getSubResource()
   {
      return subResource;
   }

   @GET
   public Response entered()
   {
      return Response.status(SubResourceImpl.methodEntered ? 444 : 200).build();
   }
}
