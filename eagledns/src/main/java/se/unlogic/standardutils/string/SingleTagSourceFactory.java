package se.unlogic.standardutils.string;

import java.util.Collections;
import java.util.Set;


public class SingleTagSourceFactory<T> implements TagSourceFactory<T>{

	protected Set<String> tags;
	protected String tag;
	
	public SingleTagSourceFactory(String tag){
		
		this.tags = Collections.singleton(tag);
		this.tag = tag;
	}

	public <X extends T> TagSource getTagSource(X value) {

		return new SingleTagSource(tags, value.toString());
	}

	public Set<String> getTagsSet() {

		return tags;
	}

	public String getAvailableTags() {

		return tag;
	}
}
