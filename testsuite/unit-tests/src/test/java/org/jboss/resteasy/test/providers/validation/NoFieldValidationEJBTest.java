package org.jboss.resteasy.test.providers.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.Min;

import org.jboss.resteasy.plugins.validation.GeneralValidatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Internal validation methods
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test constraint finding methods in GeneralValidatorImpl
 * @tpSince RESTEasy 4.6
 */
public class NoFieldValidationEJBTest {

    private static Method hasNoClassOrFieldOrPropertyConstraints;
    private static Method hasClassConstraint;
    private static Method hasFieldConstraint;
    private static Method hasPropertyConstraint;
    private static Method isConstraintAnnotation;
    private static Method isGetter;
    private static Method classHasAnnotations;

    @BeforeAll
    public static void beforeClass() {
        try {
            hasNoClassOrFieldOrPropertyConstraints = GeneralValidatorImpl.class
                    .getDeclaredMethod("hasNoClassOrFieldOrPropertyConstraints", Class.class);
            hasClassConstraint = GeneralValidatorImpl.class.getDeclaredMethod("hasClassConstraint", Class.class);
            hasFieldConstraint = GeneralValidatorImpl.class.getDeclaredMethod("hasFieldConstraint", Class.class);
            hasPropertyConstraint = GeneralValidatorImpl.class.getDeclaredMethod("hasPropertyConstraint", Class.class);
            isConstraintAnnotation = GeneralValidatorImpl.class.getDeclaredMethod("isConstraintAnnotation", Class.class);
            isGetter = GeneralValidatorImpl.class.getDeclaredMethod("isGetter", Method.class);
            classHasAnnotations = GeneralValidatorImpl.class.getDeclaredMethod("classHasAnnotations", Class.class,
                    String[].class);

            hasNoClassOrFieldOrPropertyConstraints.setAccessible(true);
            hasClassConstraint.setAccessible(true);
            hasFieldConstraint.setAccessible(true);
            hasPropertyConstraint.setAccessible(true);
            isConstraintAnnotation.setAccessible(true);
            isGetter.setAccessible(true);
            classHasAnnotations.setAccessible(true);
        } catch (Exception e) {
            //
        }
    }

    @Constraint(validatedBy = ClassConstraintValidator.class)
    @Target({ TYPE })
    @Retention(RUNTIME)
    public @interface ClassConstraint {
        int value();
    }

    public class ClassConstraintValidator implements ConstraintValidator<ClassConstraint, Object> {
        int size;

        public void initialize(ClassConstraint constraintAnnotation) {
            size = constraintAnnotation.value();
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            return true;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    ///// Constraint annotations
    //////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void testConstraintAnnotations() {
    }

    //////////////////////////////////////////////////////////////////////////////////////
    ///// Class constraints
    //////////////////////////////////////////////////////////////////////////////////////

    @ClassConstraint(3)
    public interface Ic_w {
    }

    public interface Ic_wo {
    }

    @ClassConstraint(3)
    public static class Cc_w {
    }

    public static class Cc_wo {
    }

    @ClassConstraint(3)
    public static class CIc_w_wo implements Ic_wo {
    }

    public static class CIc_wo_w implements Ic_w {
    }

    public static class CIc_wo_wwo implements Ic_w, Ic_wo {
    }

    public static class CIc_wo_wo implements Ic_wo {
    }

    @ClassConstraint(3)
    public static class SCc_w_wo extends Cc_wo {
    }

    public static class SCc_wo_w extends Cc_w {
    }

    public static class SCc_wo_wo extends Cc_wo {
    }

    public static class SCc_wo_wo_w extends Cc_wo implements Ic_w {
    }

    /**
     * @tpTestDetails Find class constraints
     * @tpSince RESTEasy 4.5
     */
    @Test
    public void testClassConstraints() throws Exception {
        Assertions.assertTrue(isInited());
        Assertions.assertTrue(Boolean.TRUE.equals(hasClassConstraint.invoke(null, Ic_w.class)));
        Assertions.assertTrue(Boolean.FALSE.equals(hasClassConstraint.invoke(null, Ic_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasClassConstraint.invoke(null, Cc_w.class)));
        Assertions.assertTrue(Boolean.FALSE.equals(hasClassConstraint.invoke(null, Cc_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasClassConstraint.invoke(null, CIc_w_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasClassConstraint.invoke(null, CIc_wo_wwo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasClassConstraint.invoke(null, CIc_wo_w.class)));
        Assertions.assertTrue(Boolean.FALSE.equals(hasClassConstraint.invoke(null, CIc_wo_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasClassConstraint.invoke(null, SCc_w_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasClassConstraint.invoke(null, SCc_wo_w.class)));
        Assertions.assertTrue(Boolean.FALSE.equals(hasClassConstraint.invoke(null, SCc_wo_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasClassConstraint.invoke(null, SCc_wo_wo_w.class)));
    }

    //////////////////////////////////////////////////////////////////////////////////////
    ///// Field constraints
    //////////////////////////////////////////////////////////////////////////////////////

    public static class Cf_w {
        @Min(3)
        int n;
    }

    public static class Cf_wo {
    }

    public static class SCf_wo_w extends Cf_w {
    }

    public static class SCf_w_wo extends Cf_wo {
        @Min(3)
        int n;
    }

    public static class SCf_wo_wo extends Cf_wo {
    }

    /**
     * @tpTestDetails Find field constraints
     * @tpSince RESTEasy 4.5
     */
    @Test
    public void testFieldConstraints() throws Exception {
        Assertions.assertTrue(isInited());
        Assertions.assertTrue(Boolean.TRUE.equals(hasFieldConstraint.invoke(null, Cf_w.class)));
        Assertions.assertTrue(Boolean.FALSE.equals(hasFieldConstraint.invoke(null, Cf_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasFieldConstraint.invoke(null, SCf_wo_w.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasFieldConstraint.invoke(null, SCf_w_wo.class)));
        Assertions.assertTrue(Boolean.FALSE.equals(hasFieldConstraint.invoke(null, SCf_wo_wo.class)));
    }

    //////////////////////////////////////////////////////////////////////////////////////
    ///// Property constraints
    //////////////////////////////////////////////////////////////////////////////////////
    public interface Ip_w {
        @Min(3)
        int getN();
    }

    public interface Ip_wo {
    }

    public static class Cp_w {
        @Min(3)
        public int getN() {
            return 4;
        }
    }

    public static class Cp_wo {
    }

    public static class CIp_w_wo implements Ip_wo {
        @Min(3)
        public int getN() {
            return 4;
        }
    }

    public abstract static class CIp_wo_w implements Ip_w {
    }

    public abstract static class CIp_wo_wwo implements Ip_w, Ip_wo {
    }

    public static class CIp_wo_wo implements Ip_wo {
    }

    public static class SCp_w_wo extends Cp_wo {
        @Min(3)
        public int getN() {
            return 4;
        }
    }

    public static class SCp_wo_w extends Cp_w {
    }

    public static class SCp_wo_wo extends Cp_wo {
    }

    public abstract static class SCp_wo_wo_w extends Cp_wo implements Ip_w {
    }

    /**
     * @tpTestDetails Find property constraints
     * @tpSince RESTEasy 4.5
     */
    @Test
    public void testPropertyConstraints() throws Exception {
        Assertions.assertTrue(isInited());
        Assertions.assertTrue(Boolean.TRUE.equals(hasPropertyConstraint.invoke(null, Ip_w.class)));
        Assertions.assertTrue(Boolean.FALSE.equals(hasPropertyConstraint.invoke(null, Ip_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasPropertyConstraint.invoke(null, Cp_w.class)));
        Assertions.assertTrue(Boolean.FALSE.equals(hasPropertyConstraint.invoke(null, Cp_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasPropertyConstraint.invoke(null, CIp_w_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasPropertyConstraint.invoke(null, CIp_wo_wwo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasPropertyConstraint.invoke(null, CIp_wo_w.class)));
        Assertions.assertTrue(Boolean.FALSE.equals(hasPropertyConstraint.invoke(null, CIp_wo_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasPropertyConstraint.invoke(null, SCp_w_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasPropertyConstraint.invoke(null, SCp_wo_w.class)));
        Assertions.assertTrue(Boolean.FALSE.equals(hasPropertyConstraint.invoke(null, SCc_wo_wo.class)));
        Assertions.assertTrue(Boolean.TRUE.equals(hasPropertyConstraint.invoke(null, SCp_wo_wo_w.class)));
    }

    //////////////////////////////////////////////////////////////////////////////////////
    ///// Testing GeneralValidatorImpl.classHasAnnotations()
    //////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void test() throws Exception {
        Assertions.assertEquals(Boolean.TRUE,
                classHasAnnotations.invoke(null, SCc_wo_wo_w.class, new String[] { ClassConstraint.class.getName() }));
        Assertions.assertEquals(Boolean.FALSE,
                classHasAnnotations.invoke(null, SCc_wo_wo_w.class, new String[] { Min.class.getName() }));
    }

    //////////////////////////////////////////////////////////////////////////////////////
    ///// Private methods
    //////////////////////////////////////////////////////////////////////////////////////
    private static boolean isInited() {
        return hasNoClassOrFieldOrPropertyConstraints != null
                && hasClassConstraint != null
                && hasFieldConstraint != null
                && hasPropertyConstraint != null
                && isConstraintAnnotation != null
                && isGetter != null;
    }
}
