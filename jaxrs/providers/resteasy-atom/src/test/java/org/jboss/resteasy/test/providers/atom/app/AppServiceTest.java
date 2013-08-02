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

import org.jboss.resteasy.plugins.providers.atom.Category;
import org.jboss.resteasy.plugins.providers.atom.app.AppAccept;
import org.jboss.resteasy.plugins.providers.atom.app.AppCategories;
import org.jboss.resteasy.plugins.providers.atom.app.AppCollection;
import org.jboss.resteasy.plugins.providers.atom.app.AppService;
import org.jboss.resteasy.plugins.providers.atom.app.AppWorkspace;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;

/**
 * @author <a href="mailto:kurt.stam@gmail.com">Kurt Stam</a>
 * @version $Revision: 1 $
 */
public class AppServiceTest {
    /**
     * Taken from the RFC 5023 section-8.2.
     * http://tools.ietf.org/html/rfc5023#section-8.2
     */
    private static final String XML = "<?xml version=\"1.0\" encoding='utf-8'?>\n"
            + "   <service xmlns=\"http://www.w3.org/2007/app\"\n"
            + "            xmlns:atom=\"http://www.w3.org/2005/Atom\">\n"
            + "     <workspace>\n"
            + "       <atom:title>Main Site</atom:title>\n"
            + "       <collection\n"
            + "           href=\"http://example.org/blog/main\" >\n"
            + "         <atom:title>My Blog Entries</atom:title>\n"
            + "         <categories\n"
            + "            href=\"http://example.com/cats/forMain.cats\" />\n"
            + "       </collection>\n"
            + "       <collection\n"
            + "           href=\"http://example.org/blog/pic\" >\n"
            + "         <atom:title>Pictures</atom:title>\n"
            + "         <accept>image/png</accept>\n"
            + "         <accept>image/jpeg</accept>\n"
            + "         <accept>image/gif</accept>\n"
            + "       </collection>\n"
            + "     </workspace>\n"
            + "     <workspace>\n"
            + "       <atom:title>Sidebar Blog</atom:title>\n"
            + "       <collection\n"
            + "           href=\"http://example.org/sidebar/list\" >\n"
            + "         <atom:title>Remaindered Links</atom:title>\n"
            + "         <accept>application/atom+xml;type=entry</accept>\n"
            + "         <categories fixed=\"yes\">\n"
            + "           <atom:category\n"
            + "             scheme=\"http://example.org/extra-cats/\"\n"
            + "             term=\"joke\" />\n"
            + "           <atom:category\n"
            + "             scheme=\"http://example.org/extra-cats/\"\n"
            + "             term=\"serious\" />\n"
            + "         </categories>\n"
            + "       </collection>\n"
            + "     </workspace>\n"
            + "   </service>";

    @Test
    public void marshallAppService() throws Exception {
        JAXBContext jaxbContext = JAXBContext
                .newInstance("org.jboss.resteasy.test.providers.atom.app");
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        AppService service = new AppService();

        AppWorkspace workspace1 = new AppWorkspace();
        workspace1.setTitle("Main Site");
        AppCollection collection1 = new AppCollection();
        collection1.setHref("http://example.org/blog/main");
        collection1.setTitle("My Blog Entries");
        AppCategories categories1 = new AppCategories();
        categories1.setHref("http://example.com/cats/forMain.cats");
        collection1.getCategories().add(categories1);
        workspace1.getCollection().add(collection1);

        AppCollection collection2 = new AppCollection();
        collection2.setHref("http://example.org/blog/pic");
        collection2.setTitle("Pictures");
        AppAccept accept1 = new AppAccept();
        accept1.setContent("image/png");
        collection2.getAccept().add(accept1);
        AppAccept accept2 = new AppAccept();
        accept2.setContent("image/jpg");
        collection2.getAccept().add(accept2);
        AppAccept accept3 = new AppAccept();
        accept3.setContent("image/gif");
        collection2.getAccept().add(accept3);
        workspace1.getCollection().add(collection2);

        service.getWorkspace().add(workspace1);

        AppWorkspace workspace2 = new AppWorkspace();
        workspace1.setTitle("Sidebar Blog");
        AppCollection collection3 = new AppCollection();
        collection3.setHref("http://example.org/sidebar/list");
        collection3.setTitle("Remaindered Links");
        AppAccept accept4 = new AppAccept();
        accept4.setContent("application/atom+xml;type=entry");
        collection3.getAccept().add(accept4);
        AppCategories categories3 = new AppCategories();
        categories3.setFixed(true);
        Category category1 = new Category();
        category1.setScheme(new URI("http://example.org/extra-cats/"));
        category1.setTerm("joke");
        categories3.getCategory().add(category1);
        Category category2 = new Category();
        category2.setScheme(new URI("http://example.org/extra-cats/"));
        category2.setTerm("serious");
        categories3.getCategory().add(category2);

        collection3.getCategories().add(categories3);
        workspace2.getCollection().add(collection3);

        service.getWorkspace().add(workspace2);

        StringWriter writer = new StringWriter();
        JAXBElement<AppService> element = new JAXBElement<AppService>(
                new QName("", "service", ""), AppService.class, service);

        marshaller.marshal(element, writer);
        String actualXml = writer.toString();
        System.out.println(actualXml);
        Assert.assertTrue(actualXml.contains("atom:category"));
    }

    @Test
    public void unmarshallAppService() throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(AppService.class);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        AppService service = (AppService) unmarshaller
                .unmarshal(new StringReader(XML));
        Assert.assertEquals(2, service.getWorkspace().size());
    }
}
