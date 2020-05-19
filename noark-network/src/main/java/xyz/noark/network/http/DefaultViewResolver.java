package xyz.noark.network.http;

import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.HttpResponseStatus;
import xyz.noark.core.ioc.wrap.method.HttpMethodWrapper;
import xyz.noark.network.http.exception.HandlerDeprecatedException;
import xyz.noark.network.http.exception.NoHandlerFoundException;

import java.util.Objects;

import static xyz.noark.log.LogHelper.logger;

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
            response.send((String) result);
        }
        // 当Json处理，日后再细化
        else {
            response.send(JSON.toJSONString(result));
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
                return (HttpResult) returnValue;
            }
            // 如果返回值不是HttpResult类或子类的话，底层会将方法的返回值封装为HttpResult对象里的data属性，然后再以特定的格式写入到response的body区域<br>
            else {
                HttpResult result = new HttpResult(HttpErrorCode.OK);
                result.setData(returnValue);
                return result;
            }
        }
    }

    @Override
    public void resolveException(HttpServletResponse response, Throwable cause) {
        // 404 Handler没找到...
        if (cause instanceof NoHandlerFoundException) {
            this.noHandlerFound(response, (NoHandlerFoundException) cause);
        }
        // API已过期...
        else if (cause instanceof HandlerDeprecatedException) {
            this.handleDeprecated(response);
        }
        // 服务器内部错误.
        else {
            this.handleException(response);
            logger.debug("服务器内部错误. cause={}", cause);
        }
    }

    private void handleException(HttpServletResponse response) {
        HttpResult result = new HttpResult(HttpErrorCode.INTERNAL_ERROR, "request's API internal error.");
        response.send(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), JSON.toJSONString(result));
    }

    private void handleDeprecated(HttpServletResponse response) {
        HttpResult result = new HttpResult(HttpErrorCode.API_DEPRECATED, "request's API Deprecated.");
        response.send(HttpResponseStatus.NOT_FOUND.code(), JSON.toJSONString(result));
    }

    private void noHandlerFound(HttpServletResponse response, NoHandlerFoundException exception) {
        HttpResult result = new HttpResult(HttpErrorCode.NO_API, "request's API Unrealized.");
        response.send(HttpResponseStatus.NOT_FOUND.code(), JSON.toJSONString(result));
    }
}
