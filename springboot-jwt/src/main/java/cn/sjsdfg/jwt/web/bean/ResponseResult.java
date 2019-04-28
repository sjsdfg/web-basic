package cn.sjsdfg.jwt.web.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Joe on 2019/4/28.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {
    private String statusCode;
    private String message;
    private T data;
}
