package org.jboss.resteasy.examples.oauth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;

import org.jboss.resteasy.auth.oauth.OAuthException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.HttpResponseCodes;

// TODO : RestEast client api needs to be used

public class OAuthMessageSender implements MessageSender {

    private static Connection conn;
    
    static {
        Properties props = new Properties();
        try {
            props.load(OAuthPushMessagingFilter.class.getResourceAsStream("/db.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("db.properties resource is not available");
        }
        String driver = props.getProperty("db.driver");
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.username");
        String password = props.getProperty("db.password");
        
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception ex) {
            throw new RuntimeException("In memory OAuth DB can not be created " + ex.getMessage());
        }
    }

    public void sendMessage(String callbackURI, String messageSenderId, String message) {
       ClientRequest request = null;
       ClientResponse<?> response = null;
       try {
          request = new ClientRequest(getPushMessageURL(callbackURI, messageSenderId));
          request.body(message, "text/plain");
          response = null;
          response = request.post();
          if (HttpResponseCodes.SC_OK != response.getStatus()) {
             throw new RuntimeException("Message can not be delivered to subscribers");
          }
       } catch (Exception ex) 
       {
          throw new RuntimeException("Message can not be delivered to subscribers");
       } finally {
          response.releaseConnection();
       }
//       
//        HttpClient client = new HttpClient();
//        try {
//            PostMethod method = new PostMethod(getPushMessageURL(callbackURI, messageSenderId));
//            method.setRequestEntity(new StringRequestEntity(message, "text/plain", "UTF-8"));
//            int status = client.executeMethod(method);
//            if (HttpResponseCodes.SC_OK != status) {
//               throw new RuntimeException("Message can not be delivered to subscribers");
//            }
//        } catch (Exception ex) 
//        {
//            throw new RuntimeException("Message can not be delivered to subscribers");
//        }
    }

    @SuppressWarnings("unchecked")
    private String getPushMessageURL(String callbackURI, String messageSenderId) 
       throws Exception {
       OAuthMessage message = new OAuthMessage("POST", callbackURI, Collections.<Map.Entry>emptyList());
       OAuthConsumer consumer = new OAuthConsumer(null, messageSenderId, 
            getConsumerSecret(messageSenderId), null);
       OAuthAccessor accessor = new OAuthAccessor(consumer);
       message.addRequiredParameters(accessor);
       return OAuth.addParameters(message.URL, message.getParameters());
    }
    
    public String getConsumerSecret(String consumerKey) throws OAuthException {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM consumers WHERE key = '" + consumerKey + "'");
            if (rs.next()) {
                return rs.getString("secret");
            } else {
                throw new RuntimeException("No consumer with the key "+consumerKey + " exists");
            }
        } catch (SQLException ex) {
            throw new RuntimeException("No consumer with the key "+consumerKey + " exists");
        }
    }
}
