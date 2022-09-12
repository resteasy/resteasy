package org.jboss.resteasy.links.test;

import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.ResourceFacade;
import org.jboss.resteasy.links.ResourceID;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ScrollableCollection implements ResourceFacade<Comment> {
   @ResourceID
   private String id;
   @XmlAttribute
   private int start;
   @XmlAttribute
   private int limit;
   @XmlAttribute
   private int totalRecords;
   @XmlTransient
   private String query;
   @XmlElement
   private List<Comment> comments = new ArrayList<Comment>();
   @XmlElement
   private RESTServiceDiscovery rest;

   public ScrollableCollection() {}

   public ScrollableCollection(final String id, final int start, final int limit, final int totalRecords,
                               final List<Comment> comments, final String query) {
      this.id = id;
      this.start = start;
      this.limit = limit;
      this.totalRecords = totalRecords;
      this.comments.addAll(comments);
      this.setQuery(query);
   }

   public Class<Comment> facadeFor() {
      return Comment.class;
   }

   public Map<String, ? extends Object> pathParameters() {
      HashMap<String, String> map = new HashMap<String, String>();
      map.put("id", id);
      return map;
   }

   public int getStart() {
      return start;
   }

   public void setStart(int start) {
      this.start = start;
   }

   public int getLimit() {
      return limit;
   }

   public void setLimit(int limit) {
      this.limit = limit;
   }

   public int getTotalRecords() {
      return totalRecords;
   }

   public void setTotalRecords(int totalRecords) {
      this.totalRecords = totalRecords;
   }

   public RESTServiceDiscovery getRest() {
      return rest;
   }

   public void setRest(RESTServiceDiscovery rest) {
      this.rest = rest;
   }

   public List<Comment> getComments() {
      return comments;
   }

   public void setComments(List<Comment> comments) {
      this.comments = comments;
   }

   public String getQuery() {
      return query;
   }

   public void setQuery(String query) {
      this.query = query;
   }

}
