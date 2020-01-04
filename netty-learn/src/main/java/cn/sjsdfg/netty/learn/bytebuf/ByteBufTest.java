package cn.sjsdfg.netty.learn.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Joe
 * @date 2019/12/21
 */
@Slf4j
public class ByteBufTest {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);
        log.info("allocate ByteBuf(9, 100) {}", buffer);

        // write 方法改变写指针，写完之后写指针未到 capacity 的时候，buffer 仍然可写
        buffer.writeBytes(new byte[]{1, 2, 3, 4});
        log.info("writeBytes(1,2,3,4) {}", buffer);

        // write 方法改变写指针，写完之后写指针未到 capacity 的时候，buffer 仍然可写, 写完 int 类型之后，写指针增加4
        buffer.writeInt(12);
        log.info("writeInt(12) {}", buffer);

        // write 方法改变写指针, 写完之后写指针等于 capacity 的时候，buffer 不可写
        log.info("byteBuf is writable {}", buffer.isWritable());
        buffer.writeBytes(new byte[]{5});
        log.info("writeBytes(5), {}", buffer);
        log.info("byteBuf is writable {}", buffer.isWritable());

        // write 方法改变写指针，写的时候发现 buffer 不可写则开始扩容，扩容之后 capacity 随即改变
        buffer.writeBytes(new byte[]{6});
        log.info("writeBytes(6) {}", buffer);
    }
}
