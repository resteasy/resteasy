package org.jboss.resteasy.test.providers.sse;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author Nicolas NESMON
 *
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SsePostResourceMethodInvokerTest {

	private static final String WITHOUT_EXCEPTION_REQUEST_FILTER = "withoutExceptionRequestFilter";
	private static final String WITH_EXCEPTION_REQUEST_FILTER = "withExceptionRequestFilter";

	@Deployment(name = WITH_EXCEPTION_REQUEST_FILTER)
	public static Archive<?> deployWithoutExceptionRequestFilter() throws Exception {
		WebArchive war = TestUtil.prepareArchive(WITH_EXCEPTION_REQUEST_FILTER);
		return TestUtil.finishContainerPrepare(war, null, Arrays.asList(SsePostResourceMethodInvokerTestResource.class),
				SsePostResourceMethodInvokerTestResource.ExceptionRequestFilter.class);
	}

	@Deployment(name = WITHOUT_EXCEPTION_REQUEST_FILTER)
	public static Archive<?> deployWithExceptionRequestFilter() throws Exception {
		WebArchive war = TestUtil.prepareArchive(WITHOUT_EXCEPTION_REQUEST_FILTER);
		war.addAsWebInfResource("org/jboss/resteasy/test/providers/sse/synch-web.xml", "web.xml");
		war.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		return TestUtil.finishContainerPrepare(war, null, SsePostResourceMethodInvokerTestResource.class,
				SsePostResourceMethodInvokerApplication.class);
	}

	private static String generateURL(String tesName) {
		return PortProviderUtil.generateBaseUrl(tesName);
	}

	@Test()
	@OperateOnDeployment(WITHOUT_EXCEPTION_REQUEST_FILTER)
	public void Should_Return200OkResponse_When_ResourceMethodExitFirst() throws Exception {
		Client client = ClientBuilder.newClient();
		try {
			WebTarget baseTarget = client.target(generateURL(WITHOUT_EXCEPTION_REQUEST_FILTER))
					.path(SsePostResourceMethodInvokerTestResource.BASE_PATH);
			try {
				Response response = baseTarget.request(MediaType.SERVER_SENT_EVENTS_TYPE).buildGet().submit().get(10,
						TimeUnit.SECONDS);
				try {
					Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
				} finally {
					client.close();
				}
			} catch (TimeoutException e) {
				Assert.fail("Sse initial 200 ok response is expected when resource method resource returns first.");
			}
		} finally {
			client = ClientBuilder.newClient();
			try {
				client.target(generateURL(WITHOUT_EXCEPTION_REQUEST_FILTER))
						.path(SsePostResourceMethodInvokerTestResource.BASE_PATH)
						.path(SsePostResourceMethodInvokerTestResource.CLOSE_PATH).request().delete();
			} finally {
				client.close();
			}
		}
	}

	@Test(expected = InternalServerErrorException.class)
	@OperateOnDeployment(WITH_EXCEPTION_REQUEST_FILTER)
	public void Should_ThrowIntenalServerError_When_AnyFilterAfterSseFilterThrowsIOException() throws Exception {
		Client client = ClientBuilder.newClient();
		try {
			client.target(generateURL(WITH_EXCEPTION_REQUEST_FILTER))
					.path(SsePostResourceMethodInvokerTestResource.BASE_PATH).request(MediaType.SERVER_SENT_EVENTS_TYPE)
					.get(String.class);
		} finally {
			client.close();
		}
	}

}
