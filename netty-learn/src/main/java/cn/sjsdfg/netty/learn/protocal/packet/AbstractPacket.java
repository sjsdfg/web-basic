package cn.sjsdfg.netty.learn.protocal.packet;

import lombok.Data;

/**
 * @author Joe
 * @date 2020/1/4
 */
@Data
public abstract class AbstractPacket {
    /**
     * 协议版本号
     */
    private byte version;

    /**
     * 指令
     *
     * @return
     */
    public abstract byte getCommand();
}
