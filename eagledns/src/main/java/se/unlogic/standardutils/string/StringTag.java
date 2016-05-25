package se.unlogic.standardutils.string;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface StringTag {
	String name() default "";
	//Class<? extends Stringyfier> valueFormatter() default DummyStringyfier.class;
}
