package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.ejb.Local;
import java.util.concurrent.Future;

@Local
public interface AsynchronousStatelessLocal {
   Future<Boolean> asynch() throws InterruptedException;
}
