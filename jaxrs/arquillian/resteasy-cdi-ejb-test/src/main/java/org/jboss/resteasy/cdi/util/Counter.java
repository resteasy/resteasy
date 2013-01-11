package org.jboss.resteasy.cdi.util;

import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;


/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@Singleton
@CounterBinding
@ApplicationScoped
public class Counter
{
   public static final int INITIAL_VALUE = 17;
   private static AtomicInteger counter = new AtomicInteger(INITIAL_VALUE);
   
   public int getNext()
   {
      System.out.println("In Counter: counter: " + counter);
      return counter.getAndIncrement();
   }
   
   public void reset()
   {
      counter.set(INITIAL_VALUE);
   }
}

