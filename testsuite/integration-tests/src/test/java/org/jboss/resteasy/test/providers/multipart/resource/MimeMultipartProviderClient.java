package org.jboss.resteasy.test.providers.multipart.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.MultipartConstants;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;

import java.util.List;
import java.util.Map;

@Path("mime")
public interface MimeMultipartProviderClient {
    @Path("mixed")
    @PUT
    @Consumes("multipart/mixed")
    void putMixed(MultipartOutput output);

    @Path("form")
    @PUT
    @Consumes("multipart/form-data")
    void putFormData(MultipartFormDataOutput output);

    @Path("related")
    @PUT
    @Consumes(MultipartConstants.MULTIPART_RELATED)
    void putRelated(MultipartRelatedOutput output);

    @Path("mixed")
    @PUT
    @Consumes("multipart/mixed")
    void putMixedList(
            @PartType("application/xml") List<MimeMultipartProviderCustomer> mimeMultipartProviderCustomers);

    @Path("form")
    @PUT
    @Consumes("multipart/form-data")
    void putFormDataMap(
            @PartType("application/xml") Map<String, MimeMultipartProviderCustomer> customers);

    @Path("form/class")
    @PUT
    @Consumes("multipart/form-data")
    void putFormDataMap(
            @MultipartForm MimeMultipartProviderResource.Form form);

    @Path("xop")
    @PUT
    @Consumes(MultipartConstants.MULTIPART_RELATED)
    void putXop(
            @XopWithMultipartRelated MimeMultipartProviderResource.Xop bean);
}
