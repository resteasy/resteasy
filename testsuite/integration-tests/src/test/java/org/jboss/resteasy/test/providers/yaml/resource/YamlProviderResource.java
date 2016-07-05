package org.jboss.resteasy.test.providers.yaml.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/yaml")
public class YamlProviderResource {


    public static YamlProviderObject createMyObject() {

        YamlProviderObject obj = new YamlProviderObject();

        obj.setSomeText("This is some sample text");
        obj.setDate(new Date(123456789));
        obj.getNested().setMoreText("This is some more sample text");

        Map<String, YamlProviderNestedObject> dataMap = new HashMap<String, YamlProviderNestedObject>();
        YamlProviderNestedObject mno = new YamlProviderNestedObject();
        mno.setMoreText("blah");
        dataMap.put("fooBar", mno);

        obj.setData(dataMap);

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

    @POST
    @Path("/list")
    @Consumes("text/x-yaml")
    @Produces("text/plain")
    public String populate(List<String> data) {
       return data.toString();
    }
}
