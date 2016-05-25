package org.jboss.resteasy.plugins.providers.html;

import org.jboss.resteasy.plugins.providers.html.i18n.Messages;
import org.jboss.resteasy.spi.InternalServerErrorException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class View implements Renderable
{

   /**
    * If left unspecified, the default name of the model in the request
    * attributes
    */
   public static final String DEFAULT_MODEL_NAME = "model";

   /** */
   protected String path;
   protected Map<String, Object> model = new HashMap<String, Object>();

   /**
    * Creates a view without a model.
    * 
    * @param path
    *           will be dispatched to using the servlet container; it should
    *           have a leading /.
    */
   public View(String path)
   {
      this(path, null, null);
   }

   public View(String path, Object model)
   {
      this(path, model, DEFAULT_MODEL_NAME);
   }

   public View(String path, Object model, String modelName)
   {
      this.path = path;
      if(modelName != null)
         setValue(modelName, model);
   }

   private Object setValue(String variable, Object model)
   {
      return this.model.put(variable, model);
   }

   public String getPath()
   {
      return this.path;
   }

   private <T> T getFirst(Collection<T> values)
   {
      return values.isEmpty() ? null : values.iterator().next();
   }

   public String getModelName()
   {
      return getFirst(this.model.keySet());
   }

   public Object getModel()
   {
      return getFirst(this.model.values());
   }

   public Map<String, Object> getModelMap()
   {
      return this.model;
   }

   /**
    * Sets up the model in the request attributes, creates a dispatcher, and
    * forwards the request.
    */
   public void render(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException,
         WebApplicationException
   {
      for (Entry<String, Object> entry : model.entrySet())
      {
         request.setAttribute(entry.getKey(), entry.getValue());
      }

      RequestDispatcher disp = request.getRequestDispatcher(path);
      if (disp == null)
         throw new InternalServerErrorException(Messages.MESSAGES.noDispatcherFound(path));

      disp.forward(request, response);
   }
}
