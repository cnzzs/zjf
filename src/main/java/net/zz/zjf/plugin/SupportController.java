package net.zz.zjf.plugin;

import com.jfinal.core.Controller;
import com.jfinal.log.Log4jLogger;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZaoSheng on 2015/10/25.
 */
public  class SupportController extends Controller{

    protected Map<String, Object> successData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("ZZCode", 0);
        setAttrs(data);
        return data;
    }

    /**
     *
     * @return
     */
    protected Map<String, Object> successData(String key, Object result) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("ZZCode", 0);
        data.put(key, result);
        setAttrs(data);
        return data;
    }
    /**
     *
     * @return
     */
    protected Map<String, Object> successData(Object message) {

        return successData("message", message);
    }

    /**
     *
     * @return
     */
    protected Map<String, Object> throwsData(Object ZZCode, Object message)  {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("ZZCode", ZZCode);
        data.put("message",message);
        setAttrs(data);
        return data;
    }




    protected Map<String, Object> assemblyPageData(Page result) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("ZZCode", 0);
        data.put("total", result.getTotalRow());
        data.put("page", result.getPageNumber());
        data.put("count", result.getPageSize());
        data.put("rows", result.getList());
        setAttrs(data);
        return data;
    }

}
