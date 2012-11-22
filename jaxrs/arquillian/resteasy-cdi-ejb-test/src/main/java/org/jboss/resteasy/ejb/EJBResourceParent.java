/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.resteasy.ejb;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.cdi.util.Constants;

@Path("/")
public interface EJBResourceParent
{
   @GET
   @Path("verifyScopes")
   public int verifyScopes();
   
   @GET
   @Path("verifyInjection")
   public int verifyInjection();
   
   @POST
   @Path("create")
   @Consumes(Constants.MEDIA_TYPE_TEST_XML)
   public int createBook(Book book);
   
   @GET
   @Path("book/{id:[0-9][0-9]*}")
   @Produces(Constants.MEDIA_TYPE_TEST_XML)
   public Book lookupBookById(@PathParam("id") int id);
   
   @GET
   @Path("uses/{count}")
   public int testUse(@PathParam("count") int count);
   
   @GET
   @Path("reset")
   public void reset();
}

