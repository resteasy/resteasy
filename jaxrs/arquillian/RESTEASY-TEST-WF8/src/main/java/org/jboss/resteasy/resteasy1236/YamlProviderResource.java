package org.jboss.resteasy.resteasy1236;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Date;

@Path("/yaml")
public class YamlProviderResource {


    public static YamlProviderObject createMyObject() {

        YamlProviderObject obj = new YamlProviderObject();

        obj.setSomeText("This is some sample text");
        obj.setDate(new Date(123456789));
        obj.getNested().setMoreText("This is some more sample text");

        return obj;
    }

    @GET
    @Produces("text/x-yaml")
    public YamlProviderObject getMyObject() {
        return createMyObject();
    }


    @POST
    @Consumes("text/x-yaml")
    @Produces("text/x-yaml")
    public YamlProviderObject setMyObject(YamlProviderObject obj) {
        return obj;
    }


}
