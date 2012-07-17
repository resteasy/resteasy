/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.jboss.resteasy.test.providers.atom.app;

import org.jboss.resteasy.plugins.providers.atom.app.AppAccept;
import org.jboss.resteasy.plugins.providers.atom.app.AppCategories;
import org.jboss.resteasy.plugins.providers.atom.app.AppCollection;
import org.jboss.resteasy.plugins.providers.atom.app.AppService;
import org.jboss.resteasy.plugins.providers.atom.app.AppWorkspace;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the org.w3._2007.app package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: org.w3._2007.app
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AppWorkspace }
     * 
     */
    public AppWorkspace createAppWorkspace() {
        return new AppWorkspace();
    }

    /**
     * Create an instance of {@link AppService }
     * 
     */
    public AppService createAppService() {
        return new AppService();
    }

    /**
     * Create an instance of {@link AppAccept }
     * 
     */
    public AppAccept createAppAccept() {
        return new AppAccept();
    }

    /**
     * Create an instance of {@link AppCategories }
     * 
     */
    public AppCategories createAppCategories() {
        return new AppCategories();
    }

    /**
     * Create an instance of {@link AppCollection }
     * 
     */
    public AppCollection createAppCollection() {
        return new AppCollection();
    }

}
