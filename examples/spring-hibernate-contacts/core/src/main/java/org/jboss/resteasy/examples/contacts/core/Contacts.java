package org.jboss.resteasy.examples.contacts.core;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author <a href="mailto:obrand@yahoo.com">Olivier Brand</a>
 * Jun 28, 2008
 * 
 */
@XmlRootElement(name = "contacts")
public class Contacts
{
    private Collection<Contact> contacts;

    @XmlElement(name = "contact")
    public Collection<Contact> getContacts()
    {
        return contacts;
    }

    public void setContacts(Collection<Contact> contacts)
    {
        this.contacts = contacts;
    }
    
    
}
