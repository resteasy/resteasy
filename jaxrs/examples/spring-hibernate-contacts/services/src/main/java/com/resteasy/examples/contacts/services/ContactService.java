package com.resteasy.examples.contacts.services;

import com.resteasy.examples.contacts.core.Contact;
import com.resteasy.examples.contacts.core.Contacts;


/**
 * @author <a href="mailto:obrand@yahoo.com">Olivier Brand</a>
 * Jun 28, 2008
 * 
 */
public interface ContactService
{
    public Contact getContactById(Long id);
    public Contacts getAllContacts();
}
