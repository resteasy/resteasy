package org.jboss.resteasy.client.core;


@SuppressWarnings("unchecked")
public interface ClientInterceptor
{
   void preBaseMethodConstruction(ClientResponseImpl clientResponseImpl);
   
   void preExecute(ClientResponseImpl clientResponseImpl);

   void postExecute(ClientResponseImpl clientResponseImpl);
   
   void postUnMarshalling(ClientResponseImpl clientResponseImpl);
}
