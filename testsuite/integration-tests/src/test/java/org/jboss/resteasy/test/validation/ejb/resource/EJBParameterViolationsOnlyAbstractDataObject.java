package org.jboss.resteasy.test.validation.ejb.resource;

import java.io.Serializable;

import javax.validation.constraints.Min;

public abstract class EJBParameterViolationsOnlyAbstractDataObject implements Serializable
{
   private static final long serialVersionUID = 1L;

   @Min(1)
   protected int speed;

   public abstract int getSpeed();

   public abstract void setSpeed(int speed);
}
