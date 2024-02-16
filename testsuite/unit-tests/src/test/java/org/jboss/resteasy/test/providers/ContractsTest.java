package org.jboss.resteasy.test.providers;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.providers.resource.ContractsData;
import org.jboss.resteasy.test.providers.resource.ContractsDataReaderWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        factory.register(ContractsDataReaderWriter.class, MessageBodyReader.class);
        MessageBodyReader reader = factory.getMessageBodyReader(ContractsData.class, ContractsData.class, null,
                MediaType.APPLICATION_ATOM_XML_TYPE);
        Assertions.assertNotNull(reader, "Reader is not assigned");
        MessageBodyWriter writer = factory.getMessageBodyWriter(ContractsData.class, ContractsData.class, null,
                MediaType.APPLICATION_ATOM_XML_TYPE);
        Assertions.assertNull(writer, "Writer is not assigned");
    }

    /**
     * @tpTestDetails Test for map
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLimitedContractMap() {
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        Map<Class<?>, Integer> contract = new HashMap<Class<?>, Integer>();
        contract.put(MessageBodyReader.class, 5);
        factory.register(ContractsDataReaderWriter.class, contract);
        MessageBodyReader reader = factory.getMessageBodyReader(ContractsData.class, ContractsData.class, null,
                MediaType.APPLICATION_ATOM_XML_TYPE);
        Assertions.assertNotNull(reader, "Reader is not assigned");
        MessageBodyWriter writer = factory.getMessageBodyWriter(ContractsData.class, ContractsData.class, null,
                MediaType.APPLICATION_ATOM_XML_TYPE);
        Assertions.assertNull(writer, "Writer is not assigned");
    }

}
