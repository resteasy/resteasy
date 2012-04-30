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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.junit.Assert;

import org.jboss.resteasy.plugins.providers.atom.Category;
import org.jboss.resteasy.plugins.providers.atom.app.AppCategories;
import org.junit.Test;

/**
 * @author <a href="mailto:kurt.stam@gmail.com">Kurt Stam</a>
 * @version $Revision: 1 $
 */
public class AppCategoriesTest
{
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

   @Test
   public void marshallAppCategories() throws Exception {
       JAXBContext jaxbContext=JAXBContext.newInstance("org.jboss.resteasy.test.providers.atom.app");
       Marshaller marshaller = jaxbContext.createMarshaller();
       marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
       marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
       marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
      
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
       JAXBElement<AppCategories> element = new JAXBElement<AppCategories>(new QName("","app:categories","app"),AppCategories.class,appCategories);
       
       marshaller.marshal(element,writer);
       String actualXml=writer.toString();
       System.out.println(actualXml);
       Assert.assertTrue(actualXml.contains("atom:category"));
   }
   
   @Test
   public void unmarshallAppCategories() throws Exception
   {
      JAXBContext ctx = JAXBContext.newInstance(AppCategories.class);
      Unmarshaller unmarshaller = ctx.createUnmarshaller();
      AppCategories categories = (AppCategories) unmarshaller.unmarshal(new StringReader(XML));
      Assert.assertTrue(categories.isFixed());
   }
}
