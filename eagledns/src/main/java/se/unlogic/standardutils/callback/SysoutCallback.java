package se.unlogic.standardutils.callback;


public class SysoutCallback<T> implements Callback<T> {

	protected String prefix;
	protected String suffix;
	
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

		System.out.println(prefix + type.toString() + suffix);
	}
}
