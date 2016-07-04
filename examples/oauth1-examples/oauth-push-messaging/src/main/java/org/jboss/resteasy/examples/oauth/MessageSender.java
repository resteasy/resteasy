package org.jboss.resteasy.examples.oauth;

public interface MessageSender {
    void sendMessage(String callbackURI, String messageSenderId, String message);
}
