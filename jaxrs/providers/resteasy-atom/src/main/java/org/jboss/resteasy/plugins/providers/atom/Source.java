package org.jboss.resteasy.plugins.providers.atom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"title", "subtitle", "categories", "updated", "id", "links", "authors", "contributors", "rights",
        "icon", "logo", "generator"})
public class Source extends CommonAttributes
{
   @XmlElement(name = "author")
   private List<Person> authors = new ArrayList<Person>();
   @XmlElementRef
   private List<Category> categories = new ArrayList<Category>();
   @XmlElement(name = "contributor")
   private List<Person> contributors = new ArrayList<Person>();
   @XmlElementRef
   private Generator generator;
   @XmlElement
   private URI id;
   @XmlElement
   private String title;
   @XmlElement
   private Date updated;
   @XmlElementRef
   private List<Link> links = new ArrayList<Link>();
   @XmlElement
   private URI icon;
   @XmlElement
   private URI logo;
   @XmlElement
   private String rights;
   @XmlElement
   private String subtitle;

   public List<Person> getAuthors()
   {
      return authors;
   }

   public List<Person> getContributors()
   {
      return contributors;
   }

   public URI getId()
   {
      return id;
   }

   public void setId(URI id)
   {
      this.id = id;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public Date getUpdated()
   {
      return updated;
   }

   public void setUpdated(Date updated)
   {
      this.updated = updated;
   }

   public List<Link> getLinks()
   {
      return links;
   }

   public List<Category> getCategories()
   {
      return categories;
   }

   public Generator getGenerator()
   {
      return generator;
   }

   public void setGenerator(Generator generator)
   {
      this.generator = generator;
   }
}
