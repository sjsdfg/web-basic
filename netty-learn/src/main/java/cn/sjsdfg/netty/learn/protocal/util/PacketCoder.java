package cn.sjsdfg.netty.learn.protocal.util;

import cn.sjsdfg.netty.learn.protocal.enums.CommandEnum;
import cn.sjsdfg.netty.learn.protocal.enums.SerializerAlgorithm;
import cn.sjsdfg.netty.learn.protocal.packet.AbstractPacket;
import cn.sjsdfg.netty.learn.protocal.packet.LoginRequestPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author Joe
 * @date 2020/1/4
 */
public class PacketCoder {
    private static final int MAGIC_NUMBER = 0x12345678;
    private static final Serializer JSON_SERIALIZER = new JsonSerializer();

    public static ByteBuf encode(AbstractPacket packet) {
        if (packet == null) {
            return ByteBufAllocator.DEFAULT.ioBuffer();
        }
        // 1. 创建 ByteBuf 对象
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        // 2. 序列化 Java 对象
        byte[] bytes = JSON_SERIALIZER.serialize(packet);

        // 3. 进行编码
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(JSON_SERIALIZER.getSerializerAlgorithm().getAlgorithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }

    public static AbstractPacket decode(ByteBuf byteBuf) {
        if (byteBuf == null) {
            return null;
        }
        //跳过 magic number
        byteBuf.skipBytes(4);
        // 跳过版本号
        byteBuf.skipBytes(1);
        // 序列化算法标识
        byte serializeAlgorithm = byteBuf.readByte();
        // 指令
        byte command = byteBuf.readByte();
        // 数据包长度
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Class<? extends AbstractPacket> requestType = getRequestType(command);
        Serializer serializer = getSerializer(serializeAlgorithm);
        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }
        return null;
    }

    private static Serializer getSerializer(int serializeAlgorithm) {
        SerializerAlgorithm algorithm = SerializerAlgorithm.parse(serializeAlgorithm);
        if (algorithm == null) {
            return null;
        } else if (SerializerAlgorithm.Json == algorithm) {
            return JSON_SERIALIZER;
        }
        return null;
    }

    private static Class<? extends AbstractPacket> getRequestType(byte command) {
        CommandEnum parse = CommandEnum.parse(command);
        if (parse == null) {
            return null;
        }
        if (parse == CommandEnum.LOGIN_REQUEST) {
            return LoginRequestPacket.class;
        }
        return null;
    }
}
