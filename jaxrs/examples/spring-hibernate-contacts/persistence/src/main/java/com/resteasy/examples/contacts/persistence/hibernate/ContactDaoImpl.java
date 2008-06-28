package com.resteasy.examples.contacts.persistence.hibernate;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.resteasy.examples.contacts.core.Contact;
import com.resteasy.examples.contacts.core.ContactAttrs;
import com.resteasy.examples.contacts.persistence.ContactDao;


/**
 * @author <a href="mailto:obrand@yahoo.com">Olivier Brand</a>
 * Jun 28, 2008
 * 
 */
public class ContactDaoImpl extends HibernateDaoSupport implements ContactDao
{

    public ContactDaoImpl()
    {
	// TODO Auto-generated constructor stub
    }

    public void addUpdateContact(Contact contact)
    {
	getHibernateTemplate().merge(contact);
    }

    public void deleteContact(Contact contact)
    {
	getHibernateTemplate().delete(contact);
    }

    public Collection<Contact> findAllContacts()
    {

	Collection<Contact> result = (Collection<Contact>) getHibernateTemplate().execute(
		new HibernateCallback()
		{
		    public Object doInHibernate(Session session)
			    throws HibernateException
		    {
			Query query = session
				.createQuery("FROM Contact c");
			return query.list();
		    }
		});
	return result;
    }

    public Contact findContactByName(final String contactName)
    {
	Contact result = (Contact) getHibernateTemplate().execute(
		new HibernateCallback()
		{
		    public Object doInHibernate(Session session)
			    throws HibernateException
		    {
			Query query = session
				.createQuery("FROM Contact c WHERE c.name =:name");
			query.setString("name", contactName);
			return query.uniqueResult();
		    }
		});
	return result;

    }

    public Contact findContactById(final long id)
    {
	Contact result = (Contact) getHibernateTemplate().execute(
		new HibernateCallback()
		{
		    public Object doInHibernate(Session session)
			    throws HibernateException
		    {
			Query query = session
				.createQuery("FROM Contact c WHERE c.id =:id");
			query.setLong("id", id);
			return query.uniqueResult();
		    }
		});
	return result;

    }

    public Contact findContactByAttribute(final ContactAttrs attribute, final Object value)
    {
	Contact result = (Contact) getHibernateTemplate().execute(
		new HibernateCallback()
		{
		    public Object doInHibernate(Session session)
			    throws HibernateException
		    {
			Query query = session.createQuery(
				"FROM Contact c WHERE c." + attribute + " =:" + attribute);
			query.setParameter(attribute.toString(), value);
			return query.uniqueResult();
		    }
		});
	return result;
	
    }

    public Contact findContactByEmail(final String email)
    {
	Contact result = (Contact) getHibernateTemplate().execute(
		new HibernateCallback()
		{
		    public Object doInHibernate(Session session)
			    throws HibernateException
		    {
			Query query = session.createQuery(
			"FROM Contact c WHERE c.email =:email");
		query.setString("email", email);
			return query.uniqueResult();
		    }
		});
	return result;
    }

    public Contact findContactByPhone(final String phone)
    {
	Contact result = (Contact) getHibernateTemplate().execute(
		new HibernateCallback()
		{
		    public Object doInHibernate(Session session)
			    throws HibernateException
		    {
			Query query = session.createQuery(
			"FROM Contact c WHERE c.telephone =:phone");
		query.setString("phone", phone);
			return query.uniqueResult();
		    }
		});
	return result;
	
    }

}
