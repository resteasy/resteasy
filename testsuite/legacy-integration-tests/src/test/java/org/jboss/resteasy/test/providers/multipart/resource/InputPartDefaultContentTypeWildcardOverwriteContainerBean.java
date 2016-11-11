package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

public class InputPartDefaultContentTypeWildcardOverwriteContainerBean {
    @FormParam("foo")
    @PartType(MediaType.APPLICATION_XML)
    private InputPartDefaultContentTypeWildcardOverwriteXmlBean foo;

    public InputPartDefaultContentTypeWildcardOverwriteXmlBean getFoo() {
        return foo;
    }

    public void setFoo(InputPartDefaultContentTypeWildcardOverwriteXmlBean foo) {
        this.foo = foo;
    }
}
