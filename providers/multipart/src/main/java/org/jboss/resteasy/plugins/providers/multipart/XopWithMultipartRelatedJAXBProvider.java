package org.jboss.resteasy.plugins.providers.multipart;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.jboss.resteasy.plugins.providers.jaxb.AbstractJAXBProvider;

/**
 * A special JAXB Provider. It is not a real provider, it is only used as a
 * helper class inside {@link XopWithMultipartRelatedReader} and
 * {@link XopWithMultipartRelatedWriter}.
 * 
 * @author Attila Kiraly
 * @version $Revision: 1 $
 */
public class XopWithMultipartRelatedJAXBProvider extends
		AbstractJAXBProvider<Object> {

	public XopWithMultipartRelatedJAXBProvider(Providers providers) {
		super();
		this.providers = providers;
	}

	@Override
	protected boolean isReadWritable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		throw new UnsupportedOperationException(
				"This provider and this method are not ment for stand alone usage.");
	}

	public Object readFrom(Class<Object> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders,
			InputStream entityStream, final MultipartRelatedInput xopPackage)
			throws IOException {
		try {
			InputPart rootPart = xopPackage.getRootPart();
			JAXBContext jaxb = findJAXBContext(type, annotations, rootPart
					.getMediaType(), true);
			Unmarshaller unmarshaller = jaxb.createUnmarshaller();
			unmarshaller
					.setAttachmentUnmarshaller(new AttachmentUnmarshaller() {
						@Override
						public byte[] getAttachmentAsByteArray(String cid) {
							InputPart inputPart = getInputPart(cid);
							try {
								return inputPart.getBody(byte[].class, null);
							} catch (IOException e) {
								throw new IllegalArgumentException(
										"Exception while extracting attachment with cid = "
												+ cid
												+ " from xop message to a byte[].",
										e);
							}
						}

						@Override
						public DataHandler getAttachmentAsDataHandler(
								final String cid) {
							final InputPart inputPart = getInputPart(cid);
							return new DataHandler(new DataSource() {
								public String getContentType() {
									return inputPart.getMediaType().toString();
								}

								public String getName() {
									return cid;
								}

								public InputStream getInputStream()
										throws IOException {
									return inputPart.getBody(InputStream.class,
											null);
								}

								public OutputStream getOutputStream()
										throws IOException {
									throw new IOException(
											"This DataSource represents an incoming xop message part. Getting an OutputStream on it is not allowed.");
								}
							});
						}

						protected InputPart getInputPart(String cid) {
							cid = cid.trim();
							int cidIndex = cid.indexOf("cid:");
							if (cidIndex == 0)
								cid = cid.substring(4).trim();
							InputPart inputPart = xopPackage.getRelatedMap()
									.get(cid);
							if (inputPart == null)
								throw new IllegalArgumentException(
										"No attachment with cid = " + cid
												+ " found in xop message.");
							return inputPart;
						}

						@Override
						public boolean isXOPPackage() {
							return true;
						}
					});
			return unmarshaller.unmarshal(new StreamSource(rootPart.getBody(
					InputStream.class, null)));
		} catch (JAXBException e) {
			Response response = Response.serverError().build();
			throw new WebApplicationException(e, response);
		}
	}

	public void writeTo(Object t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			final MultipartRelatedOutput xopPackage) throws IOException {
		try {
			Map<String, String> mediaTypeParameters = new LinkedHashMap<String, String>();
			mediaTypeParameters.put("charset", "UTF-8");
			mediaTypeParameters.put("type", "text/xml");

			MediaType xopRootMediaType = new MediaType("application",
					"xop+xml", mediaTypeParameters);

			Marshaller marshaller = getMarshaller(type, annotations,
					xopRootMediaType);
			marshaller.setAttachmentMarshaller(new AttachmentMarshaller() {

				@Override
				public String addMtomAttachment(DataHandler data,
						String elementNamespace, String elementLocalName) {
					String cid = generateContentId();
					xopPackage.addPart(data.getDataSource(), MediaType
							.valueOf(data.getContentType()), cid, "binary");
					return "cid:" + cid;
				}

				@Override
				public String addMtomAttachment(byte[] data, int offset,
						int length, String mimeType, String elementNamespace,
						String elementLocalName) {
					String cid = generateContentId();
					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
							data, offset, length);
					xopPackage.addPart(byteArrayInputStream, MediaType
							.valueOf(mimeType), cid, "binary");
					return "cid:" + cid;
				}

				protected String generateContentId() {
					return UUID.randomUUID().toString();
				}

				@Override
				public String addSwaRefAttachment(DataHandler data) {
					throw new UnsupportedOperationException(
							"SwaRefs are not supported in xop creation.");
				}

				@Override
				public boolean isXOPPackage() {
					return true;
				}

			});
			ByteArrayOutputStream xml = new ByteArrayOutputStream();
			marshaller.marshal(t, xml);

			OutputPart outputPart = xopPackage.addPart(xml
					.toString(getCharset(xopRootMediaType)), xopRootMediaType,
					UUID.randomUUID().toString(), null);
			List<OutputPart> outputParts = xopPackage.getParts();
			outputParts.remove(outputPart);
			outputParts.add(0, outputPart);
		} catch (JAXBException e) {
			Response response = Response.serverError().build();
			throw new WebApplicationException(e, response);
		}
	}

}
