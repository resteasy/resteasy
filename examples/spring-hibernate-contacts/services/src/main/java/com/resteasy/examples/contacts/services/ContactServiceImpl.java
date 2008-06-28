package com.resteasy.examples.contacts.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;

import org.springframework.transaction.annotation.Transactional;

import com.resteasy.examples.contacts.core.Contact;
import com.resteasy.examples.contacts.core.Contacts;
import com.resteasy.examples.contacts.persistence.ContactDao;


/**
 * @author <a href="mailto:obrand@yahoo.com">Olivier Brand</a>
 * Jun 28, 2008
 * 
 */
@Path("contactservice")
@Transactional
public class ContactServiceImpl implements ContactService
{
    // DAO class used for interacting with the database
    private ContactDao contactDao;
    
    public ContactServiceImpl() {
	System.out.println("In Constructor ContactServiceImpl");
    }

    @GET
    @Path("/contacts")
    @ProduceMime("application/xml")
    public Contacts getAllContacts()
    {
	Contacts contacts = new Contacts();
	contacts.setContacts(contactDao.findAllContacts());
	return contacts;
    }

    @Path("/contacts/{id}")
    @ProduceMime("application/xml")
    public Contact getContactById(@PathParam("id")Long id)
    {
	Contact contact = contactDao.findContactById(id);
	
	return contact;
    }

    public ContactDao getContactDao()
    {
        return contactDao;
    }

    public void setContactDao(ContactDao contactDao)
    {
        this.contactDao = contactDao;
    }

}
