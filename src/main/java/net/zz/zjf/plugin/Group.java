package net.zz.zjf.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZaoSheng on 2015/8/10.
 */
public class Group implements SQLParams {
    private List<String[]> groups = null;

    public Group add(String key) {

        return add(key, null);
    }

    public Group add(String value, String prefix) {
        if (value == null || "".equals(value)) return this;

        if (null == groups) groups = new ArrayList<String[]>();

        groups.add(new String[]{value, prefix});

        return this;
    }

    @Override
    public String toFormatSQL() {
        return toSQL();
    }

    @Override
    public String toSQL() {
        if (null == groups || groups.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("Group by ");
        for (String[] value : groups) {

            sb.append(String.format("%s%s, ", null == value[1] ? "" : (value[1] + '.'), value[0]));
        }
        sb.deleteCharAt(sb.length() - 2);
        return sb.toString();
    }

    public static void main(String[] args) {
        Group group = new Group();
        group.add("id");
        group.add("qq", "t");
        System.out.println(group.toSQL());
    }

}
