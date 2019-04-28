package cn.sjsdfg.jwt.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Joe on 2019/4/28.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "persons")
public class Person implements Serializable {
    private static final long serialVersionUID = -6201512717542320408L;
    @Id
    private String id;
    private String username;
    private String password;
    private List<Role> roles;
}
