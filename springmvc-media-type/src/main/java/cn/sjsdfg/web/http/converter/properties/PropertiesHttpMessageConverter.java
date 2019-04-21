package cn.sjsdfg.web.http.converter.properties;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by Joe on 2019/4/3.
 * 参考 {@link MappingJackson2HttpMessageConverter}
 * {@link Properties} {@link AbstractGenericHttpMessageConverter} 实现
 */
public class PropertiesHttpMessageConverter extends AbstractGenericHttpMessageConverter<Properties> {

    public PropertiesHttpMessageConverter() {
        // 设置支持的 MediaType
        super(new MediaType("text", "properties"));
    }

    @Override
    protected void writeInternal(Properties properties, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        // 获取请求头中的字符编码
        Charset charset = getCharset(outputMessage.getHeaders());
        // Properties -> String
        // 获取字节输出流
        OutputStream outputStream = outputMessage.getBody();
        // 字节 -> 字符输出流
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, charset);
        properties.store(outputStreamWriter, null);
    }

    @Override
    protected Properties readInternal(Class<? extends Properties> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        // 获取请求头中的编码
        Charset charset = getCharset(inputMessage.getHeaders());
        // 获取 Body，并将字节流转换为字符流
        InputStream inputStream = inputMessage.getBody();
        InputStreamReader reader = new InputStreamReader(inputStream, charset);
        // 加载字符流成为 Properties 对象
        Properties properties = new Properties();
        properties.load(reader);
        return properties;
    }

    @Override
    public Properties read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return read(null, inputMessage);
    }

    private Charset getCharset(HttpHeaders httpHeaders) {
        // 获取请求头中的 Content-Type
        MediaType contentType = httpHeaders.getContentType();
        if (Objects.nonNull(contentType) && Objects.nonNull(contentType.getCharset())) {
            // 从请求头中获取编码，如果编码为空则默认 UTF-8
            return contentType.getCharset();
        } else {
           return Charset.defaultCharset();
        }
    }
}
