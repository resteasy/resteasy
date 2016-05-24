package org.jboss.resteasy.test.smoke;

import org.jboss.resteasy.auth.oauth.OAuthException;
import org.jboss.resteasy.auth.oauth.OAuthMemoryProvider;


public class MyProvider extends OAuthMemoryProvider {

	public final static String Consumer1Key = "dpf43f3p2l4k3l03";
	public final static String Consumer1Secret = "kd94hf93k423kf44";
	public final static String Consumer1Request1Key = "q2efd58opw30t";
	public final static String Consumer1Request1Secret = "k9aw43q2ou934";
	public final static String Consumer1Request1Callback = "http://callback.net";
	public static final String Consumer1Request1Verifier = "q2r59kilj1pw9087"; 
	public final static String Consumer1Request2Key = "po0825epq2p09wi";
	public final static String Consumer1Request2Secret = "laiu49w8up3io5";
	public final static String Consumer1Request2Callback = "http://callback.net";
	
	public static final String[] Consumer1Access1Roles = {"admin"};
	public static final String Consumer1Access1Key = "o5i4eukolw9y8i65e25";
	public static final String Consumer1Access1Secret = "p0w394dk5uqoirej";
	
	public final static String Realm = "My realm";
	public MyProvider(){
		super(Realm);
		addConsumer(Consumer1Key, Consumer1Secret);
		try {
			addRequestKey(Consumer1Key, Consumer1Request1Key, Consumer1Request1Secret, Consumer1Request1Callback, new String[]{});
			authoriseRequestToken(Consumer1Key, Consumer1Request1Key, Consumer1Request1Verifier);
			addRequestKey(Consumer1Key, Consumer1Request2Key, Consumer1Request2Secret, Consumer1Request2Callback, new String[]{});
			addAccessKey(Consumer1Key, Consumer1Access1Key, Consumer1Access1Secret, Consumer1Access1Roles);
		} catch (OAuthException e) {
			// should not happen
			throw new RuntimeException(e);
		}
	}

}
