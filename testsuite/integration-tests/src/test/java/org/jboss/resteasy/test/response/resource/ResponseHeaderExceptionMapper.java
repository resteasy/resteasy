package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.ArrayList;
import java.util.List;

/**
 * Replace the HEADER "Server" with replacement text.
 */
public class ResponseHeaderExceptionMapper implements ExceptionMapper<ResponseHeaderExceptionMapperRuntimeException> {
   public Response toResponse(ResponseHeaderExceptionMapperRuntimeException exception) {

      List<Object> hList = new ArrayList<>();
      hList.add("WILDFLY/TEN.Full");
      hList.add("AndOtherStuff");

      Response response = Response.status(Response.Status.PRECONDITION_FAILED)
              .entity("My custom headers test").build();
      MultivaluedMap<String, Object> headerMap = response.getHeaders();
      headerMap.put("Server", hList);

      return response;
   }
}
