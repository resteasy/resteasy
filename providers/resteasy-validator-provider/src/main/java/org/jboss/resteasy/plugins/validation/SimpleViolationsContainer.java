package org.jboss.resteasy.plugins.validation;

import java.io.Serializable;
import java.util.Set;

import jakarta.validation.ConstraintViolation;

import org.jboss.resteasy.api.validation.ConstraintType;


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
   private boolean parametersValidated;
   private ConstraintTypeUtilImpl util = new ConstraintTypeUtilImpl();

   public SimpleViolationsContainer(final Object target)
   {
      super(target);
   }

   public SimpleViolationsContainer(final Set<ConstraintViolation<Object>> cvs)
   {
      super(cvs);
   }

   /**
    * If some ConstraintViolations are created by Resteasy and some are created by CDI, two
    * essentially identical ones might appear to be different. For example, those created by Resteasy
    * might refer to CDI proxy classes while those created by CDI might refer to the backing java classes.
    */
   public void addViolations(Set<ConstraintViolation<Object>> cvs)
   {
      if (cvs.size() == 0)
      {
         return;
      }
      ConstraintViolation<Object> cv = cvs.iterator().next();
      ConstraintType.Type type = util.getConstraintType(cv);
      if ((ConstraintType.Type.CLASS.equals(type) || ConstraintType.Type.PROPERTY.equals(type)) && isFieldsValidated())
      {
          return;
       }
       if (ConstraintType.Type.PARAMETER.equals(type))
       {
          if (parametersValidated)
          {
             return;
          }
          parametersValidated = true;
       }
       getViolations().addAll(cvs);
       return;
   }
}
