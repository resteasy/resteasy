package org.jboss.resteasy.core;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 
 * @author Nicolas NESMON
 *
 */
public class PostResourceMethodInvokers {

	private final Set<PostResourceMethodInvoker> invokers;

	public PostResourceMethodInvokers() {
		this.invokers = new LinkedHashSet<>();
	}

	public Set<PostResourceMethodInvoker> getInvokers() {
		return this.invokers;
	}

	public void clear() {
		this.invokers.clear();
	}

	public void addInvokers(PostResourceMethodInvoker... invokers) {
		Collections.addAll(this.invokers, invokers);
	}

}
