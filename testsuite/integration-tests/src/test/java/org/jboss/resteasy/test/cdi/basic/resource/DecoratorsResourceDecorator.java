package org.jboss.resteasy.test.cdi.basic.resource;

import java.util.logging.Logger;

import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@Decorator
public abstract class DecoratorsResourceDecorator implements DecoratorsResourceIntf {
    @Inject
    private Logger log;

    @Inject
    @Delegate
    private DecoratorsResource resource;

    @Override
    public Response createBook(EJBBook book) {
        log.info("entering DecoratorsResourceDecorator.createBook()");
        DecoratorsVisitList.add(DecoratorsVisitList.RESOURCE_DECORATOR_ENTER);
        Response response = resource.createBook(book);
        DecoratorsVisitList.add(DecoratorsVisitList.RESOURCE_DECORATOR_LEAVE);
        log.info("leaving DecoratorsResourceDecorator.createBook()");
        return response;
    }

    @Override
    public EJBBook lookupBookById(int id) {
        log.info("entering DecoratorsResourceDecorator.lookupBookById()");
        DecoratorsVisitList.add(DecoratorsVisitList.RESOURCE_DECORATOR_ENTER);
        EJBBook book = resource.lookupBookById(id);
        DecoratorsVisitList.add(DecoratorsVisitList.RESOURCE_DECORATOR_LEAVE);
        log.info("leaving DecoratorsResourceDecorator.lookupBookById()");
        return book;
    }

    @Override
    public abstract Response test();
}
