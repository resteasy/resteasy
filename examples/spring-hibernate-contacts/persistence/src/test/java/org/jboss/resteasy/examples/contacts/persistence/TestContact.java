package org.jboss.resteasy.examples.contacts.persistence;


import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.jboss.resteasy.examples.contacts.core.Contact;
import org.jboss.resteasy.examples.contacts.core.ContactAttrs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author <a href="mailto:obrand@yahoo.com">Olivier Brand</a>
 * Jun 28, 2008
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Load the beans to configure, here the DAOs
@ContextConfiguration(locations={"/test-config.xml"})
// apply the transaction manager to the test class so every DAO methods are executed
// within a transaction
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
@Transactional
public class TestContact {
	private static final String CONTACT_PHONE = "16506193726";
	private static final String CONTACT_EMAIL = "olivier@yahoo.com";
	private static final String CONTACT_NAME_3 = "newcontact";
	private static final String CONTACT_NAME_4 = "newcontact2";
	private static final int CONTACT_ID = 1;
	private static final String CONTACT_NAME = "olivier";
	private static final String CONTACT_NAME_2 = "angela";
	private static final long PARENT_CONTACT_ID = 2;

	// JSR 250 annotation injecting the contactDao bean. Similar to the Spring
	// @Autowired annotation
	@Resource
	private ContactDao contactDao;

	@Before
	public void setup() {

	}

	@After
	public void cleanup() {
		Contact contact = contactDao.findContactByName(CONTACT_NAME_3);
		if (contact != null) {
			contactDao.deleteContact(contact);
		}
	}

	@Test
	public void simpleTest() {
		Assert.assertNotNull(contactDao);
	}

	@Test
	public void testFindByName() {
		Assert.assertNotNull(contactDao.findContactByName(CONTACT_NAME));
	}

	@Test
	public void testFindById() {
		Assert.assertNotNull(contactDao.findContactById(CONTACT_ID));
	}

	@Test
	public void testFindAllContacts() {
		Assert.assertTrue(!contactDao.findAllContacts().isEmpty());
		Assert.assertEquals(3, contactDao.findAllContacts().size());
	}

	@Test
	public void testFindByNameContacts() {
		Contact contact = contactDao.findContactByName(CONTACT_NAME_2);
		Assert.assertNotNull(contact);
		Assert.assertEquals(2, contact.getContactChildren().size());
	}

	@Test
	public void testFindByEmail() {
		Assert.assertNotNull(contactDao.findContactByEmail(CONTACT_EMAIL));
	}

	@Test
	public void testFindByPhone() {
		Assert.assertNotNull(contactDao.findContactByPhone(CONTACT_PHONE));
	}

	@Test
	public void testFindByAttribute() {
		Assert.assertNotNull(contactDao.findContactByAttribute(
				ContactAttrs.telephone, CONTACT_PHONE));
		Assert.assertNotNull(contactDao.findContactByAttribute(
				ContactAttrs.email, CONTACT_EMAIL));
		Assert.assertNotNull(contactDao.findContactByAttribute(
				ContactAttrs.name, CONTACT_NAME));
		Assert.assertNotNull(contactDao.findContactByAttribute(ContactAttrs.id,
				1L));
	}

	@Test
	public void testInsertContact() {
		Contact newContact = new Contact();
		newContact.setEmail("newcontact@yahoo.com");
		newContact.setName(CONTACT_NAME_3);
		newContact.setTelephone("3213123123");
		Contact contact = contactDao.findContactByName(CONTACT_NAME_3);
		Assert.assertNull(contact);
		contactDao.addUpdateContact(newContact);
		contact = contactDao.findContactByName(CONTACT_NAME_3);
		Assert.assertNotNull(contact);
	}

	@Test
	public void testDeleteContact() {
		Contact newContact = new Contact();
		newContact.setEmail("newcontact2@yahoo.com");
		newContact.setName(CONTACT_NAME_4);
		newContact.setTelephone("3213123134");
		Contact contact = contactDao.findContactByName(CONTACT_NAME_4);
		Assert.assertNull(contact);
		contactDao.addUpdateContact(newContact);
		contact = contactDao.findContactByName(CONTACT_NAME_4);
		Assert.assertNotNull(contact);
		contactDao.deleteContact(contact);
		contact = contactDao.findContactByName(CONTACT_NAME_4);
		Assert.assertNull(contact);
	}
	
	@Test
	public void testGetContactsOfContact() {
		Collection<Contact> contacts = contactDao.findContactsOfContact(PARENT_CONTACT_ID);
		Assert.assertEquals(2, contacts.size());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void empty() {
		new ArrayList<Object>().get(0);
	}
}