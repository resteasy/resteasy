package org.jboss.resteasy.examples.contacts.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.transaction.annotation.Transactional;

import org.jboss.resteasy.examples.contacts.core.Contact;
import org.jboss.resteasy.examples.contacts.core.Contacts;
import org.jboss.resteasy.examples.contacts.persistence.ContactDao;

/**
 * @author <a href="mailto:obrand@yahoo.com">Olivier Brand</a> Jun 28, 2008
 */
@Path("contactservice")
@Transactional
public class ContactServiceImpl implements ContactService {
	// DAO class used for interacting with the database
	private ContactDao contactDao;

	public ContactServiceImpl() {
		System.out.println("In Constructor ContactServiceImpl");
	}

	@GET
	@Path("/contacts")
	@Produces("application/xml")
	public Contacts getAllContacts() {
		Contacts contacts = new Contacts();
		contacts.setContacts(contactDao.findAllContacts());
		return contacts;
	}

	@GET
	@Path("/contacts/{id}")
	@Produces("application/xml")
	public Contact getContactById(@PathParam("id") Long id) {
		Contact contact = contactDao.findContactById(id);

		return contact;
	}

	@GET
	@Path("contacts/{id}/contacts")
	@Produces("application/xml")
	public Contacts getContactsOfContact(@PathParam("id") Long id) {
		Contacts contacts = new Contacts();
		contacts.setContacts(contactDao.findContactsOfContact(id));
		return contacts;
	}

	public ContactDao getContactDao() {
		return contactDao;
	}

	public void setContactDao(ContactDao contactDao) {
		this.contactDao = contactDao;
	}

}
