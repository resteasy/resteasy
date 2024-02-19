package org.jboss.resteasy.test.providers;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.namespace.QName;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.jboss.resteasy.plugins.providers.atom.Category;
import org.jboss.resteasy.plugins.providers.atom.app.AppCategories;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Providers
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for atom categories
 * @tpSince RESTEasy 3.0.16
 */
public class AppCategoriesTest {
    /**
     * Taken from the RFC 5023 section-8.2.
     * http://tools.ietf.org/html/rfc5023#section-7.1
     */
    private static final String XML = "<?xml version=\"1.0\" ?>\n" +
            "<app:categories xmlns:app=\"http://www.w3.org/2007/app\"\n" +
            "    xmlns:atom=\"http://www.w3.org/2005/Atom\" fixed=\"yes\"\n" +
            "    scheme=\"http://example.com/cats/big3\">\n" +
            "    <atom:category term=\"animal\" />\n" +
            "    <atom:category term=\"vegetable\" />\n" +
            "    <atom:category term=\"mineral\" />\n" +
            "</app:categories>";

    /**
     * @tpTestDetails Marshall categories
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void marshallAppCategories() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance("org.jboss.resteasy.test.providers.resource");
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());

        AppCategories appCategories = new AppCategories();
        appCategories.setFixed(true);
        appCategories.setScheme("http://example.com/cats/big3");
        Category category1 = new Category();
        category1.setTerm("animal");
        appCategories.getCategory().add(category1);
        Category category2 = new Category();
        category2.setTerm("vegetable");
        appCategories.getCategory().add(category2);
        Category category3 = new Category();
        category3.setTerm("mineral");
        appCategories.getCategory().add(category3);

        StringWriter writer = new StringWriter();
        JAXBElement<AppCategories> element = new JAXBElement<AppCategories>(new QName("", "app:categories", "app"),
                AppCategories.class, appCategories);

        marshaller.marshal(element, writer);
        String actualXml = writer.toString();
        Assertions.assertTrue(actualXml.contains("atom:category"), "Categories are missing");
    }

    /**
     * @tpTestDetails Unmarshall categories
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void unmarshallAppCategories() throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(AppCategories.class);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        AppCategories categories = (AppCategories) unmarshaller.unmarshal(new StringReader(XML));
        Assertions.assertTrue(categories.isFixed(), "Wrong categories");
    }
}
