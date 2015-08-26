package net.zz.zjf.plugin;


/**
 * Created by ZaoSheng on 2015/7/15.
 */
public interface SQLParams {

   String toFormatSQL();
   String toSQL();
    public QueryParams builderAttrs();
    public QueryParams builderParas();

}
