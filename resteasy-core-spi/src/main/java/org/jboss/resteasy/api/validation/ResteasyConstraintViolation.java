package org.jboss.resteasy.api.validation;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.api.validation.ConstraintType.Type;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Jun 4, 2013
 */
@XmlRootElement(name = "resteasyConstraintViolation")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResteasyConstraintViolation implements Serializable {
    private static final long serialVersionUID = -5441628046215135260L;

    private Type constraintType;

    private String path;

    private String message;

    private String value;

    public ResteasyConstraintViolation(final Type constraintType, final String path, final String message, final String value) {
        this.constraintType = constraintType;
        this.path = path;
        this.message = message;
        this.value = value;
    }

    public ResteasyConstraintViolation() {
    }

    /**
     * @return type of constraint
     */
    public Type getConstraintType() {
        return constraintType;
    }

    /**
     * @return description of element violating constraint
     */
    public String getPath() {
        return path;
    }

    /**
     * @return description of constraint violation
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return object in violation of constraint
     */
    public String getValue() {
        return value;
    }

    /**
     * @return String representation of violation
     */
    public String toString() {
        return "[" + type() + "]\r[" + path + "]\r[" + message + "]\r[" + value + "]\r";
    }

    /**
     * @return String form of violation type
     */
    public String type() {
        return constraintType.toString();
    }

    public void setConstraintType(Type constraintType) {
        this.constraintType = constraintType;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
