package org.jboss.resteasy.test.providers.jaxb.resource.homecontrol;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the de.creaity.homecontrol.service.rest.jaxb.v1 package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

   private final static QName _User_QNAME = new QName("http://creaity.de/homecontrol/rest/types/v1", "user");
   private final static QName _Error_QNAME = new QName("http://creaity.de/homecontrol/rest/types/v1", "error");
   private final static QName _Id_QNAME = new QName("http://creaity.de/homecontrol/rest/types/v1", "id");

   /**
    * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.creaity.homecontrol.service.rest.jaxb.v1
    *
    */
   public ObjectFactory() {
   }

   /**
    * Create an instance of {@link UserType }
    *
    */
   public UserType createUserType() {
      return new UserType();
   }

   /**
    * Create an instance of {@link ErrorMessageType }
    *
    */
   public ErrorMessageType createErrorMessageType() {
      return new ErrorMessageType();
   }

   /**
    * Create an instance of {@link IDType }
    */
   public IDType createIDType() {
      return new IDType();
   }

   /**
    * Create an instance of {@link BinaryType }
    *
    */
   public BinaryType createBinaryType() {
      return new BinaryType();
   }

   /**
    * Create an instance of {@link UserType.Credentials }
    *
    */
   public UserType.Credentials createUserTypeCredentials() {
      return new UserType.Credentials();
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link UserType }{@code >}}
    *
    */
   @XmlElementDecl(namespace = "http://creaity.de/homecontrol/rest/types/v1", name = "user")
   public JAXBElement<UserType> createUser(UserType value) {
      return new JAXBElement<UserType>(_User_QNAME, UserType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link ErrorMessageType }{@code >}}
    *
    */
   @XmlElementDecl(namespace = "http://creaity.de/homecontrol/rest/types/v1", name = "error")
   public JAXBElement<ErrorMessageType> createError(ErrorMessageType value) {
      return new JAXBElement<ErrorMessageType>(_Error_QNAME, ErrorMessageType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link IDType }{@code >}}
    *
    */
   @XmlElementDecl(namespace = "http://creaity.de/homecontrol/rest/types/v1", name = "id")
   public JAXBElement<IDType> createId(IDType value) {
      return new JAXBElement<IDType>(_Id_QNAME, IDType.class, null, value);
   }

}
