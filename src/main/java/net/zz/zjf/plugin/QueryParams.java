package net.zz.zjf.plugin;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZaoSheng on 2015/7/15.
 */
public class QueryParams {
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private List<String> groups = new ArrayList<String>();
    private Map<String, Object> likes = new HashMap<String, Object>();
    private Map<String, Object> orders = new HashMap<String, Object>();
    private Map<String, Collection<Object>> ins = new HashMap<String, Collection<Object>>();
    protected String sql = " where 1=1 ";
    private Map<String, Object> attrs = new HashMap<String, Object>();
    private List<Object> paras = new ArrayList<Object>();

//    private Map<String, List<Object>> mutValues = new HashMap<String, List<Object>>();

    public void addOrder(String key, OrderAD ad) {
        orders.put(key, ad.name());
    }

    public void addOrder(String key) {
        orders.put(key, OrderAD.DESC.name());
    }

    public enum OrderAD {
        DESC, ASC
    }

    public Map<String, Object> getSqlLikes() {
        return likes;
    }

    public QueryParams addGroup(String group) {
        this.groups.add(group);

        return this;
    }

    public QueryParams addIn(String propertyName, Collection<Object> values) {
        if (null != values && values.size() > 0) {
            ins.put(propertyName, values);
        }

        return this;
    }

    public Map<String, Object> getLikes() {
        return likes;
    }

    public Map<String, Collection<Object>> getIns() {
        return ins;
    }

    public List<Object> getParas() {
        return paras;
    }

    public QueryParams like(String propertyName, String value, MatchMode matchMode) {
        this.likes.put(propertyName, matchMode.toMatchString(value));
        return this;
    }

    public QueryParams like(String propertyName, String value) {
        like(propertyName, value, MatchMode.ANYWHERE);
        return this;
    }


    public List<String> getGroups() {
        return groups;
    }

    private String prefix(String prefix) {
        if (prefix != null && !"".equals(prefix.trim())) {
            return prefix += ".";
        }
        return "m.";
    }

    public String toGroupSQL(String prefix) {
        prefix = prefix(prefix);

        if (groups != null && groups.size() >= 1) {

            StringBuilder g = new StringBuilder();

            for (String group : groups) {
                g.append(prefix).append(group).append(", ");

            }

            g.deleteCharAt(g.length() - 2);

            return String.format(" GROUP BY %s ", g.toString());
        }
        return "";
    }

    public String toLikeSQL(String prefix) {
        prefix = prefix(prefix);

        if (likes != null && !likes.isEmpty()) {

            StringBuilder g = new StringBuilder();

            for (String like : likes.keySet()) {
                String _sql = " and  %s%s like :%s ";
                g.append(String.format(_sql, prefix, like, like));
            }

            return g.toString();
        }
        return "";
    }

    public String toInSQL(String prefix) {
        prefix = prefix(prefix);

        if (ins != null && !ins.isEmpty()) {

            StringBuilder g = new StringBuilder();

            for (String in : ins.keySet()) {
                String _sql = " and  %s%s in( :%s )";
                g.append(String.format(_sql, prefix, in, in));
            }

            return g.toString();
        }
        return "";
    }


    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    public QueryParams add(String key, Object _value) {
        if (_value == null) {
            attrs.remove(key);
        } else {
            attrs.put(key, _value);

        }
        return this;

    }


    public Map<String, Object> getSqlValue() {
        return attrs;
    }

    public String toOrderSQL() {
        return toOrderSQL("");
    }

    public String toOrderSQL(String prefix) {
        prefix = prefix(prefix);

        if (!orders.isEmpty()) {

            StringBuffer sb = new StringBuffer();
            sb.append("Order BY ");
            for (String order : orders.keySet()) {
                sb.append(String.format("  %s%s %s, ", prefix, order, orders.get(order)));
            }
            sb.deleteCharAt(sb.length() - 2);
            return sb.toString();
        }

        return "";
    }

    public String toWhereSQL() {
        return toWhereSQL("");
    }

    public String toWhereSQL(String prefix) {
        prefix = prefix(prefix);
        String _sql = " and %s%s = :%s ";
        Set<String> keys = attrs.keySet();
        Map<String, Object> _value = new HashMap<String, Object>();
        StringBuffer sb = new StringBuffer();

        sb.append(sql);
        for (String key : keys) {
            String filterDotKey = key.replaceAll("\\.", "_");
            //    String filterDotKey = key;
            _value.put(filterDotKey, attrs.get(key));
            sb.append(String.format(_sql, prefix, key, filterDotKey));
        }
        this.attrs = _value;
        return sb.toString();
    }

    public String toFormatSQL(String hsql) {
        return  toFormatSQL(hsql, attrs, paras);
    }

    /**
     * 
     * @param hsql
     * @param attrs
     * @param values
     * @return
     */
    public static  String toFormatSQL(String hsql, Map<String, Object> attrs, List<Object> values) {
        Matcher matcher = Pattern.compile(":(\\w+)").matcher(hsql);

        while ( matcher.find()){

            String rexp = null;
            String group = matcher.group(1);

            Object ov = attrs.get(group);
            if (ov instanceof List)
            {
                StringBuilder sb = new StringBuilder();
                List vs = (List) ov;
                for (Object v : vs)
                {
                    sb.append("?,");
                    values.add(v);
                }
                sb.deleteCharAt(sb.length() - 1);
                rexp = sb.toString();

            }else
            {
                values.add(ov);
                rexp = "?";
            }
            hsql = hsql.replace(String.format(":%s", group), rexp);
        }
        return hsql;
    }
    public String toSqlExceptSelect(String tableName, String prefix ) {
        String hsql = " from " + tableName +  "  " + prefix + toWhereSQL(prefix) + toInSQL(prefix) + toLikeSQL(prefix) + toGroupSQL(prefix) + toOrderSQL(prefix);
        getSqlValue().putAll(getSqlLikes());
        getSqlValue().putAll(getIns());
        return toFormatSQL(hsql);

    }


    public static void main(String[] args) {

        QueryParams params = new QueryParams();
        params.add("id", 1);
        params.addGroup("cc");
        List<Object> names = new ArrayList<Object>();
        names.add("张三");
        names.add("李四");
        params.addIn("name", names);
        params.like("nick", "张");
        params.addOrder("time");
        String hsql = " from  user"  +  " t " + params.toWhereSQL("t") + params.toInSQL("t") + params.toLikeSQL("t") + params.toGroupSQL("t") + params.toOrderSQL("t");
        System.out.println("hsql:" + hsql);
        params.getSqlValue().putAll(params.getSqlLikes());
        params.getSqlValue().putAll(params.getIns());
        String sql = params.toFormatSQL(hsql);
        System.out.println("sql:" + sql);
        params.atts(params.getParas().toArray());

    }

    public void atts(Object ... os)
    {
        System.out.println("参数:");
        for (Object o : os)
        {
            System.out.print(o);
            System.out.print(",");
        }
    }


}
