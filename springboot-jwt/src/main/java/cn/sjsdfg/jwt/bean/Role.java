package cn.sjsdfg.jwt.bean;

/**
 * Created by Joe on 2019/4/28.
 */
public enum Role {
    ADMIN, MEMBER;

    public String authority() {
        return "ROLE_" + this.name();
    }

    @Override
    public String toString() {
        return this.name();
    }
}
