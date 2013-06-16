package org.jboss.resteasy.test.validation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.plugins.providers.validation.ViolationsContainer;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for {@link ViolationsContainer}.
 *
 * @author Gunnar Morling
 */
public class ViolationsContainerTest {

   private GeneralValidator validator;

   @Before
   public void setupValidator()
   {
      ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
      ContextResolver<GeneralValidator> resolver = providerFactory.getContextResolver(GeneralValidator.class, MediaType.WILDCARD_TYPE);
      validator = providerFactory.getContextResolver(GeneralValidator.class, MediaType.WILDCARD_TYPE).getContext(null);
   }

   @Test
   @Ignore
   public void shouldReturnFieldViolation()
   {
      Set<ResteasyConstraintViolation> violations = validator.validate(new Person(null, "Closed"));
      ViolationsContainer<Person> violationsContainer = new ViolationsContainer<Person>(violations);

      assertEquals(1, violationsContainer.getFieldViolations().size());
      assertTrue(violationsContainer.getPropertyViolations().isEmpty());
      assertTrue(violationsContainer.getClassViolations().isEmpty());
      assertTrue(violationsContainer.getParameterViolations().isEmpty());
      assertTrue(violationsContainer.getReturnValueViolations().isEmpty());
   }

   @Test
   @Ignore
   public void shouldReturnPropertyViolation()
   {
      Set<ResteasyConstraintViolation> violations = validator.validate(new Person("Glen", null));
      ViolationsContainer<Person> violationsContainer = new ViolationsContainer<Person>(violations);

      assertTrue(violationsContainer.getFieldViolations().isEmpty());
      assertEquals(1, violationsContainer.getPropertyViolations().size());
      assertTrue(violationsContainer.getClassViolations().isEmpty());
      assertTrue(violationsContainer.getParameterViolations().isEmpty());
      assertTrue(violationsContainer.getReturnValueViolations().isEmpty());
   }

   @Test
   @Ignore
   public void shouldReturnClassViolation()
   {
      Set<ResteasyConstraintViolation> violations = validator.validate(new AnotherPerson());
      ViolationsContainer<AnotherPerson> violationsContainer = new ViolationsContainer<AnotherPerson>(violations);

      assertTrue(violationsContainer.getFieldViolations().isEmpty());
      assertTrue(violationsContainer.getPropertyViolations().isEmpty());
      assertEquals(1, violationsContainer.getClassViolations().size());
      assertTrue(violationsContainer.getParameterViolations().isEmpty());
      assertTrue(violationsContainer.getReturnValueViolations().isEmpty());
   }

   @Test
   @Ignore
   public void shouldReturnParameterViolation() throws Exception
   {
      Person person = new Person(null, null);
      Method method = Person.class.getMethod("setLastName", String.class);
      Object[] parameterValues = new Object[]{ null };

      Set<ResteasyConstraintViolation> violations = validator.validateAllParameters(person, method, parameterValues);
      ViolationsContainer<Person> violationsContainer = new ViolationsContainer<Person>(violations);

      assertTrue(violationsContainer.getFieldViolations().isEmpty());
      assertTrue(violationsContainer.getPropertyViolations().isEmpty());
      assertTrue(violationsContainer.getClassViolations().isEmpty());
      assertEquals(1, violationsContainer.getParameterViolations().size());
      assertTrue(violationsContainer.getReturnValueViolations().isEmpty());
   }

   @Test
   @Ignore
   public void shouldReturnParameterViolationForCrossParameterConstraint() throws Exception
   {
      Person person = new Person(null, null);
      Method method = Person.class.getMethod("setNames", String.class, String.class);
      Object[] parameterValues = new Object[]{ null };

      Set<ResteasyConstraintViolation> violations = validator.validateAllParameters(person, method, parameterValues);
      ViolationsContainer<Person> violationsContainer = new ViolationsContainer<Person>(violations);

      assertTrue(violationsContainer.getFieldViolations().isEmpty());
      assertTrue(violationsContainer.getPropertyViolations().isEmpty());
      assertTrue(violationsContainer.getClassViolations().isEmpty());
      assertEquals(1, violationsContainer.getParameterViolations().size());
      assertTrue(violationsContainer.getReturnValueViolations().isEmpty());
   }

   @Test
   @Ignore
   public void shouldReturnReturnValueViolation() throws Exception
   {
      Person person = new Person(null, null);
      Method method = Person.class.getMethod("getLastName");
      Object returnValue = null;

      Set<ResteasyConstraintViolation> violations = validator.validateReturnValue(person, method, returnValue );
      ViolationsContainer<Person> violationsContainer = new ViolationsContainer<Person>(violations);

      assertTrue(violationsContainer.getFieldViolations().isEmpty());
      assertTrue(violationsContainer.getPropertyViolations().isEmpty());
      assertTrue(violationsContainer.getClassViolations().isEmpty());
      assertTrue(violationsContainer.getParameterViolations().isEmpty());
      assertEquals(1, violationsContainer.getReturnValueViolations().size());
   }

   @SuppressWarnings("unused")
   private static class Person
   {
      @NotNull
      private String firstName = null;
      private String lastName = null;

      public Person(String firstName, String lastName)
      {
         this.firstName = firstName;
         this.lastName = lastName;
      }

      @NotNull
      public String getLastName()
      {
         return lastName;
      }

	public void setLastName(@NotNull String lastName)
      {
         this.lastName = lastName;
      }

      @ValidParameters
      public void setNames(String firstName, String lastName)
      {
         this.firstName = firstName;
         this.lastName = lastName;
      }
   }

   @ValidPerson
   private static class AnotherPerson
   {
   }

   @Target(TYPE)
   @Retention(RUNTIME)
   @Documented
   @Constraint(validatedBy = ValidPerson.Validator.class)
   public @interface ValidPerson
   {

      String message() default "{org.jboss.resteasy.test.validation.ViolationsContainerTest.ValidPerson.Size.message}";
      Class<?>[] groups() default { };
      Class<? extends Payload>[] payload() default { };

      public static class Validator implements ConstraintValidator<ValidPerson, AnotherPerson>
      {

         @Override
         public void initialize(ValidPerson constraintAnnotation)
         {
         }

         @Override
         public boolean isValid(AnotherPerson value, ConstraintValidatorContext context)
         {
            return false;
         }
      }
   }

   @Target(METHOD)
   @Retention(RUNTIME)
   @Documented
   @Constraint(validatedBy = ValidParameters.Validator.class)
   public @interface ValidParameters
   {

      String message() default "{org.jboss.resteasy.test.validation.ViolationsContainerTest.ValidParameters.Size.message}";
      Class<?>[] groups() default { };
      Class<? extends Payload>[] payload() default { };

      @SupportedValidationTarget(ValidationTarget.PARAMETERS)
      public static class Validator implements ConstraintValidator<ValidParameters, Object[]>
      {

         @Override
         public void initialize(ValidParameters constraintAnnotation)
         {
         }

         @Override
         public boolean isValid(Object[] parameters, ConstraintValidatorContext context)
         {
            return false;
         }
      }
   }
}

