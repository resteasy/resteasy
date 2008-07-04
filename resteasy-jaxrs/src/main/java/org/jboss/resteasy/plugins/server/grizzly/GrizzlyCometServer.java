package org.jboss.resteasy.plugins.server.grizzly;

import com.sun.grizzly.arp.DefaultAsyncHandler;
import com.sun.grizzly.comet.CometAsyncFilter;
import com.sun.grizzly.container.GrizzletAdapter;
import com.sun.grizzly.grizzlet.Grizzlet;
import com.sun.grizzly.http.AsyncHandler;
import com.sun.grizzly.http.SelectorThread;
import com.sun.grizzly.standalone.StaticStreamAlgorithm;

import javax.servlet.ServletException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GrizzlyCometServer extends GrizzlyServer
{
   protected String path;
   protected Grizzlet grizzlet;

   public String getPath()
   {
      return path;
   }

   public void setPath(String path)
   {
      this.path = path;
   }

   public Grizzlet getGrizzlet()
   {
      return grizzlet;
   }

   public void setGrizzlet(Grizzlet grizzlet)
   {
      this.grizzlet = grizzlet;
   }

   protected void initSelectorThread() throws ServletException
   {
      selectorThread = new SelectorThread();
      selectorThread.setAlgorithmClassName(StaticStreamAlgorithm.class.getName());
      selectorThread.setPort(port);
      selectorThread.setMaxThreads(5);
      selectorThread.setDisplayConfiguration(false);
      selectorThread.setEnableAsyncExecution(true);
      selectorThread.setBufferResponse(false);
      selectorThread.setFileCacheIsEnabled(false);
      selectorThread.setLargeFileCacheEnabled(false);
      AsyncHandler asyncHandler = new DefaultAsyncHandler();
      asyncHandler.addAsyncFilter(new CometAsyncFilter());
      selectorThread.setAsyncHandler(asyncHandler);


      GrizzletAdapter adapter = new GrizzletAdapter("path");
      adapter.setGrizzlet(grizzlet);
      selectorThread.setAdapter(adapter);

   }
}
