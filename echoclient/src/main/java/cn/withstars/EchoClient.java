package cn.withstars;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;


/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: withstars
 * Date: 2018-06-09
 * Time: 13:39
 * Mail: withstars@126.com
 */
public class EchoClient {

    private final String host;

    private final int port;

    public EchoClient(String host, int port){
        this.port = port;
        this.host = host;
    }

    /**
     * 创建一个Bootstrap实例
     * 为进行事件处理分配了一个NioEventLoopGroup实例，其中事件处理包括创建新的连接以及处理入站和出站数据
     * 当连接被建立时，一个EchoClientHandler实例会被安装到该Channel的ChannelPipeline中
     * 在一切设置完成后，调用Bootstrap.connect()方法连接到远程节点
     * @throws Exception
     */
    public void start() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();  // 创建Bootstrap
            b.group(group)                  // 指定EventLoopGroup处理客户端事件，需要适用于NIO实现
                    .channel(NioSocketChannel.class)  //适用于NIO传输的Channel类型
                    .remoteAddress(new InetSocketAddress(host, port)) // 设置服务器的InetSocketAddress
                    .handler(new ChannelInitializer<SocketChannel>() {  // 在创建Channel的时候，向ChannelPipeline中添加一个EchoClientHandler实例

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture f = b.connect().sync(); // 连接到远程节点，阻塞等待直到连接完成
            f.channel().closeFuture().sync();   // 阻塞 知道channel关闭
        }finally {
            group.shutdownGracefully().sync();  // 关闭线程池并释放所有资源
        }
    }

    public static void main(String[] args) throws Exception{

        /*
        if (args.length != 2){
            System.err.println(
                    "Usage:"+EchoClient.class.getSimpleName()+"<host><port>"
            );
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        */
        new EchoClient("localhost",8888).start();
    }
}
