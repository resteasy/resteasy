package org.jboss.resteasy.setup;

import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.dmr.ModelNode;
import org.jboss.as.security.Constants;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OPERATION_HEADERS;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ALLOW_RESOURCE_SERVICE_RESTART;
import static org.jboss.as.security.Constants.AUTHENTICATION;
import static org.jboss.as.security.Constants.CODE;
import static org.jboss.as.security.Constants.FLAG;
import static org.jboss.as.security.Constants.SECURITY_DOMAIN;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kanovotn on 1/7/15.
 */
public class UsersRolesSecurityDomainSetup extends AbstractSecurityDomainSetup {

    protected static final String DEFAULT_SECURITY_DOMAIN_NAME = "resteasy-security-ann-tests";
    @Override
    protected String getSecurityDomainName() {
        return DEFAULT_SECURITY_DOMAIN_NAME;
    }
    public boolean isUsersRolesRequired() {
        return true;
    }
    @Override
    public void setup(final ManagementClient managementClient, final String containerId) throws Exception {
        final List<ModelNode> updates = new ArrayList<ModelNode>();
        ModelNode op = new ModelNode();
        op.get(OP).set(ADD);
        op.get(OP_ADDR).add(SUBSYSTEM, "security");
        op.get(OP_ADDR).add(SECURITY_DOMAIN, getSecurityDomainName());
        updates.add(op);
        op = new ModelNode();
        op.get(OP).set(ADD);
        op.get(OP_ADDR).add(SUBSYSTEM, "security");
        op.get(OP_ADDR).add(SECURITY_DOMAIN, getSecurityDomainName());
        op.get(OP_ADDR).add(AUTHENTICATION, Constants.CLASSIC);
        ModelNode loginModule = op.get(Constants.LOGIN_MODULES).add();
        loginModule.get(CODE).set("Remoting");
        if (isUsersRolesRequired()) {
            loginModule.get(FLAG).set("optional");
        } else {
            loginModule.get(FLAG).set("required");
        }
        loginModule.get(Constants.MODULE_OPTIONS).add("password-stacking", "useFirstPass");
        if (isUsersRolesRequired()) {
            loginModule = op.get(Constants.LOGIN_MODULES).add();
            loginModule.get(CODE).set("UsersRoles");
            loginModule.get(FLAG).set("required");
            loginModule.get(Constants.MODULE_OPTIONS).add("password-stacking", "useFirstPass");
        }
        op.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
        updates.add(op);
        applyUpdates(managementClient.getControllerClient(), updates);
    }
}
