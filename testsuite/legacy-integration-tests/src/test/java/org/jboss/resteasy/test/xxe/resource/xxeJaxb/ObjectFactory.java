package org.jboss.resteasy.test.xxe.resource.xxeJaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the generated package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _FavoriteMovie_QNAME = new QName("", "xxeJaxbFavoriteMovie");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XxeJaxbFavoriteMovieXmlType }
     */
    public XxeJaxbFavoriteMovieXmlType createFavoriteMovieXmlType() {
        return new XxeJaxbFavoriteMovieXmlType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XxeJaxbFavoriteMovieXmlType }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "xxeJaxbFavoriteMovie")
    public JAXBElement<XxeJaxbFavoriteMovieXmlType> createFavoriteMovie(XxeJaxbFavoriteMovieXmlType value) {
        return new JAXBElement<XxeJaxbFavoriteMovieXmlType>(_FavoriteMovie_QNAME, XxeJaxbFavoriteMovieXmlType.class, null, value);
    }

}
