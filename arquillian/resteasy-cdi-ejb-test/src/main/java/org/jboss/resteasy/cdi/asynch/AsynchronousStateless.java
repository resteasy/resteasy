package org.jboss.resteasy.cdi.asynch;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 24, 2012
 */
@Stateless
@Dependent
public class AsynchronousStateless implements AsynchronousStatelessLocal
{
   @Override
   public Future<Boolean> asynch() throws InterruptedException
   {
      Thread.sleep(2000);
      return new AsyncResult<Boolean>(true);
   }
}
