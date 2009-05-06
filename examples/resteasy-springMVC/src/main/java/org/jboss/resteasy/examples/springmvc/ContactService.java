package org.jboss.resteasy.examples.springmvc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class ContactService
{
   private Map<String, Contact> contactMap = new ConcurrentHashMap<String, Contact>();

   public void save(Contact contact)
   {
      contactMap.put(contact.getLastName(), contact);
   }

   public Contact getContact(String lastName)
   {
      return contactMap.get(lastName);
   }

   public Contacts getAll()
   {
      return new Contacts(contactMap.values());
   }
}
