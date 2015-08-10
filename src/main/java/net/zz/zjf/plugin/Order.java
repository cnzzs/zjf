package net.zz.zjf.plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZaoSheng on 2015/8/5.
 */
public class Order implements SQLParams {
    private Map<String, String[]> orders;

    public enum OrderAD {
        ASC, DESC
    }

    public Order() {

    }

    public Order(String key) {
        add(key, OrderAD.DESC, null);
    }

    public Order(String key, OrderAD value) {
        add(key, value, null);
    }
    public Order(String key, OrderAD value, String prefix ) {
        add(key, value, prefix);
    }

    public Order add(String key) {
        return add(key, OrderAD.DESC);
    }

    public Order add(String key, OrderAD value) {

        return add(key, value, null);
    }

    public Order add(String key, OrderAD value, String prefix) {
        if (null == orders) orders = new HashMap<String, String[]>();

        if (null == value) {
            orders.remove(key);
        } else {
            orders.put(key, new String[]{value.name(), prefix});

        }

        return this;
    }

    @Override
    public String toFormatSQL() {
        return toSQL();
    }

    @Override
    public String toSQL() {
        if (null == orders || orders.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        sb.append(" Order by ");
        for (String key : orders.keySet()) {
            String[] value = orders.get(key);
            sb.append(String.format("%s%s %s, ", null == value[1] ? "" : (value[1] + '.'), key, value[0]));
        }
        sb.deleteCharAt(sb.length() - 2);
        return sb.toString();
    }


    public Map<String, String[]> getOrders() {
        return orders;
    }

    public static void main(String[] args) {
        Order order = new Order();
        order.add("id");
        order.add("qq", OrderAD.DESC, "t");
        System.out.println(order.toSQL());
    }

}