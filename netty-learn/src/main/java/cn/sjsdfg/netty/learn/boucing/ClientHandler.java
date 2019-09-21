package cn.sjsdfg.netty.learn.boucing;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author Joe
 * @date 2019/9/21
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(new Date() + ": 客户端读到数据 -> " + byteBuf.toString(StandardCharsets.UTF_8));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client Read Message -> " + new Date());
        // 1.获取数据
        ByteBuf buffer = getByteBuf(ctx);
        // 2.写数据
        ctx.channel().writeAndFlush(buffer);
    }

    private ByteBuf getByteBuf(ChannelHandlerContext context) {
        byte[] bytes = "netty learn".getBytes(StandardCharsets.UTF_8);
        ByteBuf byteBuf = context.alloc().buffer();
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }
}
