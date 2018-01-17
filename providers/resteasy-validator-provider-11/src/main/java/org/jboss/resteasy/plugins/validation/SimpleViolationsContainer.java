package org.jboss.resteasy.plugins.validation;

import java.io.Serializable;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 18, 2015
 */
public class SimpleViolationsContainer extends org.jboss.resteasy.api.validation.SimpleViolationsContainer implements Serializable
{
   private static final long serialVersionUID = -7895854137980651539L;
   
   public SimpleViolationsContainer(Object target)
   {
      super(target);
   }
   
   public SimpleViolationsContainer(Set<ConstraintViolation<Object>> cvs)
   {
      super(cvs);
   }
}
