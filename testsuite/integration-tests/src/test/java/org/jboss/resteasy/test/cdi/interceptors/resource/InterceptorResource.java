package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.test.cdi.util.Constants;

@Path("/")
@RequestScoped
@Interceptors({ InterceptorOne.class })
@InterceptorClassBinding
@InterceptorLifecycleBinding
public class InterceptorResource {
    private static Map<Integer, InterceptorBook> collection = new HashMap<Integer, InterceptorBook>();
    private static AtomicInteger counter = new AtomicInteger();
    @Inject
    private Logger log;
    @Inject
    private InterceptorStereotyped stereotyped;

    @PostConstruct
    public void postConstruct() {
        log.info("executing InterceptorResource.postConstruct()");
    }

    @jakarta.annotation.PreDestroy
    public void PreDestroy() {
        log.info("executing InterceptorResource.PreDestroy()");
    }

    @POST
    @Path("create")
    @Consumes(Constants.MEDIA_TYPE_TEST_XML)
    @Produces(MediaType.TEXT_PLAIN)
    @Interceptors({ InterceptorTwo.class })
    @InterceptorMethodBinding
    @InterceptorFilterBinding
    public Response createBook(InterceptorBook book) {
        log.info("entering InterceptorResource.createBook()");
        int id = counter.getAndIncrement();
        book.setId(id);
        collection.put(id, book);
        log.info("stored: " + id + "->" + book);
        log.info("leaving InterceptorResource.createBook()");
        return Response.ok(id).build();
    }

    @GET
    @Path("book/{id:[0-9][0-9]*}")
    @Produces(Constants.MEDIA_TYPE_TEST_XML)
    @Interceptors({ InterceptorTwo.class })
    @InterceptorMethodBinding
    @InterceptorFilterBinding
    public InterceptorBook lookupBookById(@PathParam("id") int id) {
        log.info("entering InterceptorResource.lookupBookById(" + id + ")");
        log.info("books: " + collection);
        InterceptorBook book = collection.get(id);
        if (book == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("leaving InterceptorResource.lookupBookById(" + id + ")");
        return book;
    }

    @POST
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    @Interceptors({ InterceptorTwo.class })
    @InterceptorMethodBinding
    public Response test() {
        log.info("entering InterceptorResource.test()");
        stereotyped.test();
        ArrayList<Class<?>> expectedList = new ArrayList<Class<?>>();
        expectedList.add(InterceptorRequestFilterInterceptor.class); // InterceptorRequestFilter.filter()                 0
        expectedList.add(InterceptorBookReaderInterceptorInterceptor.class); // InterceptorBookReaderInterceptor.aroundReadFrom()
        expectedList.add(InterceptorBookReaderInterceptor.class); // InterceptorBookReader.[isReadable() and readFrom()]
        expectedList.add(InterceptorOne.class); // InterceptorBookReader.isReadable()
        expectedList.add(InterceptorThree.class); // InterceptorBookReader.isReadable()
        expectedList.add(InterceptorOne.class); // InterceptorBookReader.readFrom()
        expectedList.add(InterceptorTwo.class); // InterceptorBookReader.readFrom()
        expectedList.add(InterceptorThree.class); // InterceptorBookReader.readFrom()
        expectedList.add(InterceptorFour.class); // InterceptorBookReader.readFrom()
        expectedList.add(InterceptorPostConstructInterceptor.class); // InterceptorResource.postConstruct()
        expectedList.add(InterceptorOne.class); // InterceptorResource.createBook()           10
        expectedList.add(InterceptorTwo.class); // InterceptorResource.createBook()
        expectedList.add(InterceptorThree.class); // InterceptorResource.createBook()
        expectedList.add(InterceptorFour.class); // InterceptorResource.createBook()
        expectedList.add(InterceptorResponseFilterInterceptor.class); // InterceptorResponseFilter.filter()
        expectedList.add(InterceptorBookWriterInterceptorInterceptor.class); // InterceptorBookWriterInterceptor.aroundWriteTo()
        expectedList.add(InterceptorBookWriterInterceptor.class); // InterceptorBookWriter.writeTo()
        expectedList.add(InterceptorPreDestroyInterceptor.class); // InterceptorResource.preDestroy()
        expectedList.add(InterceptorRequestFilterInterceptor.class); // InterceptorRequestFilter.filter()
        expectedList.add(InterceptorPostConstructInterceptor.class); // InterceptorResource.postConstruct()
        expectedList.add(InterceptorOne.class); // InterceptorResource.lookBookById()         20
        expectedList.add(InterceptorTwo.class); // InterceptorResource.lookBookById()
        expectedList.add(InterceptorThree.class); // InterceptorResource.lookBookById()
        expectedList.add(InterceptorFour.class); // InterceptorResource.lookBookById()
        expectedList.add(InterceptorResponseFilterInterceptor.class); // InterceptorResponseFilter.filter()
        expectedList.add(InterceptorOne.class); // InterceptorBookWriter.isWriteable() // Called as initial check
        expectedList.add(InterceptorThree.class); // InterceptorBookWriter.isWriteable() // Called as initial check
        expectedList.add(InterceptorOne.class); // InterceptorBookWriter.isWriteable()
        expectedList.add(InterceptorThree.class); // InterceptorBookWriter.isWriteable()                   30
        expectedList.add(InterceptorBookWriterInterceptorInterceptor.class); // InterceptorBookWriterInterceptor.aroundWriteTo()
        expectedList.add(InterceptorBookWriterInterceptor.class); // InterceptorBookWriter.[isWriteable() and writeTo()]
        expectedList.add(InterceptorOne.class); // InterceptorBookWriter.writeTo()
        expectedList.add(InterceptorThree.class); // InterceptorBookWriter.writeTo()
        expectedList.add(InterceptorOne.class); // InterceptorBookWriter.writeTo()
        expectedList.add(InterceptorTwo.class); // InterceptorBookWriter.writeTo()
        expectedList.add(InterceptorThree.class); // InterceptorBookWriter.writeTo()
        expectedList.add(InterceptorFour.class); // InterceptorBookWriter.writeTo()
        expectedList.add(InterceptorPreDestroyInterceptor.class); // InterceptorResource.preDestroy()
        expectedList.add(InterceptorPostConstructInterceptor.class); // InterceptorResource.postConstruct()
        expectedList.add(InterceptorOne.class); // InterceptorResource.test()
        expectedList.add(InterceptorTwo.class); // InterceptorResource.test()
        expectedList.add(InterceptorThree.class); // InterceptorResource.test()
        expectedList.add(InterceptorFour.class); // InterceptorResource.test()
        expectedList.add(InterceptorThree.class); // Stereotyped.test()
        expectedList.add(InterceptorFour.class); // Stereotyped.test()                         42

        ArrayList<Object> visitList = InterceptorVisitList.getList();
        boolean status = expectedList.size() == visitList.size();
        if (!status) {
            log.info("expectedList.size() [" + expectedList.size() + "] != visitList.size() [" + visitList.size() + "]");
        } else {
            for (int i = 0; i < expectedList.size(); i++) {
                if (!expectedList.get(i).isAssignableFrom(visitList.get(i).getClass())) {
                    status = false;
                    log.info("visitList.get(" + i + ") incorrect: should be an instance of: " + expectedList.get(i) + ", is: "
                            + visitList.get(i));
                    break;
                }
            }
        }
        if (!status) {
            log.info("\rexpected list:");
            for (int i = 0; i < expectedList.size(); i++) {
                log.info(i + ": " + expectedList.get(i).toString());
            }
            log.info("\rvisited list:");
            for (int i = 0; i < visitList.size(); i++) {
                log.info(i + ": " + visitList.get(i).toString());
            }
        }
        log.info("leaving InterceptorResource.test()");
        return status ? Response.ok().build() : Response.serverError().build();
    }

}
