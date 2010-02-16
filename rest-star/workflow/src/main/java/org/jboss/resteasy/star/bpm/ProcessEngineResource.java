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
public class ProcessEngineResource
{
   private ProcessEngine engine;

   public ProcessEngineResource(ProcessEngine engine)
   {
      this.engine = engine;
   }

   private AtomicLong idGenerator = new AtomicLong(1);

   @Context
   private UriInfo uriInfo;

   @Path("/definitions")
   @POST
   @Consumes("bpm/jpdl")
   public Response createProcessDefinition(String jpdl)
   {
      /*
      System.out.println("****************");
      System.out.println(jpdl);
      System.out.println("****************");
      */
      DeploymentImpl deployment = (DeploymentImpl) engine.getRepositoryService().createDeployment().addResourceFromString(idGenerator.incrementAndGet() + ".jpdl.xml", jpdl);
      deployment.deploy();
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

   @Path("/definitions/{id}")
   @HEAD
   @Produces("bpm/jpdl")
   public Response getDefinitionHead()
   {
      Response.ResponseBuilder response = Response.ok();

      addInstancesLink(response);
      return response.build();
   }

   @Path("/definitions/{id}")
   @GET
   @Produces("bpm/jpdl")
   public Response getDefinition(@PathParam("id") String pdid) throws Exception
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

      addInstancesLink(response);
      return response.build();
   }


   private void addInstancesLink(Response.ResponseBuilder response)
   {
      URI instances = uriInfo.getAbsolutePathBuilder().path("instances").build();
      response.header("Link", new Link("instances", "instances", instances.toString(), null, null));
   }

   @Path("/definitions/{id}/instances")
   @POST
   public Response createInstance(@PathParam("id") String pdid, @Context HttpRequest request)
   {
      MediaType mediaType = request.getHttpHeaders().getMediaType();
      if (mediaType == null)
      {
         ProcessInstance instance = engine.getExecutionService().startProcessInstanceById(pdid);

         return createProcessInstance(pdid, instance);
      }
      else if (MediaType.MULTIPART_FORM_DATA_TYPE.isCompatible(mediaType))
      {
         return createInstanceWithVariables(pdid, request);
      }
      else
      {
         throw new BadRequestException("Bad media type: " + mediaType);
      }
   }

   private Response createProcessInstance(String pdid, ProcessInstance instance)
   {
      URI location = uriInfo.getAbsolutePathBuilder().path(instance.getId()).build();
      Response.ResponseBuilder builder = Response.created(location);
      extractTransitions(instance, uriInfo.getAbsolutePathBuilder().path(instance.getId()), builder);
      addVariablesLink(builder, pdid, instance.getId());
      addNewVariableLink(builder, pdid, instance.getId());
      return builder.build();
   }

   public Response createInstanceWithVariables(String pdid, HttpRequest request)
   {
      MultipartFormDataInput input = null;
      try
      {
         input = ReaderUtility.read(MultipartFormDataInput.class,request.getHttpHeaders().getMediaType(), request.getInputStream());
      }
      catch (IOException e)
      {
         throw new BadRequestException(e);
      }
      HashMap<String, Object> vars = new HashMap<String, Object>();
      for (Map.Entry<String, List<InputPart>> entry : input.getFormDataMap().entrySet())
      {
         if (entry.getValue().size() < 1)
         {
            continue;
         }
         InputPart part = entry.getValue().get(0);
         byte[] bytes = null;
         try
         {
            bytes = part.getBody(byte[].class, null);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         MediaType mediaType = part.getMediaType();

         Variable var = new Variable(mediaType, part.getHeaders(), bytes);
         vars.put(entry.getKey(), var);
      }
      ProcessInstance instance = engine.getExecutionService().startProcessInstanceById(pdid, vars);

      return createProcessInstance(pdid, instance);
   }

   @Path("/definitions/{pdid}/instances/{id}")
   @HEAD
   public Response getProcessInstanceHead(@PathParam("pdid") String pdid, @PathParam("id") String pid)
   {
      ProcessInstance instance = engine.getExecutionService().findProcessInstanceById(pid);
      if (instance == null)
      {
         throw new WebApplicationException(404);
      }
      Response.ResponseBuilder builder = Response.ok();
      extractTransitions(instance, uriInfo.getAbsolutePathBuilder(), builder);
      addVariablesLink(builder, pdid, pid);
      addNewVariableLink(builder, pdid, pid);
      return builder.build();
   }

   private void extractTransitions(ProcessInstance instance, UriBuilder uriBuilder, Response.ResponseBuilder builder)
   {
      if (instance.findActiveActivityNames().size() < 1) return;
      String activityName = instance.findActiveActivityNames().iterator().next();

      ProcessDefinitionImpl pd = (ProcessDefinitionImpl) engine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(instance.getProcessDefinitionId()).uniqueResult();
      Activity current = pd.findActivity(activityName);
      for (Transition transition : current.getOutgoingTransitions())
      {
         String transitionName = transition.getName();
         if (transitionName == null)
         {
            transitionName = "continue";
         }
         URI signal = uriBuilder.path("signal").path(transitionName).build();
         builder.header("Link", new Link(transitionName, "signal", signal.toString(), null, null));
      }
   }

   private void addVariablesLink(Response.ResponseBuilder builder, String pdid, String pid)
   {
      UriBuilder uri = uriInfo.getBaseUriBuilder()
              .path(ProcessEngineResource.class)
              .path("definitions")
              .path(pdid).path("instances")
              .path(pid)
              .path("variables");
      builder.header("Link", new Link("variables", "variables", uri.build().toString(), null, null));
   }

   private void addNewVariableLink(Response.ResponseBuilder builder, String pdid, String pid)
   {
      UriBuilder uri = uriInfo.getBaseUriBuilder()
              .path(ProcessEngineResource.class)
              .path("definitions")
              .path(pdid).path("instances")
              .path(pid)
              .path("variables");
      builder.header("Link", new Link("variable-template", "variable-template", uri.build().toString() + "/{var}", null, null));
   }


   public void handleTransitionVariables(String pid, HttpRequest request)
   {
      MediaType mediaType = request.getHttpHeaders().getMediaType();
      if (mediaType == null)
      {
         return;
      }
      else if (mediaType != null && mediaType.isCompatible(MediaType.MULTIPART_FORM_DATA_TYPE) == false)
      {
         throw new BadRequestException("Bad media type: " + mediaType);
      }
      MultipartFormDataInput input;
      try
      {
         input = ReaderUtility.read(MultipartFormDataInput.class, mediaType, request.getInputStream());
      }
      catch (IOException e)
      {
         throw new BadRequestException(e);
      }
      HashMap<String, Object> vars = new HashMap<String, Object>();
      for (Map.Entry<String, List<InputPart>> entry : input.getFormDataMap().entrySet())
      {
         if (entry.getValue().size() < 1)
         {
            continue;
         }
         InputPart part = entry.getValue().get(0);
         byte[] bytes;
         try
         {
            bytes = part.getBody(byte[].class, null);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         MediaType partType = part.getMediaType();

         Variable var = (Variable) engine.getExecutionService().getVariable(pid, entry.getKey());
         if (var == null)
         {
            var = new Variable(partType, part.getHeaders(), bytes);
         }
         else
         {
            Representation rep = var.getRepresentation(partType);
            if (rep == null)
            {
               var.addRepresentation(new Representation(partType, bytes));
            }
            else
            {
               rep.setRepresentation(bytes);
            }
         }
         vars.put(entry.getKey(), var);
      }
      engine.getExecutionService().setVariables(pid, vars);

   }

   @Path("/definitions/{pdid}/instances/{id}/signal/continue")
   @POST
   public Response defaultTransition(@PathParam("pdid") String pdid, @PathParam("id") String pid, @Context HttpRequest request)
   {
      handleTransitionVariables(pid, request);
      ProcessInstance instance = engine.getExecutionService().findProcessInstanceById(pid);
      if (instance == null)
      {
         throw new WebApplicationException(404);
      }
      engine.getExecutionService().signalExecutionById(pid);
      instance = engine.getExecutionService().findProcessInstanceById(pid);
      Response.ResponseBuilder builder = Response.noContent();
      extractTransitions(instance, uriInfo.getAbsolutePathBuilder().path("definitions").path(pdid).path("instances").path(pid), builder);
      addVariablesLink(builder, pdid, pid);
      addNewVariableLink(builder, pdid, pid);
      return builder.build();
   }

   @Path("/definitions/{pdid}/instances/{id}/signal/{signal}")
   @POST
   public Response transition(@PathParam("pdid") String pdid, @PathParam("id") String pid, @PathParam("signal") String signal, @Context HttpRequest request)
   {
      handleTransitionVariables(pid, request);
      ProcessInstance instance = engine.getExecutionService().findProcessInstanceById(pid);
      if (instance == null)
      {
         throw new WebApplicationException(404);
      }
      engine.getExecutionService().signalExecutionById(pid, signal);
      Response.ResponseBuilder builder = Response.noContent();
      extractTransitions(instance, uriInfo.getAbsolutePathBuilder().path("definitions").path(pdid).path("instances").path(pid), builder);
      addVariablesLink(builder, pdid, pid);
      addNewVariableLink(builder, pdid, pid);
      return builder.build();
   }

   @Path("/definitions/{pdid}/instances/{id}/variables")
   @HEAD
   public Response headVariables(@PathParam("id") String pid)
   {
      System.out.println("HEAD VARIABLES!!!");
      Set<String> variableNames = engine.getExecutionService().getVariableNames(pid);
      if (variableNames == null)
      {
         System.out.println("VARIABLE NAMES WERE NULL!!!!");
         return Response.ok().build();
      }

      System.out.println("Gathering variables: " + variableNames);
      Map<String, Object> variables = engine.getExecutionService().getVariables(pid, variableNames);

      Response.ResponseBuilder builder = Response.ok();
      for (String varname : variableNames)
      {
         Object var = variables.get(varname);
         if (var instanceof Variable)
         {
            URI href = uriInfo.getAbsolutePathBuilder().path(varname).build();
            builder.header("Link", new Link(varname, varname, href.toString(), null, null));
         }
         else if (var instanceof Link)
         {
            builder.header("Link", var);
         }
      }
      return builder.build();
   }

   @Path("/definitions/{pdid}/instances/{pid}/variables/{var}")
   @PUT
   public Response createUpdateVariable(@PathParam("pid") String pid, @PathParam("var") String varName, @Context HttpHeaders headers, byte[] bytes)
   {
      Variable var = (Variable) engine.getExecutionService().getVariable(pid, varName);
      if (var == null)
      {
         var = new Variable(headers.getMediaType(), headers.getRequestHeaders(), bytes);
         engine.getExecutionService().setVariable(pid, varName, var);
         return Response.status(201).build();
      }
      else
      {
         Representation rep = var.getRepresentation(headers.getMediaType());
         if (rep == null)
         {
            var.addRepresentation(new Representation(headers.getMediaType(), bytes));
         }
         else
         {
            rep.setRepresentation(bytes);
         }
         return Response.noContent().build();
      }

   }

   @Path("/definitions/{pdid}/instances/{id}/variables")
   @POST
   public void setVariableLink(@PathParam("id") String pid, @HeaderParam("Set-Link") LinkHeader links)
   {
      if (links == null) return;
      for (Link link : links.getLinks())
      {
         if (link.getTitle() != null)
         {
            engine.getExecutionService().setVariable(pid, link.getTitle(), link);
         }
         else if (link.getRelationship() != null)
         {
            engine.getExecutionService().setVariable(pid, link.getRelationship(), link);
         }
      }
   }

   @Path("/definitions/{pdid}/instances/{id}/variables/{var}")
   @GET
   public Response getVariable(@PathParam("id") String pid, @PathParam("var") String varName, @Context HttpHeaders header)
   {
      Object obj = engine.getExecutionService().getVariable(pid, varName);
      if (!(obj instanceof Variable))
      {
         return Response.notAcceptable(new ArrayList<Variant>()).build();
      }
      Variable var = (Variable) obj;

      Representation rep;

      if (header.getAcceptableMediaTypes() == null || header.getAcceptableMediaTypes().size() == 0)
      {
         rep = var.getInitialRepresentation();
      }
      else
      {
         rep = var.match(header.getAcceptableMediaTypes());
      }

      if (rep == null)
      {
         List<Variant> vars = new ArrayList<Variant>();
         for (MediaType type : var.getMediaTypes())
         {
            vars.add(new Variant(type, null, null));
         }
         return Response.notAcceptable(vars).build();
      }
      else
      {
         Response.ResponseBuilder builder = Response.ok(rep.getRepresentation(), rep.getMediaType());
         Response response = builder.build();
         MultivaluedMap headers = var.getEntityHeaders();
         response.getMetadata().putAll(headers);
         return response;
      }

   }

}
