package org.jboss.resteasy.test.validation.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class GetterReturnValueValidatedResourceResetCount {
    private static Logger logger = Logger.getLogger(GetterReturnValueValidatedResourceResetCount.class);

    @GET
    @Path("set")
    public void setCount() {
        logger.info("getCount");
        int count = GetterReturnValueValidatedResourceWithGetterViolation.getCount();
        logger.info(String.format("Count = %d", count));
        GetterReturnValueValidatedResourceWithGetterViolation.setCount(0);
        GetterReturnValueValidatedResourceWithGetterViolation.setMaxCount(count > 1 ? count - 2 : 0);
    }
}
