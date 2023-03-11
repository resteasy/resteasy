package org.jboss.resteasy.test.providers.atom.resource;

import java.net.URI;
import java.util.Date;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Person;

@Path("/")
public class AtomComplexModelEntryResource {

    @POST
    @Path("entry")
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public AtomAssetMetadata entry(Entry entry, @Context UriInfo uriInfo) {
        try {
            AtomAssetMetadata assetMetadata = entry.getAnyOtherJAXBObject(AtomAssetMetadata.class);
            return assetMetadata;
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("entry2")
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry entry2(@Context UriInfo uriInfo) {
        try {
            return toAssetEntry(uriInfo);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Path("entry3")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public AtomAssetMetadata entry3(Entry entry, @Context UriInfo uriInfo) {
        try {
            AtomAssetMetadata assetMetadata = entry.getAnyOtherJAXBObject(AtomAssetMetadata.class);
            return assetMetadata;
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("entry4")
    @AtomComplexModelAtomAssetMetadataDecorators
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Entry entry4(@Context UriInfo uriInfo) {
        try {
            return toAssetEntry(uriInfo);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    public Entry toAssetEntry(UriInfo uriInfo) throws Exception {
        URI baseUri;
        baseUri = uriInfo.getBaseUriBuilder()
                .path("packages/{packageName}/assets/{assetName}")
                .build("testpackageName", "testassetName");

        Entry entry = new Entry();
        entry.setTitle("testtitle");
        entry.setSummary("testdesc");
        entry.setPublished(new Date());
        entry.setBase(baseUri);
        entry.getAuthors().add(new Person("testperson"));

        entry.setId(baseUri);

        AtomAssetMetadata atomAssetMetadata = entry.getAnyOtherJAXBObject(AtomAssetMetadata.class);
        if (atomAssetMetadata == null) {
            atomAssetMetadata = new AtomAssetMetadata();
        }
        atomAssetMetadata.setArchived(false);
        atomAssetMetadata.setUuid("testuuid");

        entry.setAnyOtherJAXBObject(atomAssetMetadata);

        Content content = new Content();
        content.setSrc(UriBuilder.fromUri(baseUri).path("binary").build());
        content.setType(MediaType.APPLICATION_OCTET_STREAM_TYPE);

        entry.setContent(content);

        return entry;
    }

}
