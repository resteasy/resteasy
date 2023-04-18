package org.jboss.resteasy.test.cdi.basic.resource;

import java.util.concurrent.Future;

import javax.ejb.Local;

@Local
public interface AsynchronousStatelessLocal {
    Future<Boolean> asynch() throws InterruptedException;
}
