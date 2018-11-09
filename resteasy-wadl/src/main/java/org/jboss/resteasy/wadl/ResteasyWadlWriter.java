package org.jboss.resteasy.wadl;

import org.jboss.resteasy.wadl.i18n.LogMessages;
import org.jboss.resteasy.wadl.i18n.Messages;
import org.jboss.resteasy.wadl.jaxb.Application;
import org.jboss.resteasy.wadl.jaxb.Doc;
import org.jboss.resteasy.wadl.jaxb.Grammars;
import org.jboss.resteasy.wadl.jaxb.Include;
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
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.jboss.resteasy.wadl.ResteasyWadlMethodParamMetaData.MethodParamType.*;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class ResteasyWadlWriter {
   private ResteasyWadlGrammar wadlGrammar = null;

   private Application wadlApp = null;

    public void setWadlGrammar(ResteasyWadlGrammar wadlGrammar) {
      this.wadlGrammar = wadlGrammar;
   }

    public ResteasyWadlGrammar getWadlGrammar() {
        return wadlGrammar;
    }

    // this is only used by deprecated @org.jboss.resteasy.wadl.ResteasyWadlServlet
    @Deprecated
    public byte[] getBytes(String base, Map<String, ResteasyWadlServiceRegistry> serviceRegistries) throws JAXBException {
      StringWriter stringWriter = getStringWriter(base, serviceRegistries);
      return stringWriter.toString().getBytes();
   }

   public StringWriter getStringWriter(String base, Map<String, ResteasyWadlServiceRegistry> serviceRegistries) throws JAXBException {

      Application app = createApplication(base, serviceRegistries);

      JAXBContext context = JAXBContext.newInstance(Application.class);
      Marshaller marshaller = context.createMarshaller();
      StringWriter stringWriter = new StringWriter();
      PrintWriter writer = new PrintWriter(stringWriter);
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(app, writer);
      return stringWriter;
   }

    private Application createApplication(String base, Map<String, ResteasyWadlServiceRegistry> serviceRegistries) {
            ObjectFactory factory = new ObjectFactory();
            Application app = factory.createApplication();
            for (Map.Entry<String, ResteasyWadlServiceRegistry> entry : serviceRegistries.entrySet()) {
                String uri = base;
                if (entry.getKey() != null) uri += entry.getKey();
                Resources resources = new Resources();
                resources.setBase(uri);
                app.getResources().add(resources);
                processWadl(entry.getValue(), resources);

            if (wadlGrammar != null && wadlGrammar.hasGrammars()) {
                app.setGrammars(wadlGrammar.getGrammars());
            }

            wadlApp = app;
        }

        return wadlApp;
    }

    private void processWadl(ResteasyWadlServiceRegistry serviceRegistry, Resources root) {

      for (Map.Entry<String, ResteasyWadlResourceMetaData> resourceMetaDataEntry : serviceRegistry.getResources().entrySet()) {
         LogMessages.LOGGER.debug(Messages.MESSAGES.path(resourceMetaDataEntry.getKey()));
         Resource resourceClass = new Resource();

         resourceClass.setPath(resourceMetaDataEntry.getKey());
         root.getResource().add(resourceClass);

         for (ResteasyWadlMethodMetaData methodMetaData : resourceMetaDataEntry.getValue().getMethodsMetaData()) {
            Method method = new Method();

            if (wadlGrammar != null) {
                 // WADL schema generation.
                 wadlGrammar.collectClassesForSchemaGeneration(methodMetaData);
             }

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
         createParam(currentResourceClass, method, paramMetaData, request);
      }
   }

   private Response createResponse(ResteasyWadlServiceRegistry serviceRegistry, ResteasyWadlMethodMetaData methodMetaData) {
      Response response = new Response();

      Class _type = methodMetaData.getMethod().getReturnType();
      Type _generic = methodMetaData.getMethod().getGenericReturnType();

      MediaType mediaType;

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

   public static class ResteasyWadlGrammar {

       // included grammars or generated grammars, or both.
       private Grammars grammars = null;
       private Map<String, byte[]> externalSchemas = new ConcurrentHashMap<>();
       private Map<String, byte[]> generatedSchemas = new ConcurrentHashMap<>();

       // the JAXB annotated classes need to be included to generate schemas
       private Set<Class> schemaClasses = Collections.synchronizedSet(new HashSet<>());

       private ClassLoader loader = Thread.currentThread().getContextClassLoader();

       private boolean generateSchema = false;

       public boolean hasGrammars() {
           return grammars != null;
       }

       protected Grammars getGrammars() {
           return grammars;
       }

       // include grammars provided by users.
       public void includeGrammars(String grammarFileName) {
           externalSchemas.clear();

           try (final InputStream is = loader.getResourceAsStream(grammarFileName)) {
               if (is != null) {
                   Grammars grammars = unmarshall(is);
                   List<Include> includes = grammars.getInclude();
                   for (Include include : includes) {
                       addExternalSchema(include.getHref());
                   }
                   addGrammars(grammars);
               } else {
                  LogMessages.LOGGER.error(Messages.MESSAGES.cantProcessWadl());
               }
           } catch (Exception e) {
              LogMessages.LOGGER.error(Messages.MESSAGES.cantProcessWadl());
           }
       }

       public void enableSchemaGeneration() {
           generateSchema = true;
       }

       public boolean schemaGenerationEnabled() {
           return generateSchema;
       }

       protected void addGrammars(Grammars grammars) {
           if (this.grammars == null) {
               this.grammars = grammars;
           } else {
               if (!grammars.getAny().isEmpty()) {
                   this.grammars.getAny().addAll(grammars.getAny());
               }
               if (!grammars.getDoc().isEmpty()) {
                   this.grammars.getDoc().addAll(grammars.getDoc());
               }
               if (!grammars.getInclude().isEmpty()) {
                   this.grammars.getInclude().addAll(grammars.getInclude());
               }
           }
       }

       private void collectClassesForSchemaGeneration(ResteasyWadlMethodMetaData methodMetaData) {
           if (!schemaGenerationEnabled())
               return;
           // support runtime rescan.
           schemaClasses.clear();
           generatedSchemas.clear();

           _addClass(methodMetaData.getMethod().getReturnType());

           for (ResteasyWadlMethodParamMetaData paramMetaData : methodMetaData.getParameters()) {
               _addClass(paramMetaData.getType());
           }

           processClassesForSchema();
       }

       private void _addClass(Class clazz) {
           if (clazz.getAnnotation(XmlRootElement.class) != null) {
               schemaClasses.add(clazz);
           }
       }

       private void processClassesForSchema() {
           try {
               final JAXBContext context = JAXBContext.newInstance(schemaClasses.toArray(new Class[schemaClasses.size()]));

               final List<StreamResult> results = new ArrayList<>();

               context.generateSchema(new SchemaOutputResolver() {
                   int counter = 0;

                   @Override
                   public Result createOutput(final String namespaceUri, final String suggestedFileName) {
                       final StreamResult result = new StreamResult(new CharArrayWriter());
                       String systemId = "xsd" + (counter++) + ".xsd";
                       result.setSystemId(systemId);
                       results.add(result);
                       return result;
                   }
               });

               if (grammars != null) {
                   Iterator<Include> iter = grammars.getInclude().iterator();
                   while (iter.hasNext()) {
                       for (Doc doc : iter.next().getDoc()) {
                           if ("Generated".equals(doc.getTitle())) ;
                           iter.remove();
                       }
                   }
               }

               // in case grammars is null
               addGrammars(new Grammars());

               for (final StreamResult result : results) {
                   final CharArrayWriter writer = (CharArrayWriter) result.getWriter();
                   final byte[] contents = writer.toString().getBytes("UTF8");
                   generatedSchemas.put(
                           result.getSystemId(),
                           contents);

                   Include inc = new Include();
                   inc.setHref(result.getSystemId());
                   Doc doc = new Doc();
                   doc.setTitle("Generated");
                   doc.setLang("en");
                   inc.getDoc().add(doc);

                   this.grammars.getInclude().add(inc);
               }
           } catch (JAXBException e) {
              LogMessages.LOGGER.error(Messages.MESSAGES.cantProcessWadl());
           } catch (IOException e) {
              LogMessages.LOGGER.error(Messages.MESSAGES.cantProcessWadl());
           }
       }

       private void addExternalSchema(String href) {
           try (InputStream is = loader.getResourceAsStream(href)) {
               if (is != null) {
                   externalSchemas.put(href, toBytes(is));
               } else {
                  LogMessages.LOGGER.error(Messages.MESSAGES.cantProcessWadl());
               }
           } catch (Exception e) {
              LogMessages.LOGGER.error(Messages.MESSAGES.cantProcessWadl());
           }
       }

       private static byte[] toBytes(InputStream is) throws Exception {
           StringBuilder textBuilder = new StringBuilder();
           try (Reader reader = new BufferedReader(new InputStreamReader
                   (is, StandardCharsets.UTF_8.name()))) {
               int c = 0;
               while ((c = reader.read()) != -1) {
                   textBuilder.append((char) c);
               }
           }
           return textBuilder.toString().getBytes(StandardCharsets.UTF_8);
       }

       private static Grammars unmarshall(InputStream is) throws JAXBException {
           JAXBContext ctx = JAXBContext.newInstance(Grammars.class);
           Unmarshaller unmarshaller = ctx.createUnmarshaller();
           Grammars out = (Grammars) unmarshaller.unmarshal(is);
           return out;
       }

       public byte[] getSchemaOfUrl(String path) {
           byte[] result;
           result = externalSchemas.get(path);
           if (result == null) {
               result = generatedSchemas.get(path);
           }
           return result;
       }
   }
}
