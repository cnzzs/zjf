package net.zz.zjf.plugin;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import net.zz.zjf.plugin.exception.CommonException;

import java.util.HashMap;
import java.util.Map;


public class ExceptionInterceptor implements Interceptor {

    @Override
    public void intercept(ActionInvocation ai) {
        Map<String,Object> data = new HashMap<String,Object>();

        try {
            System.out.println("exception befor");
            ai.invoke();
            System.out.println("exception after");
        } catch (Exception ex){
            if (ex instanceof CommonException) {
                CommonException m = (CommonException) ex ;
                data.put("ZZCode", m.getCode());
                data.put("message", m.getMessage());
                //data.put("message",msg.getMessage("MZCode_"+m.getCode(), new Object[]{}, Locale.CHINA));
            }else {
                ex.printStackTrace();
                data.put("MZCode", 500);
                data.put("message", "Server error - [500]");
            }
            ai.getController().renderJson(data);

        }

    }
}
