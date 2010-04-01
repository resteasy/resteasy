package org.jboss.resteasy.test.war;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author <a href="mailto:bill@burkecentral.com">Stephane Epardaud</a>
 * @version $Revision: 1 $
 */
public class JSAPITest
{
	static final String JSAPIURL = "http://localhost:9095/rest-js";
	
	
   @Test
   public void test() throws Exception
   {
      HttpClient client = new HttpClient();
      
      GetMethod method = new GetMethod(JSAPIURL);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_OK, status);
      String response = method.getResponseBodyAsString();
      int i = 0;
      int last = 0;
      int line = 1;
      while((i = response.indexOf('\n', i)) > 0){
    	  System.err.print(line+": ");
    	  System.err.print(response.substring(last, i+1));
    	  line++;
    	  i++;
    	  last = i;
      }
      method.releaseConnection();
   }
   
}
