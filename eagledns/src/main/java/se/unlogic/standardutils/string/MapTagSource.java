package se.unlogic.standardutils.string;

import java.util.HashMap;
import java.util.Set;


public class MapTagSource implements TagSource {

	protected HashMap<String,String> tagMap;
	
	public MapTagSource(){
		
		tagMap = new HashMap<String,String>();
	}
	
	public MapTagSource(HashMap<String, String> tagMap) {

		super();
		this.tagMap = tagMap;
	}

	public void addTag(String tag, String value){
		
		tagMap.put(tag, value);
	}
	
	public Set<String> getTags() {

		return tagMap.keySet();
	}

	public String getTagValue(String tag) {

		return tagMap.get(tag);
	}
}
