package org.jboss.resteasy.test.rx.resource;

import org.jboss.dmr.ModelNode;
import org.jboss.resteasy.utils.TestUtil;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public class AllowTrace {

	private static Address address = Address.subsystem("undertow").and("server", "default-server").and("http-listener", "default");

	public static ModelNode turnOn() throws Exception {
		OnlineManagementClient client = TestUtil.clientInit();
		Administration admin = new Administration(client);
		Operations ops = new Operations(client);

		// get original 'disallowed methods' value
		ModelNode origDisallowedMethodsValue = ops.readAttribute(address, "disallowed-methods").value();
		// set 'disallowed methods' to empty list to allow TRACE
		ops.writeAttribute(address, "disallowed-methods", new ModelNode().setEmptyList());

		// reload server
		admin.reload();
		client.close();
		return origDisallowedMethodsValue;
	}

	public static void turnOff(ModelNode origDisallowedMethodsValue) throws Exception {
		OnlineManagementClient client = TestUtil.clientInit();
		Administration admin = new Administration(client);
		Operations ops = new Operations(client);

		// write original 'disallowed methods' value
		ops.writeAttribute(address, "disallowed-methods", origDisallowedMethodsValue);

		// reload server
		admin.reload();
		client.close();
	}
}
