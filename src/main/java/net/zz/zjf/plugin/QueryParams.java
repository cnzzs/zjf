package net.zz.zjf.plugin;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZaoSheng on 2015/7/15.
 */
public abstract class QueryParams implements SQLParams {
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private StringBuilder sql = null;

    public Integer getPageIndex() {
        return pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Map<String, Object> getAttrs() {
        return null;
    }

    public List<Object> getParas() {
        return null;
    }

    public String getSqlString() {
        return sql.toString();
    }
    public StringBuilder getSql() {
        return sql;
    }

    public  QueryParams builderAttrs(StringBuilder sb){
        sb.append(toSQL());
        return this;
    }
    public  QueryParams builderParas(StringBuilder sb){
        sb.append(toFormatSQL());
        return this;
    }

    public QueryParams builderAttrs(){
        if (null == sql) sql = new StringBuilder();

        sql.append(toSQL());
        return this;
    }
    public QueryParams builderParas(){
        if (null == sql) sql = new StringBuilder();

        sql.append(toFormatSQL());
        return this;
    }

    public QueryParams builder(){

        return this;
    }

    public static Where Where(){
        return new Where();
    }

    public static Where Where(String key, Object value){
        return new Where(key, value);
    }

    public static Where Where(String key, Object value, String prefix){
        return new Where(key, value, prefix);
    }


    public static Order Order(String key)
    {
        return new Order(key);
    }

    public static Order Order(String key, String prefix)
    {
        return new Order(key, prefix);
    }

    public static Group Group(String key, String prefix)
    {
        return new Group(key, prefix);
    }

    public static Group Group(String key)
    {
        return new Group(key, null);
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

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();

        QueryParams.Where("name1", "张三", "m").or("name","张三", "c").builderAttrs(sb).Order("aa").ASC("bb").builderAttrs(sb).Group("name").add("age").builderAttrs(sb);

        System.out.println(sb.toString());
        System.out.println( QueryParams.Where("name1", "张三", "m").or("name", "张三", "c").builderAttrs().getSql().append(Order("aa").ASC("bb").builderAttrs().getSql().append(Group("name").add("age").builderAttrs().getSqlString()).toString()).toString());

    }
}
