package net.zz.zjf.plugin;

import com.jfinal.plugin.activerecord.Page;

import java.util.*;

/**
 * Created by ZaoSheng on 2015/7/15.
 */
public class QueryParams {
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private List<String> groups = new ArrayList<String>();
    private Map<String, Object> likes = new HashMap<String, Object>();
    private Map<String, Object> orders = new HashMap<String, Object>();
    private Map<String, List<String>> ins = new HashMap<String, List<String>>();
    protected String sql = " where 1=1 ";
    protected String orderHQL = "";
    private Map<String, Object> value = new HashMap<String, Object>();
    private Map<String, List<Object>> mutValues = new HashMap<String, List<Object>>();

    public void addOrder(String key, OrderAD ad) {
        orders.put(key, ad.name());
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

    public QueryParams addIn(String propertyName, List<String> values) {
        if (null != values && values.size() > 0) {
            ins.put(propertyName, values);
        }

        return this;
    }

    public Map<String, Object> getLikes() {
        return likes;
    }

    public Map<String, List<String>> getIns() {
        return ins;
    }

    public QueryParams like(String propertyName, String value, MatchMode matchMode) {
        this.likes.put(propertyName, matchMode.toMatchString(value));

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
        prefix(prefix);

        if (groups != null && groups.size() >= 1) {

            StringBuilder g = new StringBuilder();

            for (String group : groups) {
                g.append(prefix).append(group).append(", ");
            }

            g.delete(g.lastIndexOf(","), g.lastIndexOf(",") + 1);

            return String.format(" GROUP BY %s ", g.toString());
        }
        return "";
    }

    public String toLikeSQL(String prefix) {
        prefix(prefix);

        if (likes != null && !likes.isEmpty()) {

            StringBuilder g = new StringBuilder();

            for (String like : likes.keySet()) {
                String _hql = " and  %s%s like :%s ";
                g.append(String.format(_hql, prefix, like, like));
            }

            return g.toString();
        }
        return "";
    }

    public String toInSQL(String prefix) {
        prefix(prefix);

        if (ins != null && !ins.isEmpty()) {

            StringBuilder g = new StringBuilder();

            for (String in : ins.keySet()) {
                String _hql = " and  %s%s in( :%s )";
                g.append(String.format(_hql, prefix, in, in));
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
            value.remove(key);
        } else {
            value.put(key, _value);

        }
        return this;

    }


    public Map<String, Object> getSqlValue() {
        return value;
    }

    public String toOrderSQL() {
        return toOrderSQL("");
    }

    public String toOrderSQL(String prefix) {
        prefix(prefix);

        if (!orders.isEmpty()) {

            StringBuffer sb = new StringBuffer();
            sb.append("Order BY ");
            for (String order : orders.keySet()) {
                String.format("  %s%s %s, ", prefix, order, orders.get(order));
            }
            sb.delete(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1);
            return sb.toString();
        }

        return "";
    }

    public String toWhereSQL() {
        return toWhereSQL("");
    }

    public String toWhereSQL(String prefix) {
        prefix(prefix);
        String _hql = " and %s%s = :%s ";
        Set<String> keys = value.keySet();
        Map<String, Object> _value = new HashMap<String, Object>();
        StringBuffer sb = new StringBuffer();

        sb.append(sql);
        for (String key : keys) {
            String filterDotKey = key.replaceAll("\\.", "_");
            //    String filterDotKey = key;
            _value.put(filterDotKey, value.get(key));
            sb.append(String.format(_hql, prefix, key, filterDotKey));
        }
        this.value = _value;
        return sb.toString();
    }

}
