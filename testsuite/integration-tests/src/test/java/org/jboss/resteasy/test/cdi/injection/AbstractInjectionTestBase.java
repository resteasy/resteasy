package org.jboss.resteasy.test.cdi.injection;

import org.jboss.resteasy.utils.ReasteasyTestUtil;
import org.junit.AfterClass;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

/**
 * Prepare server for injection tests. Add test queue and disable security.
 */
public class AbstractInjectionTestBase {

   public static void initQueue() throws Exception {
      OnlineManagementClient client = ReasteasyTestUtil.clientInit();

      // disable security and create queue
      if (ReasteasyTestUtil.isWildFly9x()) {
         ReasteasyTestUtil.runCmd(client, "/subsystem=messaging/hornetq-server=default:write-attribute(name=security-enabled,value=false)");
         ReasteasyTestUtil.runCmd(client, "/subsystem=messaging/hornetq-server=default/jms-queue=test:add(entries=[java:/jms/queue/test])");
      } else {
         ReasteasyTestUtil.runCmd(client, "/subsystem=messaging-activemq/server=default:write-attribute(name=security-enabled,value=false)");
         ReasteasyTestUtil.runCmd(client, "/subsystem=messaging-activemq/server=default/jms-queue=test:add(entries=[java:/jms/queue/test])");
      }

      // reload server
      Administration admin = new Administration(client, 240);
      admin.reload();

      client.close();
   }

   @AfterClass
   public static void destroyQueue() throws Exception {
      OnlineManagementClient client = ReasteasyTestUtil.clientInit();

      // remove queue and enable security
      if (ReasteasyTestUtil.isWildFly9x()) {
         ReasteasyTestUtil.runCmd(client, "/subsystem=messaging/hornetq-server=default:write-attribute(name=security-enabled,value=true)");
         ReasteasyTestUtil.runCmd(client, "/subsystem=messaging/hornetq-server=default/jms-queue=test:remove");
      } else {
         ReasteasyTestUtil.runCmd(client, "/subsystem=messaging-activemq/server=default:write-attribute(name=security-enabled,value=true)");
         ReasteasyTestUtil.runCmd(client, "/subsystem=messaging-activemq/server=default/jms-queue=test:remove");
      }

      // reload server
      Administration admin = new Administration(client, 240);
      admin.reload();

      client.close();
   }
}
