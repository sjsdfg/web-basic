package cn.sjsdfg.redis.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Joe on 2019/5/6.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 7150103517017430113L;
    private String id;
    private String username;
    private String password;
}
