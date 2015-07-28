package net.zz.zjf.plugin;

import com.jfinal.plugin.activerecord.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZaoSheng on 2015/7/15.
 */
public class Model<M extends com.jfinal.plugin.activerecord.Model> extends com.jfinal.plugin.activerecord.Model<M> {

    public Page<M> queryPageUseSQL(QueryParams params, boolean isPage) throws Exception {
        String hsql = " from " + getTableName() +  " t " + params.toWhereSQL("t") + params.toInSQL("t") + params.toLikeSQL("t") + params.toGroupSQL("t") + params.toOrderSQL("t");
        System.out.println("hsql:" + hsql);
        params.getSqlValue().putAll(params.getSqlLikes());
        params.getSqlValue().putAll(params.getIns());
        String sqlExceptSelect = params.toFormatSQL(hsql);
        return paginate(params.getPageIndex(), params.getPageSize(), "SELECT * ", sqlExceptSelect, params.getParas().toArray());
    }


    private Table getTable() {
        return TableMapping.me().getTable(this.getClass());
    }

    protected String getTableName() {
        return getTable().getName();
    }
    protected String getPrimaryKey() {
        return getTable().getPrimaryKey();
    }
    protected String getSecondaryKey() {
        return getTable().getSecondaryKey();
    }
    protected Config getConfig() {
        return DbKit.getConfig(this.getClass());
    }

}
