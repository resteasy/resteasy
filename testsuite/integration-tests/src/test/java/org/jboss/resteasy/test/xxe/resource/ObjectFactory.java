package org.jboss.resteasy.test.xxe.resource;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This class can not be renamed, JAXB needs this name.
 */
@XmlRegistry
public class ObjectFactory {
    /*
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

    private static final QName _FavoriteMovie_QNAME = new QName("", "favoriteMovie");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SecureProcessingFavoriteMovieXmlType }
     */
    public SecureProcessingFavoriteMovieXmlType createFavoriteMovieXmlType() {
        return new SecureProcessingFavoriteMovieXmlType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SecureProcessingFavoriteMovieXmlType }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "favoriteMovie")
    public JAXBElement<SecureProcessingFavoriteMovieXmlType> createFavoriteMovie(SecureProcessingFavoriteMovieXmlType value) {
        return new JAXBElement<SecureProcessingFavoriteMovieXmlType>(_FavoriteMovie_QNAME, SecureProcessingFavoriteMovieXmlType.class, null, value);
    }

}
