package org.jboss.resteasy.test.providers;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.providers.resource.ContractsData;
import org.jboss.resteasy.test.providers.resource.ContractsDataReaderWriter;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @tpSubChapter Providers
 * @tpChapter Unit tests
 * @tpTestCaseDetails Correct selection of Reader and Writer
 * @tpSince RESTEasy 3.0.16
 */
public class ContractsTest {

    /**
     * @tpTestDetails Basic test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLimitedContract() {
        ResteasyProviderFactory factory = new ResteasyProviderFactory();
        factory.register(ContractsDataReaderWriter.class, MessageBodyReader.class);
        MessageBodyReader reader = factory.getMessageBodyReader(ContractsData.class, ContractsData.class, null, MediaType.APPLICATION_ATOM_XML_TYPE);
        Assert.assertNotNull("Reader is not assigned", reader);
        MessageBodyWriter writer = factory.getMessageBodyWriter(ContractsData.class, ContractsData.class, null, MediaType.APPLICATION_ATOM_XML_TYPE);
        Assert.assertNull("Writer is not assigned", writer);
    }

    /**
     * @tpTestDetails Test for map
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLimitedContractMap() {
        ResteasyProviderFactory factory = new ResteasyProviderFactory();
        Map<Class<?>, Integer> contract = new HashMap<Class<?>, Integer>();
        contract.put(MessageBodyReader.class, 5);
        factory.register(ContractsDataReaderWriter.class, contract);
        MessageBodyReader reader = factory.getMessageBodyReader(ContractsData.class, ContractsData.class, null, MediaType.APPLICATION_ATOM_XML_TYPE);
        Assert.assertNotNull("Reader is not assigned", reader);
        MessageBodyWriter writer = factory.getMessageBodyWriter(ContractsData.class, ContractsData.class, null, MediaType.APPLICATION_ATOM_XML_TYPE);
        Assert.assertNull("Writer is not assigned", writer);
    }

}
