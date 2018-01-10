package org.jboss.resteasy.test.providers.sse;

import java.util.Collections;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * 
 * @author Nicolas NESMON
 *
 */
@ApplicationPath("/")
public class SsePostResourceMethodInvokerApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		return Collections.singleton(new SsePostResourceMethodInvokerTestResource());
	}

}
