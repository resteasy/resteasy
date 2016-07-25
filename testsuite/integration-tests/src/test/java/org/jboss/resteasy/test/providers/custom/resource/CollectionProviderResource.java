package org.jboss.resteasy.test.providers.custom.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.LinkedList;

@Path("resource")
public class CollectionProviderResource {

    private static Logger logger = Logger.getLogger(CollectionProviderResource.class);

    @Path("linkedlist")
    @GET
    public LinkedList<String> checkDirect() {
        LinkedList<String> list = new LinkedList<String>();
        list.add("linked");
        list.add("list");
        return list;
    }

    @Path("response/linkedlist")
    @GET
    public Response checkResponseDirect() {
        LinkedList<String> list = new LinkedList<String>();
        list.add("linked");
        list.add("list");
        return Response.ok(list).build();
    }

    @Path("response/genericentity/linkedlist")
    @GET
    public Response checkResponseGeneric() {
        GenericEntity<LinkedList<String>> gells = checkGeneric();
        return Response.ok(gells).build();
    }

    @Path("genericentity/linkedlist")
    @GET
    public GenericEntity<LinkedList<String>> checkGeneric() {
        LinkedList<String> list = new LinkedList<String>();
        list.add("linked");
        list.add("list");
        GenericEntity<LinkedList<String>> gells = null;
        Method method = getMethodByName("checkDirect");
        if (method == null) {
            throw new WebApplicationException("No method in the Resource to bind the request", 500);
        }
        gells = new GenericEntity<LinkedList<String>>(list, method.getGenericReturnType());
        return gells;
    }

    private Method getMethodByName(String name) {
        try {
            return getClass().getMethod(name);
        } catch (NoSuchMethodException e) {
            logger.error("No method in the Resource to bind the request", e);
            return null;
        }
    }
}
