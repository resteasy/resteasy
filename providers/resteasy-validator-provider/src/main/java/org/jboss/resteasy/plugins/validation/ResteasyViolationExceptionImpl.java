package org.jboss.resteasy.plugins.validation;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.SimpleViolationsContainer;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.validation.ConstraintTypeUtil;

public class ResteasyViolationExceptionImpl extends ResteasyViolationException
{
   private static final long serialVersionUID = 657697354453281559L;

   public ResteasyViolationExceptionImpl(final Set<? extends ConstraintViolation<?>> constraintViolations)
   {
      super(constraintViolations);
   }

   /**
    * New constructor
    *
    * @param constraintViolations set of constraint violations
    * @param accept list of accept media types
    */
   public ResteasyViolationExceptionImpl(final Set<? extends ConstraintViolation<?>> constraintViolations, final List<MediaType> accept)
   {
      super(constraintViolations, accept);
   }

   /**
    * New constructor
    *
    * @param container violation container
    */
   public ResteasyViolationExceptionImpl(final SimpleViolationsContainer container)
   {
      super(container);
   }

   /**
    * New constructor
    *
    * @param container violation container
    * @param accept list of accept media types
    */

   public ResteasyViolationExceptionImpl(final SimpleViolationsContainer container, final List<MediaType> accept)
   {
      super(container, accept);
   }

   public ResteasyViolationExceptionImpl(final String stringRep)
   {
      super(stringRep);
   }

   public ConstraintTypeUtil getConstraintTypeUtil()
   {
      return new ConstraintTypeUtil11();
   }

   protected ResteasyConfiguration getResteasyConfiguration()
   {
      return ResteasyContext.getContextData(ResteasyConfiguration.class);
   }
}
