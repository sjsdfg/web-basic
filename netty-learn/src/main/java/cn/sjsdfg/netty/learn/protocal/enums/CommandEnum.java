package cn.sjsdfg.netty.learn.protocal.enums;

import lombok.Getter;

/**
 * @author Joe
 * @date 2020/1/4
 */
@Getter
public enum CommandEnum {
    /**
     * 登录请求
     */
    LOGIN_REQUEST(1);

    private byte version;

    CommandEnum(int version) {
        this.version = (byte) version;
    }

    public static CommandEnum parse(byte version) {
        for (CommandEnum commandEnum : CommandEnum.values()) {
            if (commandEnum.getVersion() == version) {
                return commandEnum;
            }
        }
        return null;
    }
}
