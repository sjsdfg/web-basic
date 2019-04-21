package cn.sjsdfg.web.method.support;

import cn.sjsdfg.web.http.converter.properties.PropertiesHttpMessageConverter;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

/**
 * {@link java.util.Properties}
 * {@link HandlerMethodReturnValueHandler}
 * Created by Joe on 2019/4/4.
 */
public class PropertiesHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    private PropertiesHttpMessageConverter propertiesHttpMessageConverter = new PropertiesHttpMessageConverter();

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return Properties.class.equals(returnType.getMethod().getReturnType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        Properties properties = (Properties) returnValue;
        // 获取 Content-Type
        ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
        // Servlet Request API
        HttpServletRequest request = servletWebRequest.getRequest();
        // 获取请求头中的媒体类型
        MediaType mediaType = MediaType.parseMediaType(request.getContentType());
        // 获取 Servlet Response 对象
        HttpServletResponse response = servletWebRequest.getResponse();
        HttpOutputMessage message = new ServletServerHttpResponse(response);

        propertiesHttpMessageConverter.write(properties, mediaType, message);
    }
}
