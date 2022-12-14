package org.jboss.resteasy.test.cdi.basic.resource;

import java.util.concurrent.Future;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.Dependent;

@Stateless
@Dependent
public class AsynchronousStateless implements AsynchronousStatelessLocal {
    @Override
    public Future<Boolean> asynch() throws InterruptedException {
        Thread.sleep(AsynchronousResource.DELAY);
        return new AsyncResult<Boolean>(true);
    }
}
