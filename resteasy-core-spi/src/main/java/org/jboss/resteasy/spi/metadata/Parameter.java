package org.jboss.resteasy.spi.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

import org.jboss.resteasy.spi.util.Types;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class Parameter {
    public enum ParamType {
        BEAN_PARAM,
        CONTEXT,
        COOKIE_PARAM,
        FORM_PARAM,
        HEADER_PARAM,
        MATRIX_PARAM,
        MESSAGE_BODY,
        PATH_PARAM,
        QUERY_PARAM,
        SUSPENDED,
        UNKNOWN,
        // resteasy specific
        FORM,
        QUERY
    }

    protected ResourceClass resourceClass;
    protected Class<?> type;
    protected Type genericType;
    protected ParamType paramType = ParamType.UNKNOWN;
    protected String paramName;
    protected boolean encoded;
    protected String defaultValue;

    protected Parameter(final ResourceClass resourceClass, final Class<?> type, final Type genericType) {
        this.resourceClass = resourceClass;
        this.genericType = Types.resolveTypeVariables(resourceClass.getClazz(), genericType);
        this.type = Types.getRawType(this.genericType);
    }

    public ResourceClass getResourceClass() {
        return resourceClass;
    }

    public void setResourceClass(ResourceClass resourceClass) {
        this.resourceClass = resourceClass;
    }

    public Class<?> getType() {
        return type;
    }

    public Type getGenericType() {
        return genericType;
    }

    public ParamType getParamType() {
        return paramType;
    }

    public String getParamName() {
        return paramName;
    }

    public boolean isEncoded() {
        return encoded;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setParamType(ParamType paramType) {
        this.paramType = paramType;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setEncoded(boolean encoded) {
        this.encoded = encoded;
    }

    public abstract AccessibleObject getAccessibleObject();

    public abstract Annotation[] getAnnotations();
}
