package net.zz.zjf.plugin;

import com.jfinal.plugin.activerecord.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZaoSheng on 2015/7/15.
 */
public class Model<M extends com.jfinal.plugin.activerecord.Model> extends com.jfinal.plugin.activerecord.Model<M> {


   /* private String getSqlExceptSelect(QueryParams params)
    {
        String hsql = " from " + getTableName() +  " t " + params.toWhereSQL("t") + params.toInSQL("t") + params.toLikeSQL("t") + params.toGroupSQL("t") + params.toOrderSQL("t");
        params.getSqlValue().putAll(params.getSqlLikes());
        params.getSqlValue().putAll(params.getIns());
        return params.toFormatSQL(hsql);
    }*/

    /**
     *
     * @param params
     * @param isPage
     * @return
     */
    public Page<M> queryPageUseSQL(QueryParams params, boolean isPage){

        String sqlExceptSelect = params.toSqlExceptSelect(getTableName() , "t");
      if (isPage){
            return paginate(params.getPageIndex(), params.getPageSize(), "SELECT * ", sqlExceptSelect, params.getParas().toArray());
        }
        long totalRow = 0L;
        List result = Db.query( "SELECT COUNT(*) " + DbKit.replaceFormatSqlOrderBy(sqlExceptSelect), params.getParas().toArray());
        int size = result.size();
        if(size == 1) {
            totalRow = ((Number)result.get(0)).longValue();
        } else {
            if(size <= 1) {
                return new Page(new ArrayList(0), 1, 10, 0, 0);
            }
            totalRow = (long)result.size();
        }

        List list = find(String.format("SELECT * ", sqlExceptSelect.toString()), params.getParas().toArray());
        return new Page(list, 1, (int) totalRow, 1, (int)totalRow);
    }

    /**
     *
     * @param params 查询参数
     * @return List
     */
    public List<M> findByProperty(QueryParams params) {
        String sqlExceptSelect = params.toSqlExceptSelect(getTableName() , "t");
        return find(String.format("SELECT * ", sqlExceptSelect.toString()), params.getParas().toArray());
    }

    /**
     * 通过orm实体属性名称查询全部
     *
     * @param propertyName orm实体属性名称
     * @param value        值
     * @return List
     */
    public List<M> findByProperty(String propertyName, Object value) {

        return findByProperty(propertyName, value, Restriction.EQ);
    }
    /**
     * 通过orm实体属性名称查询全部
     *
     * @param propertyName orm实体属性名称
     * @param value        值
     * @return M
     */
    public M findFirst(String propertyName, Object value) {
        List<M> result = findByProperty(propertyName, value, Restriction.EQ);
        return result.size() > 0?result.get(0):null;
    }
    /**
     * 通过orm实体属性名称查询全部
     *
     * @param propertyName orm实体属性名称
     * @param value        值
     * @return List
     */
    public List<M> findByProperty(String propertyName, Object value, Restriction restriction) {
        String sql = "SELECT * FROM %s WHERE %s %s";
        return find(String.format(sql, getTableName(), propertyName, restriction.toMatchString("?")), value);
    }
    /**
     * 通过orm实体属性名称查询全部
     *
     * @param propertyName orm实体属性名称
     * @param value        值
     * @return M
     */
    public M findFirst(String propertyName, Object value, Restriction restriction) {
        List<M> result = findByProperty(propertyName, value, restriction);
        return result.size() > 0?result.get(0):null;
    }


    public M findFirst(QueryParams params) {
        String hsql = " FROM "  + getTableName() + " t "   + params.toWhereSQL("t");
        M value = this.findFirst(String.format("SELECT * %s", params.toFormatSQL(hsql)) , params.getParas().toArray());
        return value;
    }


    public boolean saveAll(List<M> ms) {

        return false;
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
