package org.jboss.resteasy.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Nicolas NESMON
 *
 */
public class PostResourceMethodInvokers {

	private final List<PostResourceMethodInvoker> invokers;

	public PostResourceMethodInvokers() {
		this.invokers = new ArrayList<>();
	}

	public List<PostResourceMethodInvoker> getInvokers() {
		return this.invokers;
	}

	public void clear() {
		this.invokers.clear();
	}

	public void addInvokers(PostResourceMethodInvoker... invokers) {
		Collections.addAll(this.invokers, invokers);
	}

}
