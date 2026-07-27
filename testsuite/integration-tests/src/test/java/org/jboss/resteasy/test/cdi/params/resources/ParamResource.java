/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params.resources;

import java.io.InputStream;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public interface ParamResource {

    @Path("constructor")
    @POST
    ParamDescriptor constructor(InputStream body);

    @Path("field/{fieldPathParam}")
    @POST
    ParamDescriptor field(InputStream body);

    @Path("method/{methodPathParam}")
    @POST
    ParamDescriptor method(InputStream body);

    @Path("method-param/{methodPathParamPa}")
    @POST
    ParamDescriptor methodParamsAnnotated(InputStream body);
}
