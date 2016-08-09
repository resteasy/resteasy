package org.jboss.resteasy.test.providers;

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider;
import org.jboss.resteasy.test.providers.namespace.mapping.NamespaceMappingTestBase;
import org.jboss.resteasy.test.providers.namespace.mapping.NamespaceMappingTestExtends;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;

/**
 * @tpSubChapter Providers
 * @tpChapter Unit tests
 * @tpTestCaseDetails Namespace mapping test for jettison provider
 * @tpSince RESTEasy 3.0.16
 */
public class NamespaceMappingTest {
    static JAXBContext ctx = null;
    static Unmarshaller unmarshaller = null;
    static Marshaller marshaller = null;

    @BeforeClass
    public static void setup() throws Exception {
        ctx = JAXBContext.newInstance("org.jboss.resteasy.test.providers.namespace.mapping");
        unmarshaller = ctx.createUnmarshaller();
        marshaller = ctx.createMarshaller();
    }

    /**
     * @tpTestDetails Marshalling and unmarshalling test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testManual() throws Exception {
        String marshallError = "Wrong marshalling";
        String unmarshallError = "Wrong unmarshaling";

        String str = marshall();
        Assert.assertThat(marshallError, str, containsString("12121"));
        Assert.assertThat(marshallError, str, containsString("\"Test\""));
        Assert.assertThat(marshallError, str, containsString("\"Desc\""));
        Assert.assertThat(marshallError, str, containsString("\"test\""));
        Assert.assertThat(marshallError, str, containsString("\"element2\":\"Test\""));
        NamespaceMappingTestExtends val = unmarshall(str);
        Assert.assertEquals(unmarshallError, val.getId(), "12121");
        Assert.assertEquals(unmarshallError, val.getName(), "Test");
        Assert.assertEquals(unmarshallError, val.getDesc(), "Desc");
        Assert.assertEquals(unmarshallError, val.getSomeMoreEl(), "test");
        Assert.assertEquals(unmarshallError, val.getElement2(), "Test");
    }

    private String marshall() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(NamespaceMappingTestExtends.class, NamespaceMappingTestBase.class);

        NamespaceMappingTestExtends result = new NamespaceMappingTestExtends();
        result.setId("12121");
        result.setName("Test");
        result.setDesc("Desc");
        result.setElement2("Test");
        result.setSomeMoreEl("test");

        Configuration config = new Configuration();
        Map<String, String> xmlToJsonNamespaces = new HashMap<String, String>(1);
        xmlToJsonNamespaces.put("http://www.example.org/b/Test", "test");
        xmlToJsonNamespaces.put("http://www.example.org/a/TestCanonical", "can");
        config.setXmlToJsonNamespaces(xmlToJsonNamespaces);
        MappedNamespaceConvention con = new MappedNamespaceConvention(config);
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(con, writer);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(JAXBXmlTypeProvider.wrapInJAXBElement(result, NamespaceMappingTestExtends.class), xmlStreamWriter);
        return writer.toString();
    }

    private NamespaceMappingTestExtends unmarshall(String output) throws Exception {
        JAXBContext jc = JAXBContext.newInstance("org.jboss.resteasy.test.providers.namespace.mapping");
        Configuration config = new Configuration();
        Map<String, String> xmlToJsonNamespaces = new HashMap<String, String>(1);
        xmlToJsonNamespaces.put("http://www.example.org/b/Test", "test");
        xmlToJsonNamespaces.put("http://www.example.org/a/TestCanonical", "can");
        config.setXmlToJsonNamespaces(xmlToJsonNamespaces);
        MappedNamespaceConvention con = new MappedNamespaceConvention(config);
        XMLStreamReader xmlStreamReader = new MappedXMLStreamReader(new JSONObject(output), con);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        @SuppressWarnings(value = "unchecked")
        JAXBElement<NamespaceMappingTestExtends> val = (JAXBElement<NamespaceMappingTestExtends>) unmarshaller.unmarshal(xmlStreamReader);
        return val.getValue();
    }
}
