package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.resteasy.utils.ReasteasyTestUtil;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;

/**
 * @author Marek Kopecky mkopecky@redhat.com
 */
public class DebugLoggingServerSetup implements ServerSetupTask {

   @Override
   public void setup(ManagementClient managementClient, String s) throws Exception {
      OnlineManagementClient client = ReasteasyTestUtil.clientInit();

      // enable RESTEasy debug logging
      ReasteasyTestUtil.runCmd(client, "/subsystem=logging/console-handler=CONSOLE:write-attribute(name=level,value=ALL)");
      ReasteasyTestUtil.runCmd(client, "/subsystem=logging/logger=org.jboss.resteasy:add(level=ALL)");

      client.close();
   }

   @Override
   public void tearDown(ManagementClient managementClient, String s) throws Exception {
      OnlineManagementClient client = ReasteasyTestUtil.clientInit();

      // enable RESTEasy debug logging
      ReasteasyTestUtil.runCmd(client, "/subsystem=logging/console-handler=CONSOLE:write-attribute(name=level,value=INFO)");
      ReasteasyTestUtil.runCmd(client, "/subsystem=logging/logger=org.jboss.resteasy:remove()");

      client.close();
   }
}
