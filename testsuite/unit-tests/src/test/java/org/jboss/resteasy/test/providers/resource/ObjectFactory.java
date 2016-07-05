package org.jboss.resteasy.test.providers.resource;

import org.jboss.resteasy.plugins.providers.atom.app.AppAccept;
import org.jboss.resteasy.plugins.providers.atom.app.AppCategories;
import org.jboss.resteasy.plugins.providers.atom.app.AppCollection;
import org.jboss.resteasy.plugins.providers.atom.app.AppService;
import org.jboss.resteasy.plugins.providers.atom.app.AppWorkspace;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This class shouldn't be renamed. JAXBContext require this name.
 *
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the org.w3._2007.app package.
 *
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: org.w3._2007.app
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AppWorkspace }
     */
    public AppWorkspace createAppWorkspace() {
        return new AppWorkspace();
    }

    /**
     * Create an instance of {@link AppService }
     */
    public AppService createAppService() {
        return new AppService();
    }

    /**
     * Create an instance of {@link AppAccept }
     */
    public AppAccept createAppAccept() {
        return new AppAccept();
    }

    /**
     * Create an instance of {@link AppCategories }
     */
    public AppCategories createAppCategories() {
        return new AppCategories();
    }

    /**
     * Create an instance of {@link AppCollection }
     */
    public AppCollection createAppCollection() {
        return new AppCollection();
    }

}
