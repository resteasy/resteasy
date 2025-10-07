package org.jboss.resteasy.test.cdi.injection.resource;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.jms.DeliveryMode;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.cdi.util.Constants;
import org.jboss.resteasy.test.cdi.util.Counter;

@Path("/")
@RequestScoped
public class CDIInjectionBookResource {
    public static final String BOOK_READER = "BookReader";
    public static final String BOOK_WRITER = "BookWriter";
    public static final String BOOK_RESOURCE = "BookResource";
    public static final String BOOK_READER_DEPENDENT = "BookReaderDependent";
    public static final String BOOK_WRITER_DEPENDENT = "BookWriterDependent";
    public static final String BOOK_RESOURCE_DEPENDENT = "BookResourceDependent";
    public static final String BOOK_READER_STATEFUL = "BookReaderStateless";
    public static final String BOOK_WRITER_STATEFUL = "BookWriterStateless";
    public static final String BOOK_RESOURCE_STATEFUL = "BookResourceStateless";
    public static final String BOOK_RESOURCE_STATEFUL2 = "BookResourceStateless2";
    public static final String COLLECTION = "collection";
    public static final String BOOK_BAG = "bookBag";
    public static final String NEW_BEAN_APPLICATION_SCOPED = "newBeanApplicationScoped";
    public static final String STEREOTYPED_APPLICATION_SCOPED = "stereotypedApplicationScoped";
    public static final String STEREOTYPED_DEPENDENT_SCOPED = "stereotypedDependentScoped";

    private static HashMap<String, Object> store;
    private static AtomicInteger constructCounter = new AtomicInteger();
    private static AtomicInteger destroyCounter = new AtomicInteger();
    @Inject
    @CDIInjectionResourceBinding
    EntityManager em;
    private HashSet<CDIInjectionBook> set = new HashSet<>();
    @Inject
    private BeanManager beanManager;
    @Inject
    private int secret; // used to determine identity
    @Inject
    private CDIInjectionDependentScoped dependent; // dependent scoped managed bean
    @EJB
    private CDIInjectionBookCollection collection; // application scoped singleton: injected as EJB proxy
    // @Note: stateful and stateful2 are two very different objects.
    // stateful is an EJB proxy and stateful2 is a Weld proxy.
    @EJB
    private CDIInjectionStatefulEJB stateful; // dependent scoped SLSB
    @Inject
    private CDIInjectionStatefulEJB stateful2; // dependent scoped SLSB
    @Inject
    private CDIInjectionBookBagLocal bookBag; // session scoped SFSB
    @Inject
    @CDIInjectionResourceBinding
    private Session session;
    @Inject
    @CDIInjectionResourceBinding
    private Queue bookQueue;
    @Inject
    private CDIInjectionNewBean newBean1;
    @Inject
    private CDIInjectionStereotypedApplicationScope stereotypeApplicationScoped;
    @Inject
    private CDIInjectionStereotypedDependentScope stereotypedRequestScoped;
    private Logger log;

    public static HashMap<String, Object> getStore() {
        return store;
    }

    public static void setStore(HashMap<String, Object> store) {
        CDIInjectionBookResource.store = store;
    }

    public HashSet<CDIInjectionBook> getSet() {
        return set;
    }

    public void setSet(HashSet<CDIInjectionBook> set) {
        this.set = set;
    }

    @PreDestroy
    public void preDestroy() {
        destroyCounter.incrementAndGet();
        log.info("preDestroy(): destroyCounter: " + destroyCounter.get());
    }

    @PostConstruct
    public void postConstruct() {
        constructCounter.incrementAndGet();
        log.info("postConstruct(): constructCounter: " + constructCounter.get());
    }

    @Inject
    public void init(Instance<Logger> logInstance) {
        this.log = logInstance.get();
    }

    @GET
    @Path("verifyScopes")
    @Produces(MediaType.TEXT_PLAIN)
    public Response verifyScopes() {
        log.info("entering verifyScopes()");
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        CDIInjectionBookReader reader = CDIInjectionBookReader.class
                .cast(factory.getMessageBodyReader(CDIInjectionBook.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE));
        CDIInjectionBookWriter writer = CDIInjectionBookWriter.class
                .cast(factory.getMessageBodyWriter(CDIInjectionBook.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE));

        if (store == null) {
            log.info("Counter scope:          " + getScope(Counter.class));
            log.info("BookCollection scope:   " + getScope(CDIInjectionBookCollection.class));
            log.info("BookResource scope:     " + getScope(CDIInjectionBookResource.class));
            log.info("BookReader scope:       " + getScope(CDIInjectionBookReader.class));
            log.info("BookWriter scope:       " + getScope(CDIInjectionBookWriter.class));
            log.info("UnscopedResource scope: " + getScope(CDIInjectionUnscopedResource.class));
            log.info("DependentScoped scope:  " + getScope(CDIInjectionDependentScoped.class));
            log.info("StatelessEJB scope:     " + getScope(CDIInjectionStatefulEJB.class));
            log.info("BookBagLocal scope:     " + getScope(CDIInjectionBookBagLocal.class));
            log.info("NewBean scope:          " + getScope(CDIInjectionNewBean.class));
            log.info("stereotypeApplicationScoped: " + getScope(CDIInjectionStereotypedApplicationScope.class));
            log.info("stereotypeRequestScoped:     " + getScope(CDIInjectionStereotypedDependentScope.class));

            store = new HashMap<String, Object>();
            store.put(BOOK_READER, reader);
            store.put(BOOK_WRITER, writer);
            store.put(BOOK_RESOURCE, this);
            store.put(BOOK_READER_DEPENDENT, reader.getDependent());
            store.put(BOOK_WRITER_DEPENDENT, writer.getDependent());
            store.put(BOOK_RESOURCE_DEPENDENT, dependent);
            store.put(BOOK_READER_STATEFUL, reader.getStateful());
            store.put(BOOK_WRITER_STATEFUL, writer.getStateful());
            store.put(BOOK_RESOURCE_STATEFUL, stateful);
            store.put(BOOK_RESOURCE_STATEFUL2, stateful2);
            store.put(COLLECTION, collection);
            store.put(BOOK_BAG, bookBag);
            store.put(NEW_BEAN_APPLICATION_SCOPED, newBean1);
            store.put(STEREOTYPED_APPLICATION_SCOPED, stereotypeApplicationScoped);
            store.put(STEREOTYPED_DEPENDENT_SCOPED, stereotypedRequestScoped);
            return Response.ok().build();
        }

        if (isApplicationScoped(Counter.class) &&
                isApplicationScoped(CDIInjectionBookCollection.class) &&
                isApplicationScoped(CDIInjectionBookReader.class) &&
                isApplicationScoped(CDIInjectionBookWriter.class) &&
                isRequestScoped(CDIInjectionBookResource.class) &&
                isDependentScoped(CDIInjectionDependentScoped.class) &&
                isDependentScoped(CDIInjectionStatefulEJB.class) &&
                isRequestScoped(CDIInjectionUnscopedResource.class) &&
                isSessionScoped(CDIInjectionBookBagLocal.class) &&
                isApplicationScoped(CDIInjectionNewBean.class) &&
                isApplicationScoped(CDIInjectionStereotypedApplicationScope.class) &&
                isDependentScoped(CDIInjectionStereotypedDependentScope.class) &&
                store.get(BOOK_READER) == reader &&
                store.get(BOOK_WRITER) == writer &&
                store.get(BOOK_RESOURCE) != this &&
                store.get(BOOK_READER_DEPENDENT) == reader.getDependent() &&
                store.get(BOOK_WRITER_DEPENDENT) == writer.getDependent() &&
                store.get(BOOK_RESOURCE_DEPENDENT) != dependent &&
                store.get(BOOK_READER_STATEFUL).equals(reader.getStateful()) &&
                store.get(BOOK_WRITER_STATEFUL).equals(writer.getStateful()) &&
                !store.get(BOOK_RESOURCE_STATEFUL).equals(stateful) &&
                !store.get(BOOK_RESOURCE_STATEFUL2).equals(stateful2) &&
                store.get(COLLECTION).equals(collection) &&
                store.get(BOOK_BAG).equals(bookBag) &&
                store.get(NEW_BEAN_APPLICATION_SCOPED).equals(newBean1) &&
                store.get(STEREOTYPED_APPLICATION_SCOPED).equals(stereotypeApplicationScoped) &&
                !store.get(STEREOTYPED_DEPENDENT_SCOPED).equals(stereotypedRequestScoped)) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("empty")
    public void empty() {
        collection.empty();
    }

    @POST
    @Path("create")
    @Consumes("application/test+xml")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createBook(CDIInjectionBook book) {
        log.info("entering createBook()");
        collection.addBook(book);
        log.info("stored: " + book);
        return Response.ok(book.getId()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("books")
    public Collection<CDIInjectionBook> listAllMembers() {
        log.info("entering listAllMembers()");
        log.info("this.theSecret(): " + this.theSecret());
        Collection<CDIInjectionBook> books = collection.getBooks();
        log.info("listAllMembers(): " + books);
        return books;
    }

    @GET
    @Path("book/{id:[0-9][0-9]*}")
    @Produces("application/test+xml")
    public CDIInjectionBook lookupBookById(@PathParam("id") int id) {
        log.info("entering lookupBookById(" + id + ")");
        log.info("books: " + collection.getBooks());
        CDIInjectionBook book = collection.getBook(id);
        if (book == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return book;
    }

    @POST
    @Path("entityManager")
    @Transactional
    public Response testEntityManager() {
        log.info("entering testEntityManager()");
        CDIInjectionBook book1 = collection.getBook(Counter.INITIAL_VALUE);
        CDIInjectionBook book2 = em.find(CDIInjectionBook.class, Counter.INITIAL_VALUE);
        return book1.equals(book2) ? Response.ok().build() : Response.serverError().build();

    }

    @POST
    @Path("session/add")
    public Response sessionAdd(@Context HttpServletRequest request, CDIInjectionBook book) {
        log.info("entering sessionAdd()");
        log.info("new session: " + request.getSession().isNew());
        bookBag.addBook(book);
        return Response.ok().build();
    }

    @GET
    @Path("session/get")
    @Produces(MediaType.APPLICATION_XML)
    public Collection<CDIInjectionBook> sessionGetBag(@Context HttpServletRequest request) {
        log.info("entering sessionGetBag()");
        log.info("new session: " + request.getSession().isNew());
        Collection<CDIInjectionBook> books = bookBag.getContents();
        log.info("sessionGetBag(): " + books);
        request.getSession().invalidate();
        return books;
    }

    @POST
    @Path("session/test")
    public Response sessionTest(@Context HttpServletRequest request) {
        log.info("entering sessionTest()");
        log.info("new session: " + request.getSession().isNew());
        Collection<CDIInjectionBook> contents = bookBag.getContents();
        log.info("bookBag: " + contents);
        if (request.getSession().isNew() && contents.isEmpty()) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("mdb/get")
    @Produces(MediaType.APPLICATION_XML)
    public GenericEntity<Collection<CDIInjectionBook>> mdbGetBag() {
        log.info("entering mdbGetBag()");
        Collection<CDIInjectionBook> books = bookBag.getContents();
        log.info("sessionGetBag(): " + books);
        return new GenericEntity<Collection<CDIInjectionBook>>(books) {
        };
    }

    @GET
    @Path("getCounters")
    public String getCounters() {
        return Integer.toString(constructCounter.get()) + ":" + Integer.toString(destroyCounter.get()) + ":";
    }

    @GET
    @Path("disposer")
    public Response testDisposer() {
        log.info("entering testDisposer()");
        return CDIInjectionResourceProducer.isDisposed() ? Response.ok().build() : Response.serverError().build();
    }

    @POST
    @Path("produceMessage")
    @Consumes("application/test+xml")
    @Produces(MediaType.TEXT_PLAIN)
    public Response produceBookMessage(CDIInjectionBook book) {
        log.info("entering produceBookMessage()");
        try {
            log.info("queue: " + bookQueue);
            log.info("ResourceProducer scope: " + getScope(CDIInjectionResourceProducer.class));
            log.info("queue scope: " + getScope(Queue.class));
            MessageProducer producer = session.createProducer(bookQueue);
            TextMessage message = session.createTextMessage(book.getName());
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.send(message);
            log.info("sent: " + message.getText());
            return Response.ok().build();
        } catch (JMSException e) {
            log.info(String.format("Stacktrace: %s", (Object[]) e.getStackTrace()));
            return Response.serverError().entity("JMS error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("queue/consumeMessage")
    @Produces(MediaType.TEXT_PLAIN)
    public Response consumeBookMessageFromQueue() {
        log.info("entering consumeBookMessageFromQueue() xxx");
        log.info("getting consumer");
        try {
            MessageConsumer consumer = session.createConsumer(bookQueue);
            log.info("got consumer");
            TextMessage message = (TextMessage) consumer.receiveNoWait();
            log.info("message: " + message);
            if (message == null) {
                return Response.serverError().build();
            }
            log.info("message text: " + message.getText());
            return Response.ok(message.getText()).build();
        } catch (JMSException e) {
            return Response.serverError().entity("JMS error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("mdb/consumeMessage")
    @Produces(MediaType.TEXT_PLAIN)
    public Response consumeBookMessageFromMDB() throws InterruptedException {
        log.info("entering consumeBookMessageFromMDB()");
        if (collection.getBooks().size() == 1) {
            String name = collection.getBooks().iterator().next().getName();
            log.info("got book name: " + name);
            return Response.ok(name).build();
        } else {
            return Response.serverError().entity("Collection size: " + collection.getBooks().size()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("mdb/books")
    public Collection<CDIInjectionBook> getBooksMDB() throws InterruptedException {
        log.info("entering getBooksMDB()");
        log.info("this.theSecret(): " + this.theSecret());
        Collection<CDIInjectionBook> books = collection.getBooks();
        log.info("getBooksMDB(): " + books);
        return books;
    }

    boolean isApplicationScoped(Class<?> c) {
        return testScope(c, ApplicationScoped.class);
    }

    boolean isDependentScoped(Class<?> c) {
        return testScope(c, Dependent.class);
    }

    boolean isRequestScoped(Class<?> c) {
        return testScope(c, RequestScoped.class);
    }

    boolean isSessionScoped(Class<?> c) {
        return testScope(c, SessionScoped.class);
    }

    boolean testScope(Class<?> c, Class<?> scopeClass) {
        Class<? extends Annotation> annotation = getScope(c);
        if (annotation == null) {
            return false;
        }
        return annotation.isAssignableFrom(scopeClass);
    }

    Class<? extends Annotation> getScope(Class<?> c) {
        Set<Bean<?>> beans = beanManager.getBeans(c);
        Iterator<Bean<?>> it = beans.iterator();
        if (it.hasNext()) {
            Bean<?> bean = beans.iterator().next();
            return bean.getScope();
        }
        return null;
    }

    public boolean theSame(CDIInjectionBookResource that) {
        return this.secret == that.secret;
    }

    public int theSecret() {
        return secret;
    }
}
