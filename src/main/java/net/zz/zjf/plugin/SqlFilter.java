package net.zz.zjf.plugin;

import net.zz.dao.params.Order.OrderAD;
import net.zz.dao.params.QueryParams;
import net.zz.dao.params.Restriction;
import net.zz.dao.params.Where;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Created by ZaoSheng on 2015/10/25.
 */
public class SqlFilter {
    private HttpServletRequest request;// 为了获取request里面传过来的动态参数
    private String column;
    private OrderAD order;
    Where params = new Where().where();

	/**
	 * 默认构造
	 */
	public SqlFilter(){}
	/**
	 * 带参构造
	 * 
	 * @param request
	 */
	public SqlFilter(HttpServletRequest request) {
		this.request = request;
		addFilter(request);
	}

    public boolean isBlank(String str)
    {
       return null == str || str.trim().isEmpty();
    }

    private void setOrderValue(String prefix)
    {
        switch (order)
        {
            case DESC:
                params.order(column, prefix);
                break;
            case ASC:
                params.order().ASC(column, prefix);
                break;
        }
    }

	/**
	 * 获得添加过滤字段后加上排序字段的Sql
	 * 
	 * @return
	 */
	public void setOrder() {


		if (!isBlank(column) && null != order) {
            int index = column.indexOf(".");
			if ( index < 1) {
                setOrderValue(null);
			}else{
                setOrderValue( column.substring(index));
            }

		} else {
			if (request != null) {
				String s = request.getParameter("sort");
				String o = request.getParameter("order");
				if (isBlank(s) || isBlank(o)) {
					return;
				}
                column = s;
				order = OrderAD.valueOf(o);
                int index = column.indexOf(".");
                if ( index < 1) {
                    setOrderValue( null);
                }else{
                    setOrderValue( column.substring(index));
                }

			}
		}

	}
	/**
	 * 添加过滤
	 * 
	 * @param request
	 */
	public void addFilter(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String value = request.getParameter(name);
			addFilter(name, value);
		}
	}
	

	/**
	 * 添加过滤
	 * 
	 * 举例，name传递：QUERY^t#id^|^EQ
	 * 举例，name传递：QUERY^t#id^!|^EQ
	 *
	 * 举例，value传递：0
	 * 
	 * @param name
	 * @param value
	 */
	public void addFilter(String name, Object value) {
		if (name != null && value != null) {
			if (name.startsWith("QUERY^")) {// 如果有需要过滤的字段
				String[] filterParams = name.split("\\^");
//				String[] filterParams = StringUtils.split(name, "_");
				if (filterParams.length == 4) {
                    String[] ppn =  filterParams[1].split("#");
                    String prefix = ppn[0]; //表的别名
					String propertyName = constructCol(ppn[1]);// 要过滤的字段名称
					String ao = filterParams[2];// 操作的逻辑
					String operator = filterParams[3];// SQL操作符

                    if ("|".equals(ao)){
                        params.or(propertyName, value, Restriction.valueOf(operator), prefix);
                    }else{
                        params.and(propertyName, value, Restriction.valueOf(operator), prefix);
                    }
				}
			}
		}
	}

	public String constructCol(String str){
		String A = "^[A-Z]";
		StringBuffer col=new StringBuffer();
		for(int i=0;i<str.length();i++){
			//str.charAt(i)
			
			if(String.valueOf(str.charAt(i)).matches(A)){
				col.append("_");
			}
			col.append(str.charAt(i));
		}
		return col.toString();
	}

    public QueryParams getQueryParams() {
        return params;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public OrderAD getOrder() {
        return order;
    }

    public void setOrder(OrderAD order) {
        this.order = order;
    }

    public static void main(String[] args) {

    }
}
