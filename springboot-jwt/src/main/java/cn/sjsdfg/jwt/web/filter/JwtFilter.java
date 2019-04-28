package cn.sjsdfg.jwt.web.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by Joe on 2019/4/28.
 * 判断前端所传递的 JWT Token 是否失效
 */
public class JwtFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 向下强制转换数据类型
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        // 获取请求头中的 authorization
        String authHeader = servletRequest.getHeader("authorization");

        // If the Http request is OPTIONS then just return the status code 200
        // which is HttpServletResponse.SC_OK in this code
        if ("OPTIONS".equals(servletRequest.getMethod())) {
            // servletRequest.getMethod() 获取前端请求方式
            // HTTP请求方法 8 种方法：OPTIONS、GET、HEAD、POST、PUT、DELETE、TRACE和CONNECT
            // OPTIONS方法是用于请求获得由Request-URI标识的资源在请求/响应的通信过程中可以使用的功能选项。
            servletResponse.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
        } else {
            // 排除请求为 OPTIONS，其他请求应该检查 JWT 值
            if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")) {
                throw new ServletException("Missing or invalid Authorization header");
            }
            // 接下来获取 JWT TOKEN
            String token = authHeader.substring(7);
            try {
                // Use JWT parser to check if the signature is valid with the Key "secretkey"
                final Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(token).getBody();
                // Add the claim to request header
                servletRequest.setAttribute("claims", claims);
            } catch (final SignatureException e) {
                throw new ServletException("Invalid token");
            }

            chain.doFilter(request, response);
        }
    }
}
