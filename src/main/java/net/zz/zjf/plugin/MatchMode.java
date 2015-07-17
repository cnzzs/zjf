package net.zz.zjf.plugin;

/**
 * Created by ZaoSheng on 2015/7/15.
 */
/**
 * Represents an strategy for matching strings using "like".
 *
 * @author Gavin King
 * @see (MatchMode)
 */
public enum MatchMode {

    /**
     * Match the entire string to the pattern
     */
    EXACT {
        @Override
        public String toMatchString(String pattern) {
            return pattern;
        }
    },

    /**
     * Match the start of the string to the pattern
     */
    START {
        @Override
        public String toMatchString(String pattern) {
            return pattern + '%';
        }
    },

    /**
     * Match the end of the string to the pattern
     */
    END {
        @Override
        public String toMatchString(String pattern) {
            return '%' + pattern;
        }
    },

    /**
     * Match the pattern anywhere in the string
     */
    ANYWHERE {
        @Override
        public String toMatchString(String pattern) {
            return '%' + pattern + '%';
        }
    };

    /**
     * Convert the pattern, by appending/prepending "%"
     *
     * @param pattern The pattern for convert according to the mode
     *
     * @return The converted pattern
     */
    public abstract String toMatchString(String pattern);

}