package cn.withstars;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: withstars
 * Date: 2018-06-09
 * Time: 10:36
 * Mail: withstars@126.com
 */

/**
 * 本类实现服务器核心业务逻辑
 * 因为服务器需要响应传入的消息，所以需要实现ChannelInboundHandler接口来定义响应入站事件的方法
 * 因为本实例只需用到少量的这些方法，所以继承 ChannelInboundHandlerAdapter 就够了
 */
@ChannelHandler.Sharable
public class EchoServerHander extends ChannelInboundHandlerAdapter {

    /**
     * 对于每个传入的消息都要调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf)msg;
        System.out.println("Server received:"+in.toString(CharsetUtil.UTF_8));
        ctx.write(in); //将接收到的消息写给发送者，而不冲刷出战消息
    }

    /**
     * 通知ChannelInboundHandler最后一次channelRead()的调用是当前批量读取中的最后一条消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将消息冲刷到远程节点 并且关闭该Channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 在读取操作期间，有异常抛出时会调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(); // 打印异常
        ctx.close(); // 关闭该Channel
    }
}
