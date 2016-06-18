package org.jboss.resteasy.resteasy1405;

import java.io.StringWriter;
import static java.util.Arrays.asList;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import static javax.ws.rs.core.MediaType.*;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class TestResteasy1405 {

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "resteasy1405.war")
            .addClasses(
                MyApplication.class,
                MyResource.class,
                ByFieldForm.class,
                BySetterForm.class,
                InputData.class,
                OutputData.class
            )
            .setWebXML("web.xml");
    }

    private static final String TEST_URI = "http://localhost:8080/resteasy1405";

    private JAXBContext jaxbc;
    private Client client;

    @Before
    public void setup() throws JAXBException {
        jaxbc = JAXBContext.newInstance(InputData.class);
        client = ClientBuilder.newClient();
    }

    @After
    public void done() {
        client.close();
    }

    @Test
    public void testInputPartByField() throws Exception {
        WebTarget post = client.target(TEST_URI + "/field");

        InputData data = new InputData();
        data.setItems(asList("value1", "value2"));

        MultipartFormDataOutput multipart = new MultipartFormDataOutput();
        multipart.addFormData("name", "Test by field", TEXT_PLAIN_TYPE);
        multipart.addFormData("data", asXml(data), APPLICATION_XML_TYPE);
        GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(multipart) {};

        Response response = post.request().post(Entity.entity(entity, MULTIPART_FORM_DATA_TYPE));
        try {
            assertEquals(200, response.getStatus());
            assertEquals("OutputData[name='Test by field', contentType='application/xml', items={value1,value2}]",
                         response.readEntity(String.class));
        } finally {
            response.close();
        }
    }

    @Test
    public void testInputPartBySetter() throws Exception {
        WebTarget post = client.target(TEST_URI + "/setter");

        InputData data = new InputData();
        data.setItems(asList("value1", "value2"));

        MultipartFormDataOutput multipart = new MultipartFormDataOutput();
        multipart.addFormData("name", "Test by setter", TEXT_PLAIN_TYPE);
        multipart.addFormData("data", asXml(data), APPLICATION_XML_TYPE);
        GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(multipart) {};

        Response response = post.request().post(Entity.entity(entity, MULTIPART_FORM_DATA_TYPE));
        try {
            assertEquals(200, response.getStatus());
            assertEquals("OutputData[name='Test by setter', contentType='application/xml', items={value1,value2}]",
                          response.readEntity(String.class));
        } finally {
            response.close();
        }
    }

    private String asXml(Object obj) throws JAXBException {
        Marshaller m = jaxbc.createMarshaller();
        m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        StringWriter writer = new StringWriter();
        m.marshal(obj, writer);
        return writer.toString();
    }
}
