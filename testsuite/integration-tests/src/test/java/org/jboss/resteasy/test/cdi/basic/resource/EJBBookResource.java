package org.jboss.resteasy.test.cdi.basic.resource;

import org.jboss.resteasy.test.cdi.util.Constants;
import org.jboss.resteasy.test.cdi.util.Counter;
import org.jboss.resteasy.test.cdi.util.CounterBinding;
import org.jboss.resteasy.test.cdi.util.Utilities;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.logging.Logger;

@Stateless
@Dependent
@Path("/")
public class EJBBookResource implements EJBLocalResource, EJBRemoteResource {
    private static HashMap<Integer, EJBBook> books = new HashMap<>();

    @Inject
    private Logger log;
    @Inject
    private Utilities utilities;
    @Inject
    @CounterBinding
    private Counter counter;

    @Inject
    private EJBBookReader readerCDI;     // EJBBookReaderImpl implements @Local interface EJBBookReader
    @EJB
    private EJBBookReader readerEJB;
    @Inject
    private EJBBookWriterImpl writerCDI; // EJBBookWriterImpl has a no-interface view
    @EJB
    private EJBBookWriterImpl writerEJB;

    @GET
    @Override
    @Path("verifyScopes")
    @Produces(MediaType.TEXT_PLAIN)
    public int verifyScopes() {
        log.info("entering verifyScopes()");
        log.info("EJBBookReader scope:      " + utilities.getScope(EJBBookReader.class));
        log.info("EJBBookReaderImpl scope:  " + utilities.getScope(EJBBookReaderImpl.class));
        log.info("EJBBookWriterImpl scope:  " + utilities.getScope(EJBBookWriterImpl.class));
        log.info("EJBLocalResource scope:   " + utilities.getScope(EJBLocalResource.class));
        log.info("EJBRemoteResource scope:  " + utilities.getScope(EJBRemoteResource.class));
        log.info("EJBBookResource scope:    " + utilities.getScope(EJBBookResource.class));

        boolean result = true;
        result &= utilities.isApplicationScoped(EJBBookReader.class);
        result &= utilities.isApplicationScoped(EJBBookWriterImpl.class);
        result &= utilities.isDependentScoped(EJBLocalResource.class);
        return result ? 200 : 500;
    }

    @GET
    @Override
    @Path("verifyInjection")
    @Produces(MediaType.TEXT_PLAIN)
    public int verifyInjection() {
        log.info("entering verifyInjection()");
        log.info("readerCDI: " + readerCDI);
        log.info("readerEJB: " + readerEJB);
        log.info("writerCDI: " + writerCDI);
        log.info("writerEJB: " + writerEJB);

        boolean result = true;
        result &= readerCDI != null;
        result &= readerEJB != null;
        result &= writerCDI != null;
        result &= writerEJB != null;
        return result ? 200 : 500;
    }

    @POST
    @Path("create")
    @Consumes(Constants.MEDIA_TYPE_TEST_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public int createBook(EJBBook book) {
        log.info("entering createBook()");
        int id = counter.getNext();
        book.setId(id);
        books.put(id, book);
        log.info("stored: " + id + "->" + book);
        return id;
    }

    @GET
    @Path("book/{id:[0-9][0-9]*}")
    @Produces(Constants.MEDIA_TYPE_TEST_XML)
    public EJBBook lookupBookById(@PathParam("id") int id) {
        log.info("entering lookupBookById(" + id + ")");
        log.info("books: " + books);
        EJBBook book = books.get(id);
        if (book == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return book;
    }

    @GET
    @Path("uses/{count}")
    @Produces(MediaType.TEXT_PLAIN)
    public int testUse(@PathParam("count") int count) {
        log.info("entering testUse()");
        log.info("readerEJB uses: " + readerEJB.getUses());
        log.info("writerEJB uses: " + writerEJB.getUses());
        log.info("readerCDI uses: " + readerCDI.getUses());
        log.info("writerCDI uses: " + writerCDI.getUses());
        int readerUses = readerCDI.getUses();
        int writerUses = writerCDI.getUses();
        readerCDI.reset();
        writerCDI.reset();
        return (readerUses == count && writerUses == count) ? 200 : 500;
    }

    @GET
    @Path("reset")
    public void reset() {
        log.info("entering reset()");
        counter.reset();
    }
}

