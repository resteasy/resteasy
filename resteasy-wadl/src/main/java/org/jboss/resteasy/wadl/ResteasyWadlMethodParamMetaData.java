package org.jboss.resteasy.wadl;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class ResteasyWadlMethodParamMetaData {
    public enum MethodParamType {
        QUERY_PARAMETER, HEADER_PARAMETER, COOKIE_PARAMETER, PATH_PARAMETER, MATRIX_PARAMETER, FORM_PARAMETER, FORM, ENTITY_PARAMETER
    }

    private Class<?> type;
    private String typeName;
    private Annotation[] annotations;

    private MethodParamType paramType;
    private String paramName;

    public ResteasyWadlMethodParamMetaData(Class<?> type, Annotation[] annotations,
                                           MethodParamType paramType, String paramName) {
        super();
        this.type = type;
        this.annotations = annotations;
        this.paramType = paramType;
        this.paramName = paramName;
    }

    public Class<?> getType() {
        return type;
    }

    public String getTypeName() {
        int i = 0;

        return "";
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }

    public MethodParamType getParamType() {
        return paramType;
    }

    public void setParamType(MethodParamType paramType) {
        this.paramType = paramType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

}
