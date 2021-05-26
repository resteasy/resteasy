/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2020, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.resteasy.test.rx.rso;

import java.util.List;

import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.shared.CLIServerSetupTask;
import org.jboss.dmr.ModelNode;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class EnableReactiveExtensionsSetupTask extends CLIServerSetupTask {
    //private static final String MODULE_REACTIVE_STREAMS = "org.reactivestreams";
    //private static final String MODULE_REACTIVE_STREAMS_OPERATORS_API = "org.eclipse.microprofile.reactive-streams-operators.api";
    //private static final String MODULE_REACTIVE_STREAMS_OPERATORS_CORE = "org.eclipse.microprofile.reactive-streams-operators.core";
//    private static final String MODULE_REACTIVE_MESSAGING = "org.wildfly.extension.microprofile.reactive-messaging-smallrye";
    private static final String MODULE_REACTIVE_STREAMS_OPERATORS = "org.wildfly.extension.microprofile.reactive-streams-operators-smallrye";
//    private static final String SUBSYSTEM_REACTIVE_MESSAGING = "microprofile-reactive-messaging-smallrye";
    private static final String SUBSYSTEM_REACTIVE_STREAMS_OPERATORS = "microprofile-reactive-streams-operators-smallrye";

    public EnableReactiveExtensionsSetupTask() {
       System.setProperty("jboss.dist", "/home/rsigal/tmp/git.eap7-1661.kabir/Resteasy/testsuite/integration-tests/target/test-server/wildfly-23.0.2.Final");
    }

    @Override
    public void setup(ManagementClient managementClient, String containerId) throws Exception {
//        boolean rsExt = !containsChild(managementClient, "extension", MODULE_REACTIVE_STREAMS);
//        boolean rsoaExt = !containsChild(managementClient, "extension", MODULE_REACTIVE_STREAMS_OPERATORS_API);
//        boolean rsocExt = !containsChild(managementClient, "extension", MODULE_REACTIVE_STREAMS_OPERATORS_CORE);
        boolean rsoExt = !containsChild(managementClient, "extension", MODULE_REACTIVE_STREAMS_OPERATORS);
        boolean rsoSs = !containsChild(managementClient, "extension", SUBSYSTEM_REACTIVE_STREAMS_OPERATORS);
//        boolean rmExt = !containsChild(managementClient, "extension", MODULE_REACTIVE_MESSAGING);
//        boolean rmSs = !containsChild(managementClient, "extension", SUBSYSTEM_REACTIVE_MESSAGING);

        NodeBuilder nb = this.builder.node(containerId);
//        if (rsExt) {
//           nb.setup("/extension=%s:add", MODULE_REACTIVE_STREAMS);
//        }
//        if (rsoaExt) {
//           nb.setup("/extension=%s:add", MODULE_REACTIVE_STREAMS_OPERATORS_API);
//        }
//        if (rsocExt) {
//           nb.setup("/extension=%s:add", MODULE_REACTIVE_STREAMS_OPERATORS_CORE);
//        }
        if (rsoExt) {
            nb.setup("/extension=%s:add", MODULE_REACTIVE_STREAMS_OPERATORS);
        }
//        if (rmExt) {
//            nb.setup("/extension=%s:add", MODULE_REACTIVE_MESSAGING);
//        }
        if (rsoSs) {
            nb.setup("/subsystem=%s:add", SUBSYSTEM_REACTIVE_STREAMS_OPERATORS);
        }
//        if (rmSs) {
//            nb.setup("/subsystem=%s:add", SUBSYSTEM_REACTIVE_MESSAGING);
//        }
//        if (rmSs) {
//            nb.teardown("/subsystem=%s:remove", SUBSYSTEM_REACTIVE_MESSAGING);
//        }
        if (rsoSs) {
            nb.teardown("/subsystem=%s:remove", SUBSYSTEM_REACTIVE_STREAMS_OPERATORS);
        }
//        if (rmExt) {
//            nb.teardown("/extension=%s:remove", MODULE_REACTIVE_MESSAGING);
//        }
        if (rsoSs) {
            nb.teardown("/extension=%s:remove", MODULE_REACTIVE_STREAMS_OPERATORS);
        }
        super.setup(managementClient, containerId);
    }

    private boolean containsChild(ManagementClient managementClient, String childType, String childName) throws Exception {
        ModelNode op = new ModelNode();
        op.get("operation").set("read-children-names");
        op.get("child-type").set(childType);
        ModelNode result = managementClient.getControllerClient().execute(op);
        if (!result.get("outcome").asString().equals("success")) {
            throw new IllegalStateException(result.asString());
        }
        List<ModelNode> names = result.get("result").asList();
        for (ModelNode name : names) {
            if (name.asString().equals(childName)) {
                return true;
            }
        }
        return false;
    }
}
