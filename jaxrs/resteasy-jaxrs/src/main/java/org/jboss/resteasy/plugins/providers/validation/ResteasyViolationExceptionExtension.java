package org.jboss.resteasy.plugins.providers.validation;

import java.util.List;

import org.jboss.resteasy.spi.validation.ResteasyViolationException;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Mar 31, 2012
 */
public class ResteasyViolationExceptionExtension extends ResteasyViolationException
{
   private static final long serialVersionUID = -3374526252797501839L;
   
   public ResteasyViolationExceptionExtension(ViolationsContainer<?> container)
   {
      super(container);
   }
   
   // Makes method available in package.
   public List<List<String>> getStrings()
   {
      return super.getStrings();
   }
   
   // Makes method available in package.
   @SuppressWarnings("rawtypes")
   protected ViolationsContainer getViolationsContainer()
   {
      return super.getViolationsContainer();
   }
   
   // Makes method available in package.
   @SuppressWarnings("rawtypes")
   protected void setViolationsContainer(ViolationsContainer container)
   {
      super.setViolationsContainer(container);
   }
}
