package net.zz.zjf.plugin;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZaoSheng on 2015/7/15.
 */
public class QueryParams implements SQLParams {
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private Map<String, Object> attrs = new HashMap<String, Object>();
    private List<Object> paras = new ArrayList<Object>();
    private Where wheres = null;
    private Order orders = null;
    private Group groups = null;


    @Override
    public String toFormatSQL() {
        return null;
    }

    @Override
    public String toSQL() {
        return null;
    }
}
