package org.jboss.resteasy.examples.contacts.services;

import org.jboss.resteasy.examples.contacts.core.Contact;
import org.jboss.resteasy.examples.contacts.core.Contacts;


/**
 * @author <a href="mailto:obrand@yahoo.com">Olivier Brand</a>
 *         Jun 28, 2008
 */
public interface ContactService {
	public Contact getContactById(Long id);

	public Contacts getAllContacts();

	public Contacts getContactsOfContact(Long id);
}
