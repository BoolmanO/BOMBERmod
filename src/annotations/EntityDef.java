package annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface EntityDef{

    Class[] value();

    boolean isFinal() default true;

    boolean pooled() default false;

    boolean serialize() default true;

    boolean genio() default true;

    boolean legacy() default false;
}