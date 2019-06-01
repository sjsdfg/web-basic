package cn.sjsdfg.common.util;

import org.junit.Test;

import static cn.sjsdfg.common.util.MD5Util.encrypt;

/**
 * Created by Joe on 2019/6/1.
 */
public class MD5UtilTest {
    @Test
    public void test() {
        System.out.println(encrypt("123456"));
    }
}
