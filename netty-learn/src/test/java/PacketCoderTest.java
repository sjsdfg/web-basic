import cn.sjsdfg.netty.learn.protocal.packet.AbstractPacket;
import cn.sjsdfg.netty.learn.protocal.packet.LoginRequestPacket;
import cn.sjsdfg.netty.learn.protocal.util.PacketCoder;
import io.netty.buffer.ByteBuf;
import org.jeasy.random.EasyRandom;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Joe
 * @date 2020/1/4
 */
public class PacketCoderTest {

    private EasyRandom easyRandom = new EasyRandom();

    @Test
    public void testEncode() {
        LoginRequestPacket loginRequestPacket = easyRandom.nextObject(LoginRequestPacket.class);
        ByteBuf encode = PacketCoder.encode(loginRequestPacket);
        AbstractPacket decode = PacketCoder.decode(encode);
        Assert.assertEquals(loginRequestPacket, decode);
    }
}
