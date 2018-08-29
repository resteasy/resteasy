package org.jboss.resteasy.wadl;

import org.jboss.resteasy.wadl.i18n.LogMessages;
import org.jboss.resteasy.wadl.i18n.Messages;
import org.jboss.resteasy.wadl.jaxb.Application;
import org.jboss.resteasy.wadl.jaxb.Method;
import org.jboss.resteasy.wadl.jaxb.ObjectFactory;
import org.jboss.resteasy.wadl.jaxb.Param;
import org.jboss.resteasy.wadl.jaxb.ParamStyle;
import org.jboss.resteasy.wadl.jaxb.Representation;
import org.jboss.resteasy.wadl.jaxb.Request;
import org.jboss.resteasy.wadl.jaxb.Resource;
import org.jboss.resteasy.wadl.jaxb.Resources;
import org.jboss.resteasy.wadl.jaxb.Response;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.jboss.resteasy.wadl.ResteasyWadlMethodParamMetaData.MethodParamType.*;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class ResteasyWadlWriter {

    public byte[] getBytes(String base, Map<String, ResteasyWadlServiceRegistry> serviceRegistries) throws JAXBException {
        StringWriter stringWriter = getStringWriter(base, serviceRegistries);
        return stringWriter.toString().getBytes();
    }

    public StringWriter getStringWriter(String base, Map<String, ResteasyWadlServiceRegistry> serviceRegistries) throws JAXBException {
        ObjectFactory factory = new ObjectFactory();
        Application app = factory.createApplication();
        JAXBContext context = JAXBContext.newInstance(Application.class);
        Marshaller marshaller = context.createMarshaller();

        for (Map.Entry<String, ResteasyWadlServiceRegistry> entry : serviceRegistries.entrySet()) {
            String uri = base;
            if (entry.getKey() != null) uri += entry.getKey();
            Resources resources = new Resources();
            resources.setBase(uri);
            app.getResources().add(resources);
            processWadl(entry.getValue(), resources);
        }

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(app, writer);
        return stringWriter;
    }

    private void processWadl(ResteasyWadlServiceRegistry serviceRegistry, Resources root) throws JAXBException {

        for (Map.Entry<String, ResteasyWadlResourceMetaData> resourceMetaDataEntry : serviceRegistry.getResources().entrySet()) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.path(resourceMetaDataEntry.getKey()));
            Resource resourceClass = new Resource();

            resourceClass.setPath(resourceMetaDataEntry.getKey());
            root.getResource().add(resourceClass);

            for (ResteasyWadlMethodMetaData methodMetaData : resourceMetaDataEntry.getValue().getMethodsMetaData()) {
                Method method = new Method();

                // First we need to check whether @Path annotation exists in a method.
                // If the @Path annotation exists, we need to create a resource for it.
                if (methodMetaData.getMethodUri() != null) {
                    Resource methodResource = new Resource();
                    methodResource.setPath(methodMetaData.getMethodUri());
                    methodResource.getMethodOrResource().add(method);
                    resourceClass.getMethodOrResource().add(methodResource);
                    // add params into method resource
                    processMethodParams(methodResource, methodMetaData, method);
                } else {
                    // register method into resource
                    resourceClass.getMethodOrResource().add(method);
                    // we need to check whether the method have parameters or not.
                    // params belong to the resource of the method
                    processMethodParams(resourceClass, methodMetaData, method);
                }

                // method name = {GET, POST, DELETE, ...}
                for (String name : methodMetaData.getHttpMethods()) {
                    method.setName(name);
                }

                // method id = method name
                method.setId(methodMetaData.getMethod().getName());

                // process response of method
                Response response = createResponse(serviceRegistry, methodMetaData);
                method.getResponse().add(response);
            }
        }

        for (ResteasyWadlServiceRegistry subService : serviceRegistry.getLocators())
            processWadl(subService, root);
    }

    private void processMethodParams(Resource currentResourceClass, ResteasyWadlMethodMetaData methodMetaData, Method method) {
        // process method parameters
        Request request = new Request();

        for (ResteasyWadlMethodParamMetaData paramMetaData : methodMetaData.getParameters()) {
            Param param = createParam(currentResourceClass, method, paramMetaData, request);
        }
    }

    private Response createResponse(ResteasyWadlServiceRegistry serviceRegistry, ResteasyWadlMethodMetaData methodMetaData) {
        Response response = new Response();

        Class _type = methodMetaData.getMethod().getReturnType();
        Type _generic = methodMetaData.getMethod().getGenericReturnType();

        MediaType mediaType = MediaType.WILDCARD_TYPE;

        if (methodMetaData.getProduces() != null) {
            for (String produces : methodMetaData.getProduces()) {
                for (String _produces : produces.split(",")) {
                    mediaType = MediaType.valueOf(_produces);
                    if (mediaType == null) {
                        mediaType = serviceRegistry.getProviderFactory().getConcreteMediaTypeFromMessageBodyWriters(_type, _generic, methodMetaData.getMethod().getAnnotations(), MediaType.WILDCARD_TYPE);
                        if (mediaType == null)
                            mediaType = MediaType.WILDCARD_TYPE;
                    }
                    Representation representation = createRepresentation(mediaType);
                    response.getRepresentation().add(representation);
                }
            }
        }
        return response;
    }

    private Param createParam(Resource currentResourceClass, Method method, ResteasyWadlMethodParamMetaData paramMetaData, Request request) {
        Param param = new Param();
        setType(param, paramMetaData);

        // All the method's @PathParam belong to resource
        if (paramMetaData.getParamType().equals(PATH_PARAMETER)) {
            param.setStyle(ParamStyle.TEMPLATE);
            param.setName(paramMetaData.getParamName());
            currentResourceClass.getParam().add(param);
        } else if (paramMetaData.getParamType().equals(COOKIE_PARAMETER)) {
            param.setStyle(ParamStyle.HEADER);
            request.getParam().add(param);
            param.setName("Cookie");
            param.setPath(paramMetaData.getParamName());
            method.setRequest(request);
        } else if (paramMetaData.getParamType().equals(HEADER_PARAMETER)) {
            param.setStyle(ParamStyle.HEADER);
            request.getParam().add(param);
            param.setName(paramMetaData.getParamName());
            method.setRequest(request);
        } else if (paramMetaData.getParamType().equals(MATRIX_PARAMETER)) {
            param.setStyle(ParamStyle.MATRIX);
            param.setName(paramMetaData.getParamName());
            currentResourceClass.getParam().add(param);
        } else if (paramMetaData.getParamType().equals(QUERY_PARAMETER)) {
            param.setStyle(ParamStyle.QUERY);
            request.getParam().add(param);
            param.setName(paramMetaData.getParamName());
            method.setRequest(request);
        } else if (paramMetaData.getParamType().equals(FORM_PARAMETER)) {
            param.setStyle(ParamStyle.QUERY);
            Representation formRepresentation = createFormRepresentation(request);
            param.setName(paramMetaData.getParamName());
            formRepresentation.getParam().add(param);
            method.setRequest(request);
        } else if (paramMetaData.getParamType().equals(FORM)) {
            param.setStyle(ParamStyle.QUERY);
            Representation formRepresentation = createFormRepresentation(request);
            param.setName(paramMetaData.getParamName());
            formRepresentation.getParam().add(param);
            method.setRequest(request);
        }
        return param;
    }

    private Representation createFormRepresentation(Request request) {
        Representation formRepresentation = getRepresentationByMediaType(request.getRepresentation(),
                MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        if (formRepresentation == null) {
            formRepresentation = createRepresentation(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
            request.getRepresentation().add(formRepresentation);
        }
        return formRepresentation;
    }

    private Representation createRepresentation(MediaType mediaType) {
        Representation representation;
        representation = new Representation();
        representation.setMediaType(mediaType.toString());
        return representation;
    }

    private Representation getRepresentationByMediaType(
            final List<Representation> representations, MediaType mediaType) {
        for (Representation representation : representations) {
            if (mediaType.toString().equals(representation.getMediaType())) {
                return representation;
            }
        }
        return null;
    }

    private void setType(Param param, ResteasyWadlMethodParamMetaData paramMetaData) {
        if (paramMetaData.getType().equals(int.class) || paramMetaData.getType().equals(Integer.class)) {
            param.setType(new QName("http://www.w3.org/2001/XMLSchema", "int", "xs"));
        } else if (paramMetaData.getType().equals(boolean.class) || paramMetaData.getType().equals(Boolean.class)) {
            param.setType(new QName("http://www.w3.org/2001/XMLSchema", "boolean", "xs"));
        } else if (paramMetaData.getType().equals(long.class) || paramMetaData.getType().equals(Long.class)) {
            param.setType(new QName("http://www.w3.org/2001/XMLSchema", "long", "xs"));
        } else if (paramMetaData.getType().equals(short.class) || paramMetaData.getType().equals(Short.class)) {
            param.setType(new QName("http://www.w3.org/2001/XMLSchema", "short", "xs"));
        } else if (paramMetaData.getType().equals(byte.class) || paramMetaData.getType().equals(Byte.class)) {
            param.setType(new QName("http://www.w3.org/2001/XMLSchema", "byte", "xs"));
        } else if (paramMetaData.getType().equals(float.class) || paramMetaData.getType().equals(Float.class)) {
            param.setType(new QName("http://www.w3.org/2001/XMLSchema", "float", "xs"));
        } else if (paramMetaData.getType().equals(double.class) || paramMetaData.getType().equals(Double.class)) {
            param.setType(new QName("http://www.w3.org/2001/XMLSchema", "double", "xs"));
        } else if (paramMetaData.getType().equals(Map.class) || paramMetaData.getType().equals(List.class)) {
            param.setType(new QName("http://www.w3.org/2001/XMLSchema", "complex", "xs"));
        } else {
            param.setType(new QName("http://www.w3.org/2001/XMLSchema", "string", "xs"));
        }
    }
}
