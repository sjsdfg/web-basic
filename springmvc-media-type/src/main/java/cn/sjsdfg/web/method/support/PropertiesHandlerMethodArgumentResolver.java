package cn.sjsdfg.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Properties;

/**
 * {@link java.util.Properties}
 * {@link HandlerMethodArgumentResolver 进行参数解析}
 * Created by Joe on 2019/4/4.
 */
public class PropertiesHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Properties.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
        HttpServletRequest request = servletWebRequest.getRequest();
        // 获取字符编码
        Charset charset = getCharset(request.getContentType());
        // 请求输入字节流
        InputStream inputStream = request.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream, charset);
        Properties properties = new Properties();
        properties.load(reader);
        return properties;
    }

    private Charset getCharset(String contentType) {
        if (Objects.isNull(contentType)) {
            return Charset.defaultCharset();
        }
        MediaType mediaType = MediaType.parseMediaType(contentType);
        if (Objects.isNull(mediaType.getCharset())) {
            return Charset.defaultCharset();
        }
        return mediaType.getCharset();
    }
}
