package net.zz.zjf.annotation;

import java.lang.annotation.*;

/**
 * Created by ZaoSheng on 2015/5/24.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface C {
    String value();
}
