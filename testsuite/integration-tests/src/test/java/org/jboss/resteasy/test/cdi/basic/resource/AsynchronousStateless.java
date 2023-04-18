package org.jboss.resteasy.test.cdi.basic.resource;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;

@Stateless
@Dependent
public class AsynchronousStateless implements AsynchronousStatelessLocal {
    @Override
    public Future<Boolean> asynch() throws InterruptedException {
        Thread.sleep(AsynchronousResource.DELAY);
        return new AsyncResult<Boolean>(true);
    }
}
