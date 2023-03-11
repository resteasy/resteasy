package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.jboss.logging.Logger;

@Path("/")
public class GetterReturnValueValidatedResourceWithGetterViolation {
    private static Logger logger = Logger.getLogger(GetterReturnValueValidatedResourceWithGetterViolation.class);
    private static int count = 0;
    private static int maxCount = Integer.MAX_VALUE; // initial count of maximal validation request for REST end-point

    @GET
    @Path("get")
    @Size(min = 2, max = 4)
    public String getS() {
        /*
         * Called three times by Hibernate Validator 5 during field, property, class
         * constraint validation.
         */
        logger.info("getS(): count: " + count);
        String s = count++ <= maxCount ? "abc" : "a";
        logger.info("s: " + s);
        return s;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        GetterReturnValueValidatedResourceWithGetterViolation.count = count;
    }

    public static void setMaxCount(int maxCount) {
        GetterReturnValueValidatedResourceWithGetterViolation.maxCount = maxCount;
    }
}
