package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ApplicationScopeMyDto {
   
    @NotNull
    @Size(min = 1)
    private String test;

    @NotNull
    @Size(min = 1)
    private String path;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
