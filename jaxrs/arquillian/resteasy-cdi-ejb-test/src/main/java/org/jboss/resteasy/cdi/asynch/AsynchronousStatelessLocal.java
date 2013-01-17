package org.jboss.resteasy.cdi.asynch;

import java.util.concurrent.Future;

import javax.ejb.Local;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 22, 2012
 */
@Local
public interface AsynchronousStatelessLocal
{
   public Future<Boolean> asynch() throws InterruptedException;
}

