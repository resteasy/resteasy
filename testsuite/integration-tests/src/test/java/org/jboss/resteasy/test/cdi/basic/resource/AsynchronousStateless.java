package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ejb.AsyncResult;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import java.util.concurrent.Future;

@Stateless
@Dependent
public class AsynchronousStateless implements AsynchronousStatelessLocal {
    @Override
    public Future<Boolean> asynch() throws InterruptedException {
        Thread.sleep(AsynchronousResource.DELAY);
        return new AsyncResult<Boolean>(true);
    }
}
