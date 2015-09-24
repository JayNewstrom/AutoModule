package com.jaynewstrom.autoModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface AutoModule {

    Class<?> addsTo() default Void.class;
}
