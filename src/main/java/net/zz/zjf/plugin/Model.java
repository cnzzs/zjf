package net.zz.zjf.plugin;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZaoSheng on 2015/7/15.
 */
public class Model<M extends com.jfinal.plugin.activerecord.Model> extends com.jfinal.plugin.activerecord.Model<M> {

    public Page<M> queryPageUseSQL(QueryParams params,boolean isPage) {

        return  new Page<M>(null, 1, 10, 10, 10);
    }
    private Table getTable() {
        return TableMapping.me().getTable(this.getClass());
    }

    protected String getName() {
        return getTable().getName();
    }
    protected String getPrimaryKey() {
        return getTable().getPrimaryKey();
    }
    protected String getSecondaryKey() {
        return getTable().getSecondaryKey();
    }
}
