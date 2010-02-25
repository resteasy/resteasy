package org.jboss.resteasy.star.bpm;

import org.jbpm.api.ProcessEngine;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/bpm")
public class ProcessEngineResource
{
   private ProcessEngine engine;

   @Context
   private UriInfo uriInfo;

   public ProcessEngineResource(ProcessEngine engine)
   {
      this.engine = engine;
   }

   @Path("/definitions")
   public ProcessDefinitionResource definitions()
   {
      return new ProcessDefinitionResource(engine, uriInfo);
   }

   @Path("/tasks")
   public TaskResource tasks()
   {
      return new TaskResource(engine, uriInfo);
   }
}
