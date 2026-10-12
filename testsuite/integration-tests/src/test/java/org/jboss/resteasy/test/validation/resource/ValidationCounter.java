package org.jboss.resteasy.test.validation.resource;

import java.io.Serializable;

@ValidationCounterConstraint
public class ValidationCounter implements Serializable {
    public static final long serialVersionUID = -1068336400309384949L;
    public int count = 0;
}
