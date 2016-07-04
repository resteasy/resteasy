/**
 * This package (along with @see org.jboss.resteasy.test.resteasy1137) tests versioning compatibility
 * of the class org.jboss.resteasy.api.validation.ResteasyViolationException in module resteasy-validator-provider11.
 * 
 * As of release 3.0.19.Final-SNAPSHOT, ResteasyViolationException was changed from a subclass of javax.validation.ValidationException
 * to a subclass of javax.validation.ConstraintViolationException, which is a subclass of javax.validation.ValidationException.
 * 
 * The jar validation-versioning.jar in src/test/resources/1137 contains the class 
 * org.jboss.resteasy.test.validation.versioning.CustomExceptionMapper, with the method
 * 
 * <p>
 * <pre>
 * {@code
 *   public Response toResponse(ResteasyViolationException rve);
 * }
 * </pre>
 * <p>
 * 
 * which was compiled with the previous version of ResteasyViolationException. The test in these two packages shows
 * that the two versions of ResteasyViolationException are binary compatible.
 * 
*/
package org.jboss.resteasy.resteasy1137;
