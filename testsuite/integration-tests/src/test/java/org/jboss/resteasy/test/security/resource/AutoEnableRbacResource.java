/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.security.resource;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Resource used by {@link org.jboss.resteasy.test.security.AutoEnableRbacTest} to verify RESTEASY-3789:
 * RBAC enforcement is active by default (no {@code resteasy.role.based.security} property needed).
 *
 * <ul>
 * <li>{@code /rbac-auto/roles-allowed} — {@code @RolesAllowed("ROLE_B")}</li>
 * <li>{@code /rbac-auto/deny-all} — {@code @DenyAll}</li>
 * <li>{@code /rbac-auto/permit-all} — {@code @PermitAll}</li>
 * </ul>
 */
@Path("/rbac-auto")
@Produces(MediaType.TEXT_PLAIN)
public class AutoEnableRbacResource {

    @GET
    @Path("/roles-allowed")
    @RolesAllowed("ROLE_B")
    public String rolesAllowed() {
        return "ok-roles-allowed";
    }

    @GET
    @Path("/deny-all")
    @DenyAll
    public String denyAll() {
        return "SHOULD NOT BE REACHED";
    }

    @GET
    @Path("/permit-all")
    @PermitAll
    public String permitAll() {
        return "ok-permit-all";
    }
}
