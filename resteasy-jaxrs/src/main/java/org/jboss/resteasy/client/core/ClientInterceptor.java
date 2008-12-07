package org.jboss.resteasy.client.core;


public interface ClientInterceptor<T>
{
   void preBaseMethodConstruction(ClientResponseImpl<T> clientResponseImpl);
   
   void preExecute(ClientResponseImpl<T> clientResponseImpl);

   void postExecute(ClientResponseImpl<T> clientResponseImpl);
   
   void postUnMarshalling(ClientResponseImpl<T> clientResponseImpl);
}
