package org.jboss.resteasy.test.validation.ejb.resource;

import javax.validation.constraints.NotNull;

public class EJBParameterViolationsOnlyDataObject extends EJBParameterViolationsOnlyAbstractDataObject
{
    @NotNull
    private String direction;

    public int getSpeed()
    {
        return speed;
    }

    public String getDirection()
    {
        return direction;
    }

    public void setSpeed(int number)
    {
        this.speed = number;
    }

    public void setDirection(String direction)
    {
        this.direction = direction;
    }
}
