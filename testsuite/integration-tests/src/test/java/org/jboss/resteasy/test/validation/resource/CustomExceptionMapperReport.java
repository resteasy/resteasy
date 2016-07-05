package org.jboss.resteasy.test.validation.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "testReport")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomExceptionMapperReport {
    private int fieldViolations;
    private int propertyViolations;
    private int classViolations;
    private int parameterViolations;
    private int returnValueViolations;

    public int getFieldViolations() {
        return fieldViolations;
    }

    public void setFieldViolations(int fieldViolations) {
        this.fieldViolations = fieldViolations;
    }

    public int getPropertyViolations() {
        return propertyViolations;
    }

    public void setPropertyViolations(int propertyViolations) {
        this.propertyViolations = propertyViolations;
    }

    public int getClassViolations() {
        return classViolations;
    }

    public void setClassViolations(int classViolations) {
        this.classViolations = classViolations;
    }

    public int getParameterViolations() {
        return parameterViolations;
    }

    public void setParameterViolations(int parameterViolations) {
        this.parameterViolations = parameterViolations;
    }

    public int getReturnValueViolations() {
        return returnValueViolations;
    }

    public void setReturnValueViolations(int returnValueViolations) {
        this.returnValueViolations = returnValueViolations;
    }
}
