package org.jboss.resteasy.plugins.server.servlet;

/**
 * constant names of resteasy configuration variables within a servlet
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResteasyContextParameters
{
   String RESTEASY_PROVIDERS = "resteasy.providers";

   /**
    * this is deprecated
    */
   String RESTEASY_RESOURCE_METHOD_INTERCEPTORS = "resteasy.resource.method.interceptors";

   String RESTEASY_USE_BUILTIN_PROVIDERS = "resteasy.use.builtin.providers";
   String RESTEASY_SCAN_PROVIDERS = "resteasy.scan.providers";
   String RESTEASY_SCAN = "resteasy.scan";
   String RESTEASY_SCAN_RESOURCES = "resteasy.scan.resources";
   String RESTEASY_JNDI_RESOURCES = "resteasy.jndi.resources";
   String RESTEASY_RESOURCES = "resteasy.resources";
   String RESTEASY_MEDIA_TYPE_MAPPINGS = "resteasy.media.type.mappings";
   String RESTEASY_LANGUAGE_MAPPINGS = "resteasy.language.mappings";
   String RESTEASY_MEDIA_TYPE_PARAM_MAPPING = "resteasy.media.type.param.mapping";
   String RESTEASY_ROLE_BASED_SECURITY = "resteasy.role.based.security";
   String RESTEASY_INTERCEPTOR_BEFORE_PRECEDENCE = "resteasy.interceptor.before.precedence";
   String RESTEASY_INTERCEPTOR_AFTER_PRECEDENCE = "resteasy.interceptor.after.precedence";
   String RESTEASY_APPEND_INTERCEPTOR_PRECEDENCE = "resteasy.append.interceptor.precedence";
   String RESTEASY_SCANNED_BY_DEPLOYER = "resteasy.scanned.by.deployer";
   String RESTEASY_JNDI_COMPONENT_RESOURCES = "resteasy.jndi.component.resources";
   String RESTEASY_UNWRAPPED_EXCEPTIONS = "resteasy.unwrapped.exceptions";
   String RESTEASY_EXPAND_ENTITY_REFERENCES = "resteasy.document.expand.entity.references";
   String RESTEASY_SECURE_PROCESSING_FEATURE = "resteasy.document.secure.processing.feature";
   String RESTEASY_DISABLE_DTDS = "resteasy.document.secure.disableDTDs";
   String RESTEASY_GZIP_MAX_INPUT = "resteasy.gzip.max.input";
   String RESTEASY_SECURE_RANDOM_MAX_USE = "resteasy.secure.random.max.use";
   String RESTEASY_ADD_CHARSET = "resteasy.add.charset";
   
   // these scanned variables are provided by a deployer
   String RESTEASY_SCANNED_RESOURCES = "resteasy.scanned.resources";
   String RESTEASY_SCANNED_PROVIDERS = "resteasy.scanned.providers";
   String RESTEASY_SCANNED_JNDI_RESOURCES = "resteasy.scanned.jndi.resources";
   String RESTEASY_CONTEXT_OBJECTS = "resteasy.context.objects";
   String RESTEASY_USE_CONTAINER_FORM_PARAMS = "resteasy.use.container.form.params";
   String RESTEASY_DEPLOYMENTS = "resteasy.deployments";
   String RESTEASY_SERVLET_MAPPING_PREFIX = "resteasy.servlet.mapping.prefix";
   String RESTEASY_WIDER_REQUEST_MATCHING = "resteasy.wider.request.matching";
   String JAX_RS_2_0_REQUEST_MATCHING = "jaxrs.2.0.request.matching";
   
   String RESTEASY_PREFER_JACKSON_OVER_JSONB = "resteasy.preferJacksonOverJsonB";
}
