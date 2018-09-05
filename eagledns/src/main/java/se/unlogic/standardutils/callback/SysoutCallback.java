package se.unlogic.standardutils.callback;


import org.apache.log4j.Logger;

public class SysoutCallback<T> implements Callback<T> {

	protected String prefix;
	protected String suffix;

	private static final Logger LOG = Logger.getLogger(SysoutCallback.class);

	public SysoutCallback(String prefix, String suffix) {

		super();
		this.prefix = prefix;
		this.suffix = suffix;
		
		if(prefix == null){
			
			this.prefix = "";
		}
		
		if(suffix == null){
			
			this.suffix = "";
		}
	}
	public void callback(T type) {

		LOG.info(prefix + type.toString() + suffix);
	}
}
