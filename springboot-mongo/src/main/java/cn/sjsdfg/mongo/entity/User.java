package cn.sjsdfg.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Created by Joe on 2019/4/28.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@CompoundIndexes({
        @CompoundIndex(name = "age_idx", def = "{'name': 1, 'age': -1}")
})
public class User implements Serializable {
    private static final long serialVersionUID = -8600845799706524009L;
    @Id
    @Indexed
    private String id;
    private String name;
    private int age;
    @Transient
    private String address;
}
