package org.jboss.resteasy.test.cdi.generic.resource;

import java.util.logging.Logger;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@Decorator
public abstract class ConcreteDecorator implements ConcreteResourceIntf {
    @Inject
    private Logger log;

    private ConcreteResourceIntf resource;

    @Inject
    public ConcreteDecorator(@Delegate final ConcreteResourceIntf resource) {
        this.resource = resource;
    }

    @Override
    public Response execute() {
        log.info("entering ConcreteDecorator.execute()");
        VisitList.add(VisitList.CONCRETE_DECORATOR_ENTER);
        Response response = resource.testGenerics();
        VisitList.add(VisitList.CONCRETE_DECORATOR_LEAVE);
        log.info("leaving ConcreteDecorator.execute()");
        return response;
    }

    @Override
    public abstract Response testDecorators();
}
