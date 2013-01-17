package org.jboss.resteasy.cdi.injection;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.cdi.util.Constants;
import org.jboss.resteasy.cdi.util.Counter;
import org.jboss.resteasy.cdi.util.CounterBinding;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 7, 2012
 */
@Path("/")
@RequestScoped
public class BookResource
{  
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
   public static final String COUNTER = "counter";
   public static final String COLLECTION = "collection";
   public static final String BOOK_BAG = "bookBag";
   public static final String NEW_BEAN_APPLICATION_SCOPED = "newBeanApplicationScoped";
   public static final String NEW_BEAN_DEPENDENT_SCOPED = "newBeanDependentScoped";
   public static final String STEREOTYPED_APPLICATION_SCOPED = "stereotypedApplicationScoped";
   public static final String STEREOTYPED_DEPENDENT_SCOPED = "stereotypedDependentScoped";
   
   private static HashMap<String,Object> store;
   
   public static HashMap<String, Object> getStore()
   {
      return store;
   }

   public static void setStore(HashMap<String, Object> store)
   {
      BookResource.store = store;
   }
   
   private HashSet<Book> set = new HashSet<Book>();
   
   public HashSet<Book> getSet()
   {
      return set;
   }

   public void setSet(HashSet<Book> set)
   {
      this.set = set;
   }
   
   private static AtomicInteger constructCounter = new AtomicInteger();
   private static AtomicInteger destroyCounter = new AtomicInteger();
   
   @PreDestroy
   public void preDestroy()
   {
      destroyCounter.incrementAndGet();
      log.info("preDestroy(): destroyCounter: " + destroyCounter.get());
   }
   
   @PostConstruct
   public void postConstruct()
   {
      constructCounter.incrementAndGet();
      log.info("postConstruct(): constructCounter: " + constructCounter.get());
   }
   
   private static CountDownLatch latch = new CountDownLatch(2);
   
   public CountDownLatch getCountDownLatch()
   {
      log.info("latch.getCount(): " + latch.getCount());
      return latch;
   }
   
   @Inject private BeanManager beanManager;
   @Inject private int secret;                 // used to determine identity
   @Inject private DependentScoped dependent;  // dependent scoped managed bean
   
   @Inject @CounterBinding private Counter counter;  // application scoped singleton: injected as Weld proxy
   @EJB private BookCollection collection;           // application scoped singleton: injected as EJB proxy
   
   // @Note: stateful and stateful2 are two very different objects.
   // stateful is an EJB proxy and stateful2 is a Weld proxy.
   @EJB    private StatefulEJB stateful;       // dependent scoped SLSB
   @Inject private StatefulEJB stateful2;      // dependent scoped SLSB
   
   @Inject private BookBagLocal bookBag;       // session scoped SFSB
   
   @Inject
   @ResourceBinding
   @PersistenceContext(unitName="test")
   EntityManager em;
   
   @Inject 
   @ResourceBinding
   private Session session;   

   @Inject 
   @ResourceBinding
   private Queue bookQueue;
   
   @Inject
   private NewBean newBean1;
   
   @Inject
   @New
   private NewBean newBean2;
   
   @Inject
   private StereotypedApplicationScope stereotypeApplicationScoped;
   
   @Inject
   private StereotypedDependentScope stereotypedRequestScoped;
   
//   @Inject
//   @ResourceBinding
//   private ConnectionFactory connectionFactory;
   
//   private Context applicationContext;
//   private Context requestContext;
//   private Context sessionContext;
   
   private Logger log;
   
   @Inject
   public void init(Instance<Logger> logInstance)
   {
      this.log = logInstance.get();
   }
   
   @GET
   @Path("verifyScopes")
   @Produces(MediaType.TEXT_PLAIN)
   public Response verifyScopes()
   {
      log.info("entering verifyScopes()");
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      BookReader reader = BookReader.class.cast(factory.getMessageBodyReader(Book.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE));
      BookWriter writer = BookWriter.class.cast(factory.getMessageBodyWriter(Book.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE));
      
      if (store == null)
      {
         log.info("Counter scope:          " + getScope(Counter.class));
         log.info("BookCollection scope:   " + getScope(BookCollection.class));
         log.info("BookResource scope:     " + getScope(BookResource.class));
         log.info("BookReader scope:       " + getScope(BookReader.class));
         log.info("BookWriter scope:       " + getScope(BookWriter.class));
         log.info("UnscopedResource scope: " + getScope(UnscopedResource.class)); 
         log.info("DependentScoped scope:  " + getScope(DependentScoped.class));
         log.info("StatelessEJB scope:     " + getScope(StatefulEJB.class));
         log.info("BookBagLocal scope:     " + getScope(BookBagLocal.class));
//         log.info("BookMDB scope:          " + getScope(BookMDB.class));
         log.info("NewBean scope:          " + getScope(NewBean.class));
         log.info("stereotypeApplicationScoped: " + getScope(StereotypedApplicationScope.class));
         log.info("stereotypeRequestScoped:     " + getScope(StereotypedDependentScope.class));
         
         store = new HashMap<String,Object>();
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
         store.put(COUNTER, counter);
         store.put(COLLECTION, collection);
         store.put(BOOK_BAG, bookBag);
         store.put(NEW_BEAN_APPLICATION_SCOPED, newBean1);
         store.put(NEW_BEAN_DEPENDENT_SCOPED, newBean2);
         store.put(STEREOTYPED_APPLICATION_SCOPED, stereotypeApplicationScoped);
         store.put(STEREOTYPED_DEPENDENT_SCOPED, stereotypedRequestScoped);
         return Response.ok().build();
      }
      
      if (isApplicationScoped(Counter.class)                     &&
          isApplicationScoped(BookCollection.class)              &&
          isApplicationScoped(BookReader.class)                  &&
          isApplicationScoped(BookWriter.class)                  &&
          isRequestScoped(BookResource.class)                    &&
          isDependentScoped(DependentScoped.class)               &&
          isDependentScoped(StatefulEJB.class)                   &&
          isRequestScoped(UnscopedResource.class)                &&
          isApplicationScoped(JaxRsActivator.class)              &&
          isSessionScoped(BookBagLocal.class)                    &&
          isApplicationScoped(NewBean.class)                     &&
          isApplicationScoped(StereotypedApplicationScope.class) &&
          isDependentScoped(StereotypedDependentScope.class)     &&
//          getScope(BookMDB.class) == null                        &&
          store.get(BOOK_READER) == reader                       &&
          store.get(BOOK_WRITER) == writer                       &&
          store.get(BOOK_RESOURCE) != this                       &&
          store.get(BOOK_READER_DEPENDENT) == reader.getDependent()    &&
          store.get(BOOK_WRITER_DEPENDENT) == writer.getDependent()    &&
          store.get(BOOK_RESOURCE_DEPENDENT) != dependent              &&
          store.get(BOOK_READER_STATEFUL).equals(reader.getStateful()) &&
          store.get(BOOK_WRITER_STATEFUL).equals(writer.getStateful()) &&
         !store.get(BOOK_RESOURCE_STATEFUL).equals(stateful)           &&
         !store.get(BOOK_RESOURCE_STATEFUL2).equals(stateful2)         &&
          store.get(COUNTER).equals(counter)                           &&
          store.get(COLLECTION).equals(collection)                     &&
          store.get(BOOK_BAG).equals(bookBag)                          &&
          store.get(NEW_BEAN_APPLICATION_SCOPED).equals(newBean1)      &&
         !store.get(NEW_BEAN_DEPENDENT_SCOPED).equals(newBean2)        &&
         !newBean1.equals(newBean2)                                    &&
          store.get(STEREOTYPED_APPLICATION_SCOPED).equals(stereotypeApplicationScoped) &&
         !store.get(STEREOTYPED_DEPENDENT_SCOPED).equals(stereotypedRequestScoped)
          )
      {
         return Response.ok().build();
      }
      else
      {
         return Response.serverError().build();
      }
   }
   
   @POST
   @Path("empty")
   public void empty()
   {
      collection.empty();   
   }
   
   @POST
   @Path("create")
   @Consumes("application/test+xml")
   @Produces(MediaType.TEXT_PLAIN)
   public Response createBook(Book book)
   {
      log.info("entering createBook()");
      int id = counter.getNext();
      book.setId(id);
      collection.addBook(book);  
      log.info("stored: " + id + "->" + book);
      return Response.ok(id).build();
   }
   
   @GET
   @Produces(MediaType.APPLICATION_XML)
   @Path("books")
   public Collection<Book> listAllMembers()
   {
      log.info("entering listAllMembers()");
      log.info("this.theSecret(): " + this.theSecret());
      Collection<Book> books = collection.getBooks();
      log.info("listAllMembers(): " + books);
      return books;
   }

   @GET
   @Path("book/{id:[0-9][0-9]*}")
   @Produces("application/test+xml")
   public Book lookupBookById(@PathParam("id") int id)
   {
      log.info("entering lookupBookById(" + id + ")");
      log.info("books: " + collection.getBooks());
      Book book = collection.getBook(id);
      if (book == null)
      {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      return book;
   }
   
   @POST
   @Path("entityManager")
   public Response testEntityManager()
   {
      log.info("entering testEntityManager()");
      Book book1 = collection.getBook(Counter.INITIAL_VALUE);
      Book book2 = em.find(Book.class, Counter.INITIAL_VALUE);
      return book1.equals(book2) ? Response.ok().build() : Response.serverError().build();

   }
   
   @POST
   @Path("session/add")
   public Response sessionAdd(@Context HttpServletRequest request, Book book)
   {
      log.info("entering sessionAdd()");
      log.info("new session: " + request.getSession().isNew());
      bookBag.addBook(book);
      return Response.ok().build();
   }

   @GET
   @Path("session/get")
   @Produces(MediaType.APPLICATION_XML)
   public Collection<Book> sessionGetBag(@Context HttpServletRequest request)
   {
      log.info("entering sessionGetBag()");
      log.info("new session: " + request.getSession().isNew());
      Collection<Book> books = bookBag.getContents();
      log.info("sessionGetBag(): " + books);
      request.getSession().invalidate();
      return books;
   }
   
   @POST
   @Path("session/test")
   public Response sessionTest(@Context HttpServletRequest request)
   {
      log.info("entering sessionTest()");
      log.info("new session: " + request.getSession().isNew());
      Collection<Book> contents = bookBag.getContents();
      log.info("bookBag: " + contents);
      if (request.getSession().isNew() && contents.isEmpty())
      {
         return Response.ok().build();
      }
      else
      {
         return Response.serverError().build();
      }
   }
   
   @GET
   @Path("mdb/get")
   @Produces(MediaType.APPLICATION_XML)
   public GenericEntity<Collection<Book>> mdbGetBag()
   {
      log.info("entering mdbGetBag()");
      Collection<Book> books = bookBag.getContents();
      log.info("sessionGetBag(): " + books);
      return new GenericEntity<Collection<Book>>(books){};
   }
   
   @GET
   @Path("getCounters")
   public String getCounters()
   {
      return Integer.toString(constructCounter.get()) + ":" + Integer.toString(destroyCounter.get()) + ":";
   }
   
   @GET
   @Path("disposer")
   public Response testDisposer()
   {
      log.info("entering testDisposer()");
      return ResourceProducer.isDisposed() ? Response.ok().build() : Response.serverError().build();
   }
   
   @POST
   @Path("produceMessage")
   @Consumes("application/test+xml")
   @Produces(MediaType.TEXT_PLAIN)
   public Response produceBookMessage(Book book)
   {
      log.info("entering produceBookMessage()");
      try
      {
         log.info("queue: " + bookQueue);
         log.info("ResourceProducer scope: " + getScope(ResourceProducer.class));
         log.info("queue scope: " + getScope(Queue.class));
         MessageProducer producer = session.createProducer(bookQueue);
         TextMessage message = session.createTextMessage(book.getName());
         producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
         producer.send(message);
         log.info("sent: " + message.getText());
         return Response.ok().build();
      }
      catch (JMSException e)
      {
         e.printStackTrace();
         return Response.serverError().entity("JMS error: " + e.getMessage()).build();
      }
   }
   
   @GET
   @Path("queue/consumeMessage")
   @Produces(MediaType.TEXT_PLAIN)
   public Response consumeBookMessageFromQueue()
   {
      log.info("entering consumeBookMessageFromQueue() xxx");
      log.info("getting consumer");
      try
      {
         MessageConsumer consumer = session.createConsumer(bookQueue);
         log.info("got consumer");
         TextMessage message = (TextMessage) consumer.receiveNoWait();
         log.info("message: " + message);
         if (message == null)
         {
            return Response.serverError().build();
         }
         log.info("message text: " + message.getText());
         return Response.ok(message.getText()).build();
      }
      catch (JMSException e)
      {
         return Response.serverError().entity("JMS error: " + e.getMessage()).build();
      }
   }
   
   @GET
   @Path("mdb/consumeMessage")
   @Produces(MediaType.TEXT_PLAIN)
   public Response consumeBookMessageFromMDB() throws InterruptedException
   {
      log.info("entering consumeBookMessageFromMDB()");
      getCountDownLatch().await(5000, TimeUnit.MILLISECONDS); // Wait until BookMDB has stored book in BookCollection.
      BookCollection collection = getBookCollection();
      log.info("consumeBookMessageFromMDB(): collection.size(): " + collection.getBooks().size());
      if (collection.getBooks().size() == 1)
      {
         String name = collection.getBooks().iterator().next().getName();
         log.info("got book name: " + name);
         return Response.ok(name).build();
      }
      else
      {
         return Response.serverError().entity("Collection size: " + collection.getBooks().size()).build();
      }
   }
   
   @GET
   @Produces(MediaType.APPLICATION_XML)
   @Path("mdb/books")
   public Collection<Book> getBooksMDB() throws InterruptedException
   {
      log.info("entering getBooksMDB()");
      log.info("getBooksMDB(): waiting on latch");
      latch.await();
      log.info("this.theSecret(): " + this.theSecret());
      Collection<Book> books = collection.getBooks();
      log.info("getBooksMDB(): " + books);
      return books;
   }
   
   BookCollection getBookCollection()
   {
      log.info("entering getBookCollection()");
      return collection;
   }
   
   Counter getCounter()
   {
      log.info("returning: " + counter);
      return counter;
   }
   
   boolean isApplicationScoped(Class<?> c)
   {
      return testScope(c, ApplicationScoped.class);
   }

   boolean isDependentScoped(Class<?> c)
   {
      return testScope(c, Dependent.class);
   }
   
   boolean isRequestScoped(Class<?> c)
   {
      return testScope(c, RequestScoped.class);
   }
   
   boolean isSessionScoped(Class<?> c)
   {
      return testScope(c, SessionScoped.class);
   }
   
   boolean testScope(Class<?> c, Class<?> scopeClass)
   {
      Class<? extends Annotation> annotation = getScope(c);
      if (annotation == null)
      {
         return false;
      }
      return annotation.isAssignableFrom(scopeClass);
   }
   
   Class<? extends Annotation> getScope(Class<?> c)
   {
      Set<Bean<?>> beans = beanManager.getBeans(c);
      Iterator<Bean<?>> it = beans.iterator();
      if (it.hasNext())
      {
         Bean<?> bean = beans.iterator().next();
         return bean.getScope();
      }
//      Annotation[] annotations = c.getAnnotations();
//      for (int i = 0; i < annotations.length; i++)
//      {
//         log.info(annotations[i].annotationType().toString());
//         if (beanManager.isScope(annotations[i].annotationType()))
//         {
//            return annotations[i].getClass();
//         }
//         if (beanManager.isStereotype(annotations[i].annotationType()))
//         {
//            return getScope(annotations[i].annotationType());
//         }
//      }
      return null;
   }
   
   public boolean theSame(BookResource that)
   {
      return this.secret == that.secret;
   }
   
   public int theSecret()
   {
      return secret;
   }
}
