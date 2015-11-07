package net.zz.zjf.config;

import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.jfinal.plugin.activerecord.Model;
import net.zz.zjf.annotation.C;
import net.zz.zjf.annotation.M;
import net.zz.zjf.plugin.Scan;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ZaoSheng on 2015/5/24.
 */
public abstract class JFinalConfig  extends com.jfinal.config.JFinalConfig{
    private static final Logger LOG = Logger.getLogger(JFinalConfig.class);
    private static final List<String> controlPackage = new ArrayList<String>();
    private static final List<String> basePackage = new ArrayList<String>();

    /**
     * Scan control
     */
    public abstract void controlSscan(List<String> controlPackage);

    /**
     * Scan basePackage
     */
    public abstract void componentSscan(List<String> basePackage);

    /**
     *  设置数据源
     * @return IDataSourceProvider
     */
    public abstract IDataSourceProvider setDataSource ();



    /**
     *  controller
     * @param me
     */

    @Override
    public void configRoute(Routes me) {

//        String path = this.getClass().getClassLoader().getResource("").getPath();
        controlSscan(controlPackage);//获取需要扫描的包

        //扫描器
        Scan driven = new Scan();
        for (String pake : controlPackage){
            Set<Class<?>> clazzs = driven.getClasses(pake);
            /*System.out.println("pake: " + pake);*/
            for (Class<?> clazz : clazzs) {
//            	System.out.println(clazz.getSuperclass());
                LOG.info(clazz.getName());
                Class<?> superclass = clazz.getSuperclass();
                Class<?> jfClz = com.jfinal.core.Controller.class;
                if (superclass ==  jfClz|| superclass.getSuperclass() == jfClz) {
                   C con = clazz.getAnnotation(C.class);
                   if (null != con) {
                       me.add(con.value(), (Class<? extends Controller>) clazz);
                   }
               }
            }
        }
    }

    /**
     * model
     * @param me
     */
    @Override
    public void configPlugin(Plugins me) {

         componentSscan(basePackage);

        IDataSourceProvider iDataSourceProvider = setDataSource();
        try {
            me.add((IPlugin) iDataSourceProvider);
        }catch (Exception e){
            throw new RuntimeException("is not IPlugin type");
        }
        ActiveRecordPlugin arp = new ActiveRecordPlugin(iDataSourceProvider);

        addActiveRecord(arp); // 加入附加的活动记录
        Scan driven = new Scan();
        for (String pake : basePackage){
            Set<Class<?>> clazzs = driven.getClasses(pake);

            for (Class<?> clazz : clazzs) {
                LOG.info(clazz.getName());
                Class superClass = clazz.getSuperclass();
                Class<?> jfClz =  com.jfinal.plugin.activerecord.Model.class;
                if (superClass == jfClz || superClass.getSuperclass() ==  jfClz) {
                   M model = clazz.getAnnotation(M.class);
                    if (null != model) {
                        arp.addMapping(model.value(), model.id(), (Class<? extends Model<?>>) clazz);
                    }
                }
            }
        }
        me.add(arp);
    }

    /**
     * 这里进行附加的活动记录的添加，
     * @param arp 活动记录插件
     */
    public void addActiveRecord(ActiveRecordPlugin arp){
        //arp.setShowSql(true);//设置是sql显示开关
    }

    /**
     * Interceptors
     * @param me
     */
    @Override
    public void configInterceptor(Interceptors me) {

    }

    @Override
    public void configHandler(Handlers me) {

    }

}