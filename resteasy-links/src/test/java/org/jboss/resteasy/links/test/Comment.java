package org.jboss.resteasy.links.test;

import org.jboss.resteasy.links.ParentResource;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Comment {

   @XmlAttribute
   @XmlID
   public String id;

   @XmlElement
   public String text;
   @ParentResource
   public Book book;

   @XmlElement
   private RESTServiceDiscovery rest;

   public Comment() {
   }

   public Comment(final String id, final String text, final Book book) {
      this.id = id;
      this.text = text;
      this.book = book;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public Book getBook() {
      return book;
   }

   public void setBook(Book book) {
      this.book = book;
   }

   public RESTServiceDiscovery getRest() {
      return rest;
   }

   public void setRest(RESTServiceDiscovery rest) {
      this.rest = rest;
   }
}
