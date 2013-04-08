package org.jboss.resteasy.cdi.decorators;

import java.util.logging.Logger;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 13, 2012
 */
@Decorator
public abstract class ResourceDecorator implements TestResourceIntf
{
   @Inject private Logger log;
   @Inject private @Delegate TestResource resource;
   
   @Override
   public Response createBook(Book book)
   {
      log.info("entering ResourceDecorator.createBook()");
      VisitList.add(VisitList.RESOURCE_DECORATOR_ENTER);
      Response response = resource.createBook(book);
      VisitList.add(VisitList.RESOURCE_DECORATOR_LEAVE);
      log.info("leaving ResourceDecorator.createBook()");
      return response;
   }

   @Override
   public Book lookupBookById(int id)
   {
      log.info("entering ResourceDecorator.lookupBookById()");
      VisitList.add(VisitList.RESOURCE_DECORATOR_ENTER);
      Book book = resource.lookupBookById(id);
      VisitList.add(VisitList.RESOURCE_DECORATOR_LEAVE);
      log.info("leaving ResourceDecorator.lookupBookById()");
      return book;
   }

   @Override
   public abstract Response test();
}
