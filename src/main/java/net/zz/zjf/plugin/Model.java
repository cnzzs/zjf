package net.zz.zjf.plugin;

import com.jfinal.plugin.activerecord.*;
import net.zz.dao.params.Params;
import net.zz.dao.params.QueryParams;
import net.zz.dao.params.Restriction;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZaoSheng on 2015/7/15.
 */
public class Model<M extends Model, PK extends Serializable> extends com.jfinal.plugin.activerecord.Model<M> {

    protected String getSqlExceptSelect(Params params)
    {
        QueryParams queryParams = params.builderParas();
        return  String.format(" from %s %s %s", getTableName(), queryParams.alias(), queryParams.getSqlString());
    }

    public Long countSqlResult(String sqlExceptSelect, Object... params) {
        List result = Db.query("SELECT COUNT(*) " + DbKit.replaceFormatSqlOrderBy(sqlExceptSelect), params);
        int size = result.size();
        if (size == 1) {
            return ((Number) result.get(0)).longValue();
        }
        return 0L;
    }

    public Long countSqlResult(String sql, Map<String, Object> attrs) {
        List<Object> params = new ArrayList<Object>();
        String sqlExceptSelect = QueryParams.toFormatSQL(sql, attrs, params);
        return countSqlResult(sqlExceptSelect, params.toArray());
    }

    /**
    *
    *  例子：
    *  sql="select * from zz z where z.name = ?"
    *
    * findFirstBySQLQuery(queryOrNamedQuery, "张三")
    * @param sql
    * @param attrs
    * @return List
    */
    public List<Map<String, Object>> findMapBySQLQuery(String sql, Object...attrs) {
        List<Record> records = Db.find(sql, attrs);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Record record : records) {
            list.add(record.getColumns());
        }
        return list;
    }

    /**
    *  例子：
    *  queryOrNamedQuery="select * from zz z where z.name = :name"
    * attrs.put("name", "张三")
    * findFirstBySQLQuery(queryOrNamedQuery, attrs)
    * @param queryOrNamedQuery
    * @param attrs
    * @return List
    */
    public List<Map<String, Object>> findMapBySQLQuery(String queryOrNamedQuery, Map<String, Object> attrs) {
        List<Object> params = new ArrayList<Object>();
        String sql = QueryParams.toFormatSQL(queryOrNamedQuery, attrs, params);
        List<Record> records = Db.find(sql, params);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Record record : records) {
            list.add(record.getColumns());
        }
        return list;
    }

    /**
    *
    *  例子：
    *  queryOrNamedQuery="select * from zz z where z.name = :name"
    * attrs.put("name", "张三")
    * findFirstBySQLQuery(queryOrNamedQuery, attrs)
    * @param queryOrNamedQuery
    * @param attrs
    * @return List
    */
    public List<M> findBySQLQuery(String queryOrNamedQuery, Map<String, Object> attrs) {
        List<Object> params = new ArrayList<Object>();
        String sql = QueryParams.toFormatSQL(queryOrNamedQuery, attrs, params);

        return find(sql,params);
    }

    /**
     * 获得一条记录
     * 例子：
     * queryOrNamedQuery="select * from zz z where z.name = :name"
     * attrs.put("name", "张三")
     * findFirstBySQLQuery(queryOrNamedQuery, attrs)
     *
     * @param queryOrNamedQuery
     * @param attrs
     * @return
     */
    public Map<String, Object> findMapFirstBySQLQuery(String queryOrNamedQuery, Map<String, Object> attrs) {
        List<Object> params = new ArrayList<Object>();
        String sql = QueryParams.toFormatSQL(queryOrNamedQuery, attrs, params);
        List<Record> records = Db.find(sql, params);

        return records.size() >= 1 ?  records.get(0).getColumns() : null;
    }

    /**
     * 获得一条记录
     * 例子：
     * sql="select * from zz z where z.name = ?"
     * attrs.put("name", "张三")
     * findFirstBySQLQuery(queryOrNamedQuery, attrs)
     *
     * @param sql
     * @param attrs
     * @return
     */
    public Map<String, Object> findMapFirstBySQLQuery(String sql, Object...attrs) {
        List<Record> records = Db.find(sql, attrs);
        return records.size() >= 1 ?  records.get(0).getColumns() : null;
    }
    /**
     * 获得一条记录
     * 例子：
     * queryOrNamedQuery="select * from zz z where z.name = :name"
     * attrs.put("name", "张三")
     * findFirstBySQLQuery(queryOrNamedQuery, attrs)
     *
     * @param queryOrNamedQuery
     * @param attrs
     * @return
     */
    public M findFirstBySQLQuery(String queryOrNamedQuery, Map<String, Object> attrs) {
        List<Object> params = new ArrayList<Object>();
        String sql = QueryParams.toFormatSQL(queryOrNamedQuery, attrs, params);
        List<M> ms = find(sql, params);

        return ms.size() >= 1 ? ms.get(0) : null;
    }

    /**
     * @param params
     * @param isPage
     * @return
     */
    public Page<M> queryPageUseSQL(boolean isPage, Params params) {

     String sqlExceptSelect = getSqlExceptSelect(params);
        if (isPage) {
            return paginate(params.getPage().getPageIndex(), params.getPage().getPageSize(), "SELECT * ", sqlExceptSelect, params.getParas().toArray());
        }
        long totalRow = 0L;
        List result = Db.query("SELECT COUNT(*) " + DbKit.replaceFormatSqlOrderBy(sqlExceptSelect), params.getParas().toArray());
        int size = result.size();
        if (size == 1) {
            totalRow = ((Number) result.get(0)).longValue();
        } else {
            if (size <= 1) {
                return new Page(new ArrayList(0), 1, 10, 0, 0);
            }
            totalRow = (long) result.size();
        }

        List list = find("SELECT * " +  sqlExceptSelect, params.getParas().toArray());
        return new Page(list, 1, (int) totalRow, 1, (int) totalRow);

    }

    /**
     * @param params 查询参数
     * @return List
     */
    public List<M> findByProperty(Params params) {
        String sqlExceptSelect = getSqlExceptSelect(params);
        return find("SELECT * " + sqlExceptSelect, params.getParas().toArray());
    }

    /**
     * 通过orm实体属性名称查询全部
     *
     * @param propertyName orm实体属性名称
     * @param value        值
     * @return List
     */
    public List<M> findByProperty(String propertyName, Object value) {

        return findByProperty( Restriction.EQ, propertyName, value);
    }

    /**
     * 通过orm实体属性名称查询全部
     *
     * @param propertyName orm实体属性名称
     * @param isNull true 为 value=null
     * @return M
     */
    public M findFirstByIsNull(String propertyName, boolean isNull) {
        List<M> result = findByProperty(isNull ? Restriction.NULL : Restriction.NOTNULL, propertyName);
        return result.size() > 0 ? result.get(0) : null;
    }
    /**
     * 通过orm实体属性名称查询全部
     *
     * @param propertyName orm实体属性名称
     * @param isNull true 为 value=null
     * @return  List<M>
     */
    public List<M>  findByIsNull(String propertyName, boolean isNull) {
        return findByProperty(isNull ? Restriction.NULL : Restriction.NOTNULL, propertyName);
    }

    /**
     * 通过orm实体属性名称查询全部
     *
     * @param propertyName orm实体属性名称
     * @param value        值
     * @return M
     */
    public M findFirst(String propertyName, Object value) {
        List<M> result = findByProperty(Restriction.EQ, propertyName, value);
        return result.size() > 0 ? result.get(0) : null;
    }

    /**
     * 通过orm实体属性名称查询全部
     * @param restriction 规则
     * @param propertyName orm实体属性名称
     * @param value        值
     * @return List
     */
    public List<M> findByProperty( Restriction restriction, String propertyName, Object ... value) {
      String whereSQL =propertyName + restriction.toMatchString(propertyName);
        Matcher matcher = Pattern.compile(":(\\w+)").matcher(whereSQL);
        String group;
        for(String rexp = null; matcher.find(); whereSQL = whereSQL.replace(String.format(":%s", group), rexp)) {
            group = matcher.group(1);
            StringBuilder sb = new StringBuilder();
            for (Object v : value)
            {
                sb.append("?,");
            }
            sb.deleteCharAt(sb.length() - 1);
            rexp = sb.toString();

        }
        return find(String.format("SELECT * from %s where 1=1 and %s", getTableName(), whereSQL), value);
    }

    /**
     * 通过orm实体属性名称查询全部
     *
     * @param restriction 规则
     * @param propertyName orm实体属性名称
     * @param value        值
     * @return M
     */
    public M findFirst( Restriction restriction, String propertyName, Object ... value) {
        List<M> result = findByProperty(restriction, propertyName, value);
        return result.size() > 0 ? result.get(0) : null;
    }


    public M findFirst(Params params) {

        M value = this.findFirst("SELECT * ", getSqlExceptSelect(params), params.getParas().toArray());
        return value;

    }

    public boolean saveOrUpdate() {
        if (null == this.get(getPrimaryKey())) return save();

        return update();
    }

    public boolean saveAll(List<M> ms) {
        /**
         *  这样写确定好？
         */
        for (M m : ms) {

            if (!m.saveOrUpdate()) {
//                throw new ActiveRecordException("保存失败!");
                throw new ActiveRecordException("Save failed :" + m);
            }
        }
        return true;
   /**   int size = 0;
            if ((size = ms.size()) <= 0)
            {
               throw new ActiveRecordException("(List<M> is null ?");
            }
            Config config = this.getConfig();
            Table table = this.getTable();

            Connection conn = null;
            PreparedStatement pst = null;
            boolean result = false;
            StringBuilder sql = new StringBuilder();
            ArrayList paras = new ArrayList();
            config.getDialect().forModelSave(table, ms.get(0).getAttrs(), sql, paras);
            boolean e;
            try {
                conn = config.getConnection();
                if(config.getDialect().isOracle()) {
                    pst = conn.prepareStatement(sql.toString(), new String[]{table.getPrimaryKey()});
                } else {
                    pst = conn.prepareStatement(sql.toString(), 1);
                }

                config.getDialect().fillStatement(pst, paras);
                if (size >= 2 ){
                    for (int i = 1; i < size; i++ )
                    {

                        config.getDialect().forModelSave(table, ms.get(i).getAttrs(), sql, paras);
                        pst.addBatch();
                    }
                }
                int result1 = pst.executeUpdate();
                this.getGeneratedKey(pst, table);
                clear();
                e = result1 >= 1;
            } catch (Exception var12) {
                throw new ActiveRecordException(var12);
            } finally {
                config.close(pst, conn);
            }

            return e;*/


    }


    public boolean deleteAll(List<M> ms) {

        for (M m : ms) {
            if (!m.delete()) throw new ActiveRecordException("Delete failed :" + m);
        }
        return true;
    }

    public boolean deleteAllById(List<PK> ids) {

            /*for (PK id : ids) {
                if (!deleteById(id)) throw new ActiveRecordException("Delete failed :" + id);
            }*/

        return deleteAllById((PK[])ids.toArray());
    }
  public boolean deleteAllById(PK ... ids) {

            /*for (PK id : ids) {
                if (!deleteById(id)) throw new ActiveRecordException("Delete failed :" + id);
            }*/
        StringBuilder sql = new StringBuilder();
        sql.append("delete from `").append(getTableName()).append("` where `").append(getPrimaryKey()).append(" in ( ");
        for (PK id : ids) {
            sql.append("?,");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        return Db.update(sql.toString(), ids) > 0;
    }



    /**
     * 按PK列表获取对象列表.
     *
     * @param ids 主键ID集合
     * @return List
     */
    public List<M> getByIds(Collection<PK> ids) {
        if (ids.size() <= 0) {
            return Collections.emptyList();
        }

        return findByProperty(Restriction.IN, getPrimaryKey(), ids.toArray());

    }


    protected void getGeneratedKey(PreparedStatement pst, Table table) throws SQLException {
        String pKey = table.getPrimaryKey();
        if (this.get(pKey) == null || this.getConfig().getDialect().isOracle()) {
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                Class colType = table.getColumnType(pKey);
                if (colType != Integer.class && colType != Integer.TYPE) {
                    if (colType != Long.class && colType != Long.TYPE) {
                        this.set(pKey, rs.getObject(1));
                    } else {
                        this.set(pKey, Long.valueOf(rs.getLong(1)));
                    }
                } else {
                    this.set(pKey, Integer.valueOf(rs.getInt(1)));
                }

                rs.close();
            }
        }

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
