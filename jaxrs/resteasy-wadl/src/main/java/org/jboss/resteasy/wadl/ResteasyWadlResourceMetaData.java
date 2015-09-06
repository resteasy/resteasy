package org.jboss.resteasy.wadl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class ResteasyWadlResourceMetaData {
    private String uri;
    private List<ResteasyWadlMethodMetaData> methodsMetaData = new ArrayList<ResteasyWadlMethodMetaData>();

    public ResteasyWadlResourceMetaData(String uri) {
        this.uri = uri;
    }

    public void addMethodMetaData(ResteasyWadlMethodMetaData methodMetaData) {
        this.methodsMetaData.add(methodMetaData);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<ResteasyWadlMethodMetaData> getMethodsMetaData() {
        return methodsMetaData;
    }
}
