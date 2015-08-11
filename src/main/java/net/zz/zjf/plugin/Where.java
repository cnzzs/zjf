package net.zz.zjf.plugin;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZaoSheng on 2015/8/5.
 */
public class Where implements SQLParams{
    private String first = null;
    private Map<String, Object[]> wheres = new HashMap<String, Object[]>();
    private Map<String, Object> attrs = new HashMap<String, Object>();
    private List<Object> paras = new ArrayList<Object>();

    public Where() {

    }

    public Where(String propertyName, Object value, AndOr andor, Restriction restriction) {
        first = propertyName;
       add(propertyName, value, andor, restriction);
    }

    public Where(String propertyName, Object value, AndOr andor) {
        this(propertyName, value, andor, Restriction.EQ);
    }

    public Where(String propertyName, Object value, Restriction restriction) {
        this(propertyName, value, AndOr.NUL, restriction);
    }

    public Where(String propertyName, Object value) {
        this(propertyName, value, Restriction.EQ);
    }

    public Map<String, Object[]> getWheres() {
        return wheres;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public List<Object> getParas() {
        return paras;
    }

    public Where and(String propertyName, Object value, Restriction restriction) {
        add(propertyName, value, AndOr.AND, restriction);
        return this;
    }

    public Where and(String propertyName, Object value) {
        return and(propertyName, value, Restriction.EQ);
    }

    public Where or(String propertyName, Object value, Restriction restriction) {
        add(propertyName, value, AndOr.OR, restriction);
        return this;
    }

    public Where or(String propertyName, Object value) {
        return or(propertyName, value, Restriction.EQ);
    }

    protected void add(String key, Object value, AndOr andor, Restriction restriction) {
        if (null == value || "".equals(value)) {
            if (key.equals(first))
            {
                first = null;
            }
            wheres.remove(key);
        } else {
            wheres.put(key, new Object[]{value, andor, restriction});
        }

    }



    public String toSQL() {
        if (wheres.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        if (null != first)  setSql(first, wheres.get(first), sb );

        for (String key : wheres.keySet()) {
            if (key.equals(first))  continue;

            Object[] objects = wheres.get(key);
            setSql(key, objects, sb);

        }
        return sb.toString();
    }

    private void setSql(String key, Object[] objects, StringBuilder sb)
    {
        AndOr andOr = (AndOr) objects[1];
        Restriction restriction = (Restriction)objects[2];
        switch (restriction) {
            case LIKE:
            case LLIKE:
            case RLIKE:
                sb.append(andOr.toMatchString(key, "like :" + key));
                attrs.put(key, restriction.toMatchString(objects[0].toString()));
                break;
            case NULL:
            case NOTNULL:
                sb.append(andOr.toMatchString("", restriction.toMatchString(key)));
                break;
            case BETWEEN:
                sb.append(andOr.toMatchString(key, restriction.toMatchString(key)));
                Object[] value = (Object[]) objects[0];
                attrs.put(String.format("%s1", key),  value[0]);
                attrs.put(String.format("%s2", key),  value[1]);
                break;
            default:
                sb.append(andOr.toMatchString(key, restriction.toMatchString(key)));
                attrs.put(key, objects[0]);
        }
    }

    @Override
    public String toFormatSQL() {
        return  toFormatSQL(toSQL(), attrs, paras);
    }

    /**
     * @param whereSQL
     * @param attrs
     * @param values
     * @return
     */
    public static String toFormatSQL(String whereSQL, Map<String, Object> attrs, List<Object> values) {
        Matcher matcher = Pattern.compile(":(\\w+)").matcher(whereSQL);
        String rexp = null;
        while (matcher.find()) {
            String group = matcher.group(1);
            Object ov = attrs.get(group);
            if (ov instanceof List) {
                StringBuilder sb = new StringBuilder();
                List vs = (List) ov;
                for (Object v : vs) {
                    sb.append("?,");
                    values.add(v);
                }
                sb.deleteCharAt(sb.length() - 1);
                rexp = sb.toString();

            } else {
                values.add(ov);
                rexp = "?";
            }
            whereSQL = whereSQL.replace(String.format(":%s", group), rexp);
        }
        return whereSQL;
    }

    @Override
    public String toString() {
        return toFormatSQL();
    }

    public static void main(String[] args) {
        Calendar c = new GregorianCalendar();

        Where where = new Where("name", "张三");
        where.or("class", 2);
        where.and("sex", true);
        where.and("age", new Integer[]{1,10}, Restriction.BETWEEN);
        List<Object> ids = new ArrayList<Object>();
        ids.add(4);
        ids.add(3);
        where.and("id", ids, Restriction.IN);
        System.out.println(where.toString());

        for (Object value :where.getParas())
        {
            System.out.print(String.format("%s ", value));
        }



    }
}
