package org.jboss.resteasy.star.bpm;

import org.jboss.resteasy.core.messagebody.ReaderUtility;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.spi.LinkHeader;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.TaskService;
import org.jbpm.api.task.Task;
import org.jbpm.pvm.internal.model.Activity;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.jbpm.pvm.internal.model.Transition;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TaskResource
{
   private TaskService taskService;
   private ProcessEngine engine;

   public TaskResource(ProcessEngine engine, UriInfo uriInfo)
   {
      this.engine = engine;
      this.uriInfo = uriInfo;
      this.taskService = engine.getTaskService();
   }

   private UriInfo uriInfo;

   @Path("/next")
   @POST
   public Response nextTask(@QueryParam("activity") String activity,
                            @QueryParam("user") String user)
   {
      TaskQuery query = taskService.createTaskQuery();
      if (activity != null)
      {
         query.activityName(activity);
      }
      if (user != null)
      {
         query.candidate(user);
      }
      else
      {
         throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }
      Task task = query.unassigned().uniqueResult();
      if (task == null)
      {
         throw new WebApplicationException(500);
      }
      Set<String> transitions = findTransitions(task);
      UriBuilder builder = uriInfo.getBaseUriBuilder().path("bpm/tasks").path(task.getId());
      Response.ResponseBuilder response = Response.ok();

      if (transitions.size() == 0)
      {
         URI completeHref = builder.clone().path("complete").build();
         response.header("Link", new Link("complete", "signal", completeHref.toString(), null, null));
      }
      else
      {
         for (String transition : transitions)
         {
            if (transition.equals("DEFAULT-TRANSITION"))
            {
               URI completeHref = builder.clone().path("complete").build();
               response.header("Link", new Link("complete", "signal", completeHref.toString(), null, null));
            }
            else
            {
               URI href = builder.clone().path("complete").queryParam("transition", transition).build();
               response.header("Link", new Link(transition, "signal", href.toString(), null, null));
            }
         }
      }
      URI abortHref = builder.clone().path("abort").build();
      response.header("Link", new Link("abort", "abort", abortHref.toString(), null, null));
      addVariablesLink(response, task.getId());
      addNewVariableLink(response, task.getId());
      return response.build();
   }

   @Path("/{taskid}/complete")
   @POST
   public Response complete(@PathParam("taskid") String taskid, @QueryParam("transition") String transition, @Context HttpRequest request)
   {
      Map<String, Object> vars = getVarMap(taskid, request);
      if (transition == null)
      {
         if (vars != null) taskService.completeTask(taskid, vars);
         else taskService.completeTask(taskid);
      }
      else
      {
         if (vars != null) taskService.completeTask(taskid, transition, vars);
         else taskService.completeTask(taskid, transition);
      }
      URI next = uriInfo.getBaseUriBuilder().path("bpm/tasks/next").build();
      return Response.noContent().header("Link", new Link("next", "next", next.toString(), null, null)).build();
   }

   @Path("/{taskid}/abort")
   @POST
   public Response abort(@PathParam("taskid") String taskid, @QueryParam("transition") String transition)
   {
      throw new RuntimeException("Not supported");
   }

   private Set<String> findTransitions(Task task)
   {
      HashSet<String> transitions = new HashSet<String>();
      String activityName = task.getActivityName();
      if (activityName == null) return transitions;
      String pid = task.getExecutionId();
      if (pid == null)
      {
         return transitions;
      }
      String pdid = engine.getExecutionService().findExecutionById(pid).getProcessDefinitionId();
      ProcessDefinitionImpl pd = (ProcessDefinitionImpl) engine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(pdid).uniqueResult();
      Activity current = pd.findActivity(activityName);
      for (Transition transition : current.getOutgoingTransitions())
      {
         if (transition.getCondition() != null) continue;
         String transitionName = transition.getName();
         if (transitionName == null)
         {
            transitionName = "DEFAULT-TRANSITION";
         }
         transitions.add(transitionName);
      }
      return transitions;
   }

   private void addVariablesLink(Response.ResponseBuilder builder, String taskid)
   {
      UriBuilder uri = uriInfo.getBaseUriBuilder()
              .path(ProcessEngineResource.class)
              .path("tasks")
              .path(taskid)
              .path("variables");
      builder.header("Link", new Link("variables", "variables", uri.build().toString(), null, null));
   }

   private void addNewVariableLink(Response.ResponseBuilder builder, String taskid)
   {
      UriBuilder uri = uriInfo.getBaseUriBuilder()
              .path(ProcessEngineResource.class)
              .path("tasks")
              .path(taskid)
              .path("variables");
      builder.header("Link", new Link("variable-template", "variable-template", uri.build().toString() + "/{var}", null, null));
   }


   public Map<String, Object> getVarMap(String taskid, HttpRequest request)
   {
      MediaType mediaType = request.getHttpHeaders().getMediaType();
      if (mediaType == null)
      {
         return null;
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

         Variable var = (Variable) engine.getTaskService().getVariable(taskid, entry.getKey());
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
      return vars;
   }

   @Path("/{taskid}/variables")
   @HEAD
   public Response headVariables(@PathParam("taskid") String taskid)
   {
      Set<String> variableNames = engine.getTaskService().getVariableNames(taskid);
      if (variableNames == null)
      {
         System.out.println("VARIABLE NAMES WERE NULL!!!!");
         return Response.ok().build();
      }

      Map<String, Object> variables = engine.getTaskService().getVariables(taskid, variableNames);

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

   @Path("/{taskid}/variables/{var}")
   @PUT
   public Response createUpdateVariable(@PathParam("taskid") String taskid, @PathParam("var") String varName, @Context HttpHeaders headers, byte[] bytes)
   {
      Variable var = (Variable) engine.getTaskService().getVariable(taskid, varName);
      if (var == null)
      {
         var = new Variable(headers.getMediaType(), headers.getRequestHeaders(), bytes);
         HashMap<String, Object> vars = new HashMap<String, Object>();
         vars.put(varName, var);
         engine.getTaskService().setVariables(taskid, vars);
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

   @Path("/{taskid}/variables")
   @POST
   public void setVariableLink(@PathParam("taskid") String taskid, @HeaderParam("Set-Link") LinkHeader links)
   {
      if (links == null) return;
      HashMap<String, Object> vars = new HashMap<String, Object>();
      for (Link link : links.getLinks())
      {

         if (link.getTitle() != null)
         {
            vars.put(link.getTitle(), link);
         }
         else if (link.getRelationship() != null)
         {
            vars.put(link.getRelationship(), link);
         }
      }
      taskService.setVariables(taskid, vars);
   }

   @Path("/{taskid}/variables/{var}")
   @GET
   public Response getVariable(@PathParam("taskid") String taskid, @PathParam("var") String varName, @Context HttpHeaders header)
   {
      Object obj = engine.getTaskService().getVariable(taskid, varName);
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
