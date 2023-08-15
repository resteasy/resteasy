package org.jboss.resteasy.test.exception.resource;

import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Variant;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Provider
@ApplicationScoped
@FollowUpRequired("The @ApplicationScope annotation can be removed once @Provider is a bean defining annotation.")
public class ExceptionMapperInjectionCustomMapper implements ExceptionMapper<ExceptionMapperCustomRuntimeException> {

    private static Logger logger = Logger.getLogger(ExceptionMapperInjectionCustomMapper.class);

    @Inject
    Request request;

    public Response toResponse(ExceptionMapperCustomRuntimeException exception) {
        logger.info("Method: " + request.getMethod());

        ArrayList<Variant> list = new ArrayList<Variant>();
        list.add(new Variant(MediaType.APPLICATION_JSON_TYPE, (String) null, null));
        request.selectVariant(list);
        return Response.status(Response.Status.PRECONDITION_FAILED).build();
    }
}
