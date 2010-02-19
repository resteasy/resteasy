package org.jboss.resteasy.star.bpm;

import org.jboss.resteasy.core.messagebody.ReaderUtility;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.spi.LinkHeader;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessDefinitionQuery;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.bpmn.model.BpmnProcessDefinition;
import org.jbpm.jpdl.internal.model.JpdlProcessDefinition;
import org.jbpm.pvm.internal.model.Activity;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.jbpm.pvm.internal.model.Transition;
import org.jbpm.pvm.internal.repository.DeploymentImpl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Variant;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/bpm")
public class ProcessDefinitionResource
{
   private ProcessEngine engine;

   public ProcessDefinitionResource(ProcessEngine engine)
   {
      this.engine = engine;
   }

   private AtomicLong idGenerator = new AtomicLong(1);

   @Context
   private UriInfo uriInfo;

   @Path("/definitions")
   @POST
   @Consumes("bpm/*")
   public Response createProcessDefinition(@Context HttpHeaders headers, String jpdl)
   {
      String subtype = headers.getMediaType().getSubtype();

      String fileName = idGenerator.incrementAndGet() + "." + subtype + ".xml";

      System.out.println("filename: " + fileName);
      DeploymentImpl deployment = (DeploymentImpl) engine.getRepositoryService().createDeployment().addResourceFromString(fileName, jpdl);
      deployment.deploy();
      if (deployment.hasErrors())
      {
         throw new RuntimeException("Has Errors");
      }
      if (deployment.hasProblems())
      {
         throw new RuntimeException("Has Problems");
      }
      if (deployment.getProcessDefinitionIds().size() > 1)
      {
         throw new WebApplicationException(
                 Response.status(400).entity("Only One Process Definition allowed").type("text/plain").build()
         );
      }
      else if (deployment.getProcessDefinitionIds().size() != 1)
      {
         throw new WebApplicationException(
                 Response.status(400).entity("No process definitions provided").type("text/plain").build()
         );
      }
      String id = deployment.getProcessDefinitionIds().iterator().next();

      URI location = uriInfo.getAbsolutePathBuilder().path(id).build();
      Response.ResponseBuilder builder = Response.created(location);
      addInstancesLink(builder);
      return builder.build();
   }

   @Path("/definitions/{pdid}")
   @HEAD
   public Response getDefinitionHead()
   {
      Response.ResponseBuilder response = Response.ok();

      addInstancesLink(response);
      return response.build();
   }

   @Path("/definitions/{pdid}")
   @GET
   @Produces({"bpm/jpdl", "bpm/bpmn"})
   public Response getDefinition(@PathParam("pdid") String pdid) throws Exception
   {
      ProcessDefinitionQuery query = engine.getRepositoryService().createProcessDefinitionQuery();
      query.processDefinitionId(pdid);
      ProcessDefinition pd = query.uniqueResult();
      if (pd == null)
      {
         throw new WebApplicationException(404);
      }
      Set<String> resourceNames = engine.getRepositoryService().getResourceNames(pd.getDeploymentId());
      if (resourceNames.size() != 1)
      {
         throw new WebApplicationException(500);
      }
      String resource = resourceNames.iterator().next();
      InputStream res = engine.getRepositoryService().getResourceAsStream(pd.getDeploymentId(), resource);
      Response.ResponseBuilder response = Response.ok(res);
      if (pd instanceof JpdlProcessDefinition)
      {
         response.type("bpm/jpdl");
      }
      else if (pd instanceof BpmnProcessDefinition)
      {
         response.type("bpm/bpmn");
      }
      else
      {
         throw new RuntimeException("Unknown type: " + pd.getClass().getName());
      }

      addInstancesLink(response);
      return response.build();
   }


   private void addInstancesLink(Response.ResponseBuilder response)
   {
      URI instances = uriInfo.getAbsolutePathBuilder().path("instances").build();
      response.header("Link", new Link("instances", "instances", instances.toString(), null, null));
   }

   @Path("/definitions/{pdid}/instances")
   public ProcessInstanceResource instances(@PathParam("pdid") String pdid)
   {
      return new ProcessInstanceResource(engine, uriInfo, pdid);
   }



}
