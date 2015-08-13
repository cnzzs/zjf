package net.zz.zjf.plugin;

/**
 * Created by ZaoSheng on 2015/7/30.
 */
public enum Restrictions {

    /**
     * 等于查询（from Object o where o.property = ?）
     */
    EQ {
        public String toMatchString(String pattern) {
            return "= :" + pattern;
        }
    },

    /**
     * 非等于查询（from Object o where o.property <> ?）
     */
    NE {
        public String toMatchString(String pattern) {
            return "<> :" + pattern;
        }
    },

    /**
     * 大于等于查询（from Object o where o.property >= ?）
     */
    GE {
        public String toMatchString(String pattern) {
            return ">= :" + pattern;
        }

    },

    /**
     * 大于查询（from Object o where o.property > ?）
     */
    GT {
        @Override
        public String toMatchString(String pattern) {
            return "> :" + pattern;
        }
    },

    /**
     * 小于等于查询（from Object o where o.property <= ?）
     */
    LE {
        @Override
        public String toMatchString(String pattern) {
            return "<= :" + pattern;
        }
    },

    /**
     * 小于查询（from Object o where o.property < ?）
     */
    LT {
        @Override
        public String toMatchString(String pattern) {
            return "< :" + pattern;
        }
    },
    /**
     * 两个值之间查询（from Object o where o.property between ? and ?）
     */
    BETWEEN {
        @Override
        public String toMatchString(String pattern) {
            return String.format("%s between :%s1 and :%s2", pattern, pattern, pattern);
        }
    },

    /**
     * 包含查询（from Object o where o.property in(?,?,?)）
     */
    IN {
        @Override
        public String toMatchString(String pattern) {
            return String.format("in (:%s)" , pattern);
        }
    },

    /**
     * 非包含查询（from Object o where o.property not in(?,?,?)）
     */
    NIN {
        @Override
        public String toMatchString(String pattern) {
            return String.format("not in ( :%s )" , pattern);
        }
    },

    /* *
     * 左模糊查询（from Object o where o.property like %?）
     */
    LLIKE {
        @Override
        public String toMatchString(String pattern) {
            return "%" + pattern;
        }
    },

   /* *
     * 右模糊查询（from Object o where o.property like ?%)
     */
    RLIKE {
        @Override
        public String toMatchString(String pattern) {
            return pattern + '%';
        }
    },

   /* *
     * 模糊查询（from Object o where o.property like %?%)
     */
    LIKE {
        @Override
        public String toMatchString(String pattern) {
            return '%' + pattern + '%';
        }
    },
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
