package se.unlogic.standardutils.string;

import java.util.Collections;
import java.util.Set;


public class SingleTagSource implements TagSource {

	protected Set<String> tags;
	protected String value;
	
	protected SingleTagSource(Set<String> tags, String value) {

		this.tags = tags;
		this.value = value;
	}

	public SingleTagSource(String tag, String value){
		
		this.tags = Collections.singleton(tag);
		this.value = value;
	}
	
	public Set<String> getTags() {

		return tags;
	}

	public String getTagValue(String tag) {

		return value;
	}
}
