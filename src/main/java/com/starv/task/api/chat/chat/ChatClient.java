package com.starv.task.api.chat.chat;

import com.starv.task.api.chat.protocol.SFPDecoder;
import com.starv.task.api.chat.protocol.SFPEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 * @author Alan
 * @createTime 2018年3月27日 下午9:27:57
 * 
 */
public class ChatClient {
	public static void main(String[] args) throws Exception {
		int port=8088; //服务端默认端口
		new ChatClient().connect(port, "localhost");
	}
	public void connect(int port, String host) throws Exception{
		//配置客户端NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap();
			bs.group(group)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					//创建NIOSocketChannel成功后，在进行初始化时，将它的ChannelHandler设置到ChannelPipeline中，用于处理网络IO事件
					protected void initChannel(SocketChannel arg0) throws Exception {
						
						arg0.pipeline().addLast(new SFPDecoder());
						arg0.pipeline().addLast(new SFPEncoder());
						arg0.pipeline().addLast(new ChatClientHandler());
					}
				});
			//发起异步连接操作
			ChannelFuture cf = bs.connect(host, port).sync();
			//等待客户端链路关闭
			cf.channel().closeFuture().sync();
		} finally {
			//优雅退出，释放NIO线程组
			group.shutdownGracefully();
		}
	}
}
