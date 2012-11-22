/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.resteasy.cdi.injection.reverse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.cdi.injection.Book;
import org.jboss.resteasy.cdi.injection.BookResource;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 7, 2012
 */
@Path("/reverse")
@RequestScoped
public class ReverseInjectionResource
{  
   static public final String NON_CONTEXTUAL = "non-contextual";
   static HashMap<String,Object> store = new HashMap<String,Object>();
   
   @Inject int secret;
   @Inject private EJBHolderLocal holder;
   @Inject private Logger log;
   @Inject private BookResource resource;

   @POST
   @Path("testScopes")
   public Response testScopes()
   {
      log.info("entered ReverseInjectionResource.testScopes()");
      return holder.testScopes() ? Response.ok().build() : Response.serverError().build();
   }
   
   @POST
   @Path("setup")
   public Response setup()
   {
      log.info("entered ReverseInjectionResource.setup()");
      store.put("this.secret", this.secret);
      store.put("holder.secret", holder.theSecret());
      store.put("resource.secret", resource.theSecret());
      store.put("resource", resource);
      resource.getSet().add(new Book("test"));
      holder.setup();
      return Response.ok().build();
   }

   @POST
   @Path("test")
   public Response test()
   {
      log.info("entered ReverseInjectionResource.test()");
      if (BookResource.class.cast(store.get("resource")).getSet().size() > 0)
      {
         Iterator<Book> it = BookResource.class.cast(store.get("resource")).getSet().iterator();
         log.info("stored resource set:");
         while (it.hasNext())
         {
            log.info("  " + it.next());
         }
         return Response.serverError().entity("stored resource set not empty").build();
      }
      if (secret == Integer.class.cast(store.get("this.secret")))
      {
         return Response.serverError().entity("secret == store.get(\"this.secret\") shouldn't be true").build();
      }
      if (holder.theSecret() == (Integer.class.cast(store.get("holder.secret"))))
      {
         return Response.serverError().entity("holder.theSecret == store.get(\"holder.secret\") shouldn't be true").build();
      }
      if (resource.theSecret() == Integer.class.cast(store.get("resource.secret")))
      {
         return Response.serverError().entity("resource.theSecret() == store.get(\"resource.secret\") shouldn't be true").build();
      }
      if (holder.test())
      {
         return Response.ok().build();
      }
      else
      {
         return Response.serverError().build();
      }
   }
   
   public boolean theSame(ReverseInjectionResource that)
   {
      return this.secret == that.secret;
   }
}
