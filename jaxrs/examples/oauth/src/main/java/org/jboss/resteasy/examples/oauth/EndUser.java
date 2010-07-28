package org.jboss.resteasy.examples.oauth;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.resteasy.auth.oauth.OAuthUtils;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.xml.sax.InputSource;


public class EndUser
{
       
   private static final String ConsumerWebAppURL;
   private static final String EndUserResourceURL;
   
   static {
       Properties props = new Properties();
       try {
           props.load(EndUser.class.getResourceAsStream("/oauth.properties"));
       } catch (Exception ex) {
           throw new RuntimeException("oauth.properties resource is not available");
       }
       ConsumerWebAppURL = props.getProperty("consumer.webapp.url");
       EndUserResourceURL = props.getProperty("enduser.resource.url");
   }
   

   
   public static void main(String [] args) throws Exception {
       EndUser user = new EndUser();
       
       // Step1 : request a service from a 3rd party service
       String authorizationURI = user.requestServiceFromThirdPartyWebApp();
       // Step2 : authorize a consumer's temporarily request token
       String callback = user.authorizeConsumerRequestToken(authorizationURI);
       // Step3 : return an authorized request token to the consumer using the provided callback URI
       String endUserResourceData = user.setCallback(callback);
       System.out.println("Success : " + endUserResourceData);
       
       // confirm the end user can access its own resources directly
       if (!"true".equals(System.getProperty("jetty")))
       {
           accessEndUserResource("/resource1");
           accessEndUserResource("/resource2");
           accessEndUserResource("/invisible");
       }
   }
   
   private static void accessEndUserResource(String relativeURI) throws Exception
   {
       HttpClient client = new HttpClient();
       GetMethod method = new GetMethod(EndUserResourceURL + relativeURI);
       
       Base64 base64 = new Base64();
       String base64Credentials = new String(base64.encode("admin:admin".getBytes()));
       method.addRequestHeader(new Header("Authorization", "Basic " + base64Credentials));
       
       int status = client.executeMethod(method);
       if ("/invisible".equals(relativeURI)) {
           if (status != 401) {
               throw new RuntimeException("End user can access the invisible resource");
           } else {
               return;
           }
       }
       if (HttpResponseCodes.SC_OK != status) {
           throw new RuntimeException("End user can not access its own resources");
       }
       System.out.println("End user resource : " + method.getResponseBodyAsString());
   }
   
   /**
    * End user requests that a well-known 3rd party web application
    * does something useful on its behalf
    * @return authorizationURI where the end user has been redirected for
    *         the consumer temporarily request token be authorized
    * @throws Exception
    */
   public String requestServiceFromThirdPartyWebApp() throws Exception
   {
      HttpClient client = new HttpClient();
      
      GetMethod method = new GetMethod(ConsumerWebAppURL
              + "?scope=" + OAuthUtils.encodeForOAuth(EndUserResourceURL));
      method.setFollowRedirects(false);
      int status = client.executeMethod(method);
      if (302 != status) {
          // note that if the end user knows about the OAuth service's token authorization endpoint
          // then it can try to read the request token from the body and build the authorization URL
          // itself, in case the consumer has not provided the redirection URI
          throw new RuntimeException("Service request has failed - redirection is expected");
      }
      String authorizationURI = method.getResponseHeader("Location").getValue();
      if (authorizationURI == null) {
          throw new RuntimeException("Token authorization URI is missing");
      }
      return authorizationURI;
   }
   
   /**
    * End user follows the redirection by going to the authorizationURI 
    * provided by a 3rd party consumer
    * @param url the token authorization URI
    * @return the 3rd party callback URI where the authorization verifier needs to posted to
    * @throws Exception
    */
   public String authorizeConsumerRequestToken(String url) throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(url);
      // request that XML formatted authorization request is presented
      method.addRequestHeader(new Header("Accept", "application/xml"));
      Base64 base64 = new Base64();
      String base64Credentials = new String(base64.encode("admin:admin".getBytes()));
      method.addRequestHeader(new Header("Authorization", "Basic " + base64Credentials));
      
      int status = client.executeMethod(method);
      if (200 != status) {
          throw new RuntimeException("No authorization request data is available");
      }
      // at this stage we have an XML-formatted token authorization request
      String body = method.getResponseBodyAsString();
      String consumerId = evaluateBody(new ByteArrayInputStream(body.getBytes()),
                                        "/ns:tokenAuthorizationRequest/ns:consumerId/text()");
      String consumerName = evaluateBody(new ByteArrayInputStream(body.getBytes()),
                                        "/ns:tokenAuthorizationRequest/ns:consumerName/text()");
      String requestScope = evaluateBody(new ByteArrayInputStream(body.getBytes()),
                                        "/ns:tokenAuthorizationRequest/ns:scopes/text()");
      String requestPermission = evaluateBody(new ByteArrayInputStream(body.getBytes()),
                                        "/ns:tokenAuthorizationRequest/ns:permissions/text()");
      // TODO : ask about read/write permissions
      String message = "Authorize " 
                        + ("".equals(consumerName) ? consumerId : consumerName)
                        + System.getProperty("line.separator")
                        + " to access " + ("".equals(requestScope) ? "your resources" : requestScope)
                        + (requestPermission == null ? "" :
                            (System.getProperty("line.separator")
                             + " and grant the following permissions : \"" + requestPermission + "\""))
                        + " (yes/no) ?";
      String decision = JOptionPane.showInputDialog(message);
      if (decision == null || !"yes".equalsIgnoreCase(decision)) {
          decision = "no";
      }
      String replyTo = evaluateBody(new ByteArrayInputStream(body.getBytes()),
                                        "/ns:tokenAuthorizationRequest/@replyTo");
      replyTo += "&xoauth_end_user_decision=" + decision.toLowerCase();
      return confirmAuthorization(replyTo);
   }
   
   private String evaluateBody(InputStream body, String expression) {
       XPath xpath = XPathFactory.newInstance().newXPath();
       xpath.setNamespaceContext(new NamespaceContextImpl(
               Collections.singletonMap("ns", "http://org.jboss.com/resteasy/oauth")));
       try {
           Object result = (Object)xpath.evaluate(expression, new InputSource(body), XPathConstants.STRING);
           return (String)result;
       } catch (XPathExpressionException ex) {
           throw new IllegalArgumentException("Illegal XPath expression '" + expression + "'", ex);
       }

   }
   
   public String confirmAuthorization(String url) throws Exception
   {
      HttpClient client = new HttpClient();
      PostMethod method = new PostMethod(url);
      Base64 base64 = new Base64();
      String base64Credentials = new String(base64.encode("admin:admin".getBytes()));
      method.addRequestHeader(new Header("Authorization", "Basic " + base64Credentials));
      
      int status = client.executeMethod(method);
      if (302 != status) {
          throw new RuntimeException("Initiation failed");
      }
      // check that we got all tokens
      String callbackURI = method.getResponseHeader("Location").getValue();
      if (callbackURI == null) {
          throw new RuntimeException("Callback failed");
      }
      return callbackURI;
   }
   
   public String setCallback(String url) throws Exception
   {
      HttpClient client = new HttpClient();
      PostMethod method = new PostMethod(url);
      int status = client.executeMethod(method);
      if (200 != status) {
          throw new RuntimeException("Service failed");
      }
      return method.getResponseBodyAsString();
   }
   
   private static class NamespaceContextImpl implements NamespaceContext {
       
       private Map<String, String> namespaces;
       
       public NamespaceContextImpl(Map<String, String> namespaces) {
           this.namespaces = namespaces;    
       }

       public String getNamespaceURI(String prefix) {
           return namespaces.get(prefix);
       }

       public String getPrefix(String namespace) {
           for (Map.Entry<String, String> entry : namespaces.entrySet()) {
               if (entry.getValue().equals(namespace)) {
                   return entry.getKey();
               }
           }
           return null;
       }

       public Iterator getPrefixes(String namespace) {
           String prefix = namespaces.get(namespace);
           if (prefix == null) {
               return null;
           }
           return Collections.singletonList(prefix).iterator();
       }
   }

}
