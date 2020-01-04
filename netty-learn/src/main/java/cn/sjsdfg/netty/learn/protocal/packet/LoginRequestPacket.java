package cn.sjsdfg.netty.learn.protocal.packet;

import cn.sjsdfg.netty.learn.protocal.enums.CommandEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joe
 * @date 2020/1/4
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginRequestPacket extends AbstractPacket {

    private int userId;

    private String username;

    private String password;

    @Override
    public byte getCommand() {
        return CommandEnum.LOGIN_REQUEST.getVersion();
    }
}
