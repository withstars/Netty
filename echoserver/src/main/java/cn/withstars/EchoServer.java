package cn.withstars;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: withstars
 * Date: 2018-06-09
 * Time: 11:00
 * Mail: withstars@126.com
 */
public class EchoServer {
    // 服务器绑定端口
    private final int port;

    public EchoServer(int port){
        this.port = port;
    }

    /**
     * 引导服务器
     *
     * 创建一个ServerBootstrap 的实例以引导和绑定服务器
     * 创建并分配一个NioEventLoopGroup 实例以进行事件的处理，如接受新连接以及读写数据
     * 指定服务器绑定的本地的InetSocketAddress
     * 使用一个EchoServerHandler的实例初始化每一个新的Channel
     * 调用ServerBootstrap.bind()方法以绑定服务器
     *
     * @throws Exception
     */
    public void start() throws Exception{
       final EchoServerHander serverHander = new EchoServerHander();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class) // 指定所使用的NIO传输Chanel
                    .localAddress(new InetSocketAddress(port)) // 绑定本地端口
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 新连接创建时，新的子Channel将会被创建，而ChannelInitializer会把Handler添加到该channel的pipeline中

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHander);//EchoServerHandler被标注为@Shareable,所以总是可以使用同样的实例
                        }
                    });
            ChannelFuture f = b.bind().sync();// 异步地绑定服务器 调用sync()方法阻塞等待知道绑定完成
            f.channel().closeFuture().sync();// 获取Channel的CloseFuture,并且阻塞当前线程知道它完成
        }finally {
            group.shutdownGracefully().sync(); // 关闭EventLoopGroup,释放所有资源
        }

    }

    public static void main(String[] args) throws Exception{
        /*
        if (args.length != 1){
            System.out.println(
                    "Usage: "+EchoServer.class.getSimpleName()+"<port>"
            );
            int port = Integer.parseInt(args[0]); // 设置端口值 (如果端口参数的格式不正确，则抛出一个NumberFormatException)
            */
            new EchoServer(8888).start();   // 调用服务器的start()方法
        }

    }

