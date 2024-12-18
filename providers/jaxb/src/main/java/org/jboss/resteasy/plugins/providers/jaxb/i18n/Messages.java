package org.jboss.resteasy.plugins.providers.jaxb.i18n;

import java.lang.invoke.MethodHandles;

import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.PropertyException;

import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageBundle;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 24, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages {
    Messages MESSAGES = org.jboss.logging.Messages.getBundle(MethodHandles.lookup(), Messages.class);
    int BASE = 6500;

    @Message(id = BASE
            + 00, value = "Collection wrapping failed, expected root element name of {0} got {1}", format = Format.MESSAGE_FORMAT)
    String collectionWrappingFailedLocalPart(String element, String localPart);

    @Message(id = BASE
            + 05, value = "Collection wrapping failed, expect namespace of {0} got {1}", format = Format.MESSAGE_FORMAT)
    String collectionWrappingFailedNamespace(String namespace, String uri);

    @Message(id = BASE + 10, value = "Could not find JAXBContextFinder for media type: %s")
    String couldNotFindJAXBContextFinder(MediaType mediaType);

    @Message(id = BASE + 15, value = "The method create%s() was not found in the object Factory!")
    String createMethodNotFound(Class<?> type);

    @Message(id = BASE + 20, value = "Error while trying to load schema for %s")
    String errorTryingToLoadSchema(String schema);

    @Message(id = BASE + 25, value = "Map wrapped failed, could not find map entry key attribute")
    String mapWrappedFailedKeyAttribute();

    @Message(id = BASE
            + 30, value = "Map wrapping failed, expected root element name of {0} got {1}", format = Format.MESSAGE_FORMAT)
    String mapWrappingFailedLocalPart(String map, String localPart);

    @Message(id = BASE + 35, value = "Map wrapping failed, expect namespace of {0} got {1}", format = Format.MESSAGE_FORMAT)
    String mapWrappingFailedNamespace(String map, String namespace);

    @Message(id = BASE
            + 40, value = "Could not create NamespacePrefixMapper. You need to use the JAXB RI for the prefix mapping feature")
    JAXBException namespacePrefixMapperNotInClassPath(@Cause Throwable cause);

    @Message(id = BASE + 45, value = "SecureUnmarshaller: unexpected use of unmarshal(%s)")
    String unexpectedUse(String s);

    @Message(id = BASE + 50, value = "Unable to find JAXBContext for media type: %s")
    String unableToFindJAXBContext(MediaType mediaType);

    @Message(id = BASE + 55, value = "A valid XmlRegistry could not be located.")
    String validXmlRegistryCouldNotBeLocated();

    @Message(id = BASE + 60, value = "Could not find user's JAXBContext implementation for media type: %s")
    String couldNotFindUsersJAXBContext(MediaType mediaType);

    @Message(id = BASE + 70, value = "Could not add property %s or renamed %s with value %s.")
    PropertyException couldNotAddProperty(String first, String second, Object value);

    @Message("Could not add property %s  with value %s.")
    PropertyException couldNotAddProperty(String name, Object value);

    @Message(id = BASE + 71, value = "Could not get property %s or renamed %s.")
    PropertyException couldNotGetProperty(String first, String second);

    @Message("Could not get property %s.")
    PropertyException couldNotGetProperty(String name);
}
