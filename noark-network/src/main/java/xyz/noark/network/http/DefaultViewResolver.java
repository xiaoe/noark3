package xyz.noark.network.http;

import xyz.noark.core.ioc.wrap.method.HttpMethodWrapper;

import java.util.Objects;

/**
 * 默认的视图解析器
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class DefaultViewResolver implements ViewResolver {

    @Override
    public void resolveView(HttpServletRequest request, HttpServletResponse response, HttpMethodWrapper handler, Object result) {
        result = this.handleResult(handler, result);
        // 返回值是字符串, 直接当成响应返回
        if (result instanceof String) {
            response.writeString((String) result);
        }
        // 当Json处理，日后再细化
        else {
            response.writeObject(result);
        }
    }


    private Object handleResult(HttpMethodWrapper handler, Object returnValue) {
        // 0. 当方法上面有写@ResponseBody时，那就直接以特定的格式写入到response的body区域<br>
        if (Objects.nonNull(handler.getResponseBody())) {
            return returnValue;
        }
        // 1. 当方法上面没有写@ResponseBody时<br>
        else {
            // 如果返回值是HttpResult类或子类的话，那就直接以特定的格式写入到response的body区域<br>
            if (returnValue instanceof HttpResult) {
                return returnValue;
            }
            // 如果返回值不是HttpResult类或子类的话，底层会将方法的返回值封装为HttpResult对象里的data属性，然后再以特定的格式写入到response的body区域<br>
            else {
                HttpResult result = new HttpResult(HttpErrorCode.OK);
                result.setData(returnValue);
                return result;
            }
        }
    }
}
