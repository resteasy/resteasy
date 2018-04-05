package org.jboss.resteasy.rxjava;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rx.Observable;
import rx.Single;

public class RxTest
{
	private static NettyJaxrsServer server;

	private static Dispatcher dispatcher;
	private static CountDownLatch latch;
	private static AtomicReference<Object> value = new AtomicReference<Object>();

	@SuppressWarnings("deprecation")
    @BeforeClass
	public static void beforeClass() throws Exception
	{
		server = new NettyJaxrsServer();
		server.setPort(TestPortProvider.getPort());
		server.setRootResourcePath("/");
		server.start();
		dispatcher = server.getDeployment().getDispatcher();
		POJOResourceFactory noDefaults = new POJOResourceFactory(RxResource.class);
		dispatcher.getRegistry().addResourceFactory(noDefaults);
	}

	@AfterClass
	public static void afterClass() throws Exception
	{
		server.stop();
		server = null;
		dispatcher = null;
	}

	private ResteasyClient client;

	@Before
	public void before()
	{
		client = new ResteasyClientBuilder()
				.readTimeout(5, TimeUnit.SECONDS)
				.connectionCheckoutTimeout(5, TimeUnit.SECONDS)
				.connectTimeout(5, TimeUnit.SECONDS)
				.build();
		value.set(null);
		latch = new CountDownLatch(1);
	}

	@After
	public void after()
	{
		client.close();
	}

	@Test
	public void testSingle() throws Exception
	{
		Single<Response> single = client.target(generateURL("/single")).request().rx(SingleRxInvoker.class).get();
		single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
		latch.await();
		assertEquals("got it", value.get());
	}
	
	@Test
	public void testSingleContext() throws Exception
	{
		Single<Response> single = client.target(generateURL("/context/single")).request().rx(SingleRxInvoker.class).get();
		single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
		latch.await();
		assertEquals("got it", value.get());
	}

	@Test
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void testObservable() throws Exception
	{
		ObservableRxInvoker invoker = client.target(generateURL("/observable")).request().rx(ObservableRxInvoker.class);
		Observable<String> observable = (Observable<String>) invoker.get();
		List<String> data = new ArrayList<String>();
		observable.subscribe(
				(String s) -> data.add(s),
				(Throwable t) -> t.printStackTrace(),
				() -> latch.countDown());
		latch.await();
		assertArrayEquals(new String[] {"one", "two"}, data.toArray());
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void testObservableContext() throws Exception
	{
		ObservableRxInvoker invoker = client.target(generateURL("/context/observable")).request().rx(ObservableRxInvoker.class);
		Observable<String> observable = (Observable<String>) invoker.get();
		List<String> data = new ArrayList<String>();
		observable.subscribe(
				(String s) -> data.add(s),
				(Throwable t) -> t.printStackTrace(),
				() -> latch.countDown());
		latch.await();
		assertArrayEquals(new String[] {"one", "two"}, data.toArray());
	}
}