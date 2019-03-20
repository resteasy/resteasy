package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.resteasy.utils.TestUtil;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;

/**
 * @author Marek Kopecky mkopecky@redhat.com
 */
public class DebugLoggingServerSetup implements ServerSetupTask {

   @Override
   public void setup(ManagementClient managementClient, String s) throws Exception {
      OnlineManagementClient client = TestUtil.clientInit();

      // enable RESTEasy debug logging
      TestUtil.runCmd(client, "/subsystem=logging/console-handler=CONSOLE:write-attribute(name=level,value=ALL)");
      TestUtil.runCmd(client, "/subsystem=logging/logger=org.jboss.resteasy:add(level=ALL)");

      client.close();
   }

   @Override
   public void tearDown(ManagementClient managementClient, String s) throws Exception {
      OnlineManagementClient client = TestUtil.clientInit();

      // enable RESTEasy debug logging
      TestUtil.runCmd(client, "/subsystem=logging/console-handler=CONSOLE:write-attribute(name=level,value=INFO)");
      TestUtil.runCmd(client, "/subsystem=logging/logger=org.jboss.resteasy:remove()");

      client.close();
   }
}
