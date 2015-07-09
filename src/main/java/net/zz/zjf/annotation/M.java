package net.zz.zjf.annotation;

import java.lang.annotation.*;

/**
 * Created by ZaoSheng on 2015/5/28.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface M
{
    String value() default "";//这里默认为Model的名字小写
    String id() default "id";
}
