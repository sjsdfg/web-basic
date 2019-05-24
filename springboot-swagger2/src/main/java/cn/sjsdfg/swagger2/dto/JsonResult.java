package cn.sjsdfg.swagger2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Joe on 2019/5/24.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonResult {
    private String status;
    private Object result;
}
