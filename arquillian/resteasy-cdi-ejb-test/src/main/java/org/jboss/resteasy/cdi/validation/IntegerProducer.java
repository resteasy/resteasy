package org.jboss.resteasy.cdi.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 25, 2012
 */
@ApplicationScoped
public class IntegerProducer
{     
   @Produces   
   @NumberOneBinding
   int numberOne = 5;
   
   @Produces   
   @NumberOneErrorBinding
   int numberOneError = 10;
   
   @Produces   
   @NumberTwoBinding
   int numberTwo = 15;
}
