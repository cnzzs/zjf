package net.zz.zjf.plugin;

/**
 * Created by ZaoSheng on 2015/8/5.
 */
public enum IsNul {
    /* *
 * 模糊查询（from Object o where o.property  is null)
 */
    NULL {
        @Override
        public String toMatchString(String pattern) {
            return  pattern + " is null";
        }
    },
    /* *
      * 模糊查询（from Object o where o.property is not null)
      */
    NOTNULL  {
        @Override
        public String toMatchString(String pattern) {
            return  pattern + " is not null";
        }
    };
    public abstract String toMatchString(String pattern);
}