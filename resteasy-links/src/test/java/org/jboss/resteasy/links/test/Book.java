package org.jboss.resteasy.links.test;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlRootElement;

// import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
// import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.jboss.resteasy.links.RESTServiceDiscovery;

// TODO: We need to add @XmlNsMap support in Jackson provider
// The JIRA issue to track the progress: https://issues.jboss.org/browse/RESTEASY-2067
//@Mapped(namespaceMap = @XmlNsMap(jsonName = "atom", namespace = "http://www.w3.org/2005/Atom"))
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Book {
    @XmlAttribute
    private String author;
    @XmlID
    @XmlAttribute
    private String title;
    @XmlElement
    // These both fail deserialisation for some reason
    //   @XmlElement(name = "link", namespace = "http://www.w3.org/2005/Atom")
    //   @XmlElementRef
    private RESTServiceDiscovery rest;

    private List<Comment> comments = new ArrayList<Comment>();

    public Book(final String title, final String author) {
        this.author = author;
        this.title = title;
    }

    public Book() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void addComment(String id, String text) {
        comments.add(new Comment(id, text, this));
    }

    public Comment getComment(int commentId) {
        for (Comment c : comments)
            if (Integer.valueOf(c.getId()).equals(commentId))
                return c;
        return null;
    }
}
