package com.starv.task.api.chat;

import com.starv.task.api.chat.handler.HttpHandler;
import com.starv.task.api.chat.handler.SFPHandler;
import com.starv.task.api.chat.handler.WebSocketHandler;
import com.starv.task.api.chat.protocol.SFPDecoder;
import com.starv.task.api.chat.protocol.SFPEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Alan
 * @createTime 2018年3月25日 下午8:05:54
 * 
 */
@Slf4j
public class ChatServer {

	public static void main(String[] args) throws Exception {
		int port=8088; //服务端默认端口
		new ChatServer().bind(port);
	}
	
	public void bind(int port) throws Exception{
        log.info("开始启动chat服务器。");
		//1用于服务端接受客户端的连接
		EventLoopGroup acceptorGroup = new NioEventLoopGroup();
		//2用于进行SocketChannel的网络读写
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			//Netty用于启动NIO服务器的辅助启动类
			ServerBootstrap sb = new ServerBootstrap();
			//将两个NIO线程组传入辅助启动类中
			sb.group(acceptorGroup, workerGroup)
				//设置创建的Channel为NioServerSocketChannel类型
				.channel(NioServerSocketChannel.class)
				//配置NioServerSocketChannel的TCP参数
				.option(ChannelOption.SO_BACKLOG, 1024)
				//设置绑定IO事件的处理类
				.childHandler(new ChannelInitializer<SocketChannel>() {
					//创建NIOSocketChannel成功后，在进行初始化时，将它的ChannelHandler设置到ChannelPipeline中，用于处理网络IO事件
					@Override
					protected void initChannel(SocketChannel arg0) throws Exception {


						ChannelPipeline pipeline = arg0.pipeline();
						pipeline.addLast(new SFPDecoder());
						pipeline.addLast(new SFPEncoder());
						pipeline.addLast(new SFPHandler());


						//支持Http协议
						//Http请求处理的编解码器
						pipeline.addLast(new HttpServerCodec());
						//用于将HTTP请求进行封装为FullHttpRequest对象
						pipeline.addLast(new HttpObjectAggregator(1024*64));
						//处理文件流
						pipeline.addLast(new ChunkedWriteHandler());
						//Http请求的具体处理对象
						pipeline.addLast(new HttpHandler());

						//支持WebSocket协议
						pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
						pipeline.addLast(new WebSocketHandler());
					}
				});
			//绑定端口，同步等待成功（sync()：同步阻塞方法，等待bind操作完成才继续）
			//ChannelFuture主要用于异步操作的通知回调
			ChannelFuture cf = sb.bind(port).sync();
			log.info("chat服务端启动在8088端口。");
			//等待服务端监听端口关闭
			cf.channel().closeFuture().sync();
		} finally {
			//优雅退出，释放线程池资源
			acceptorGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
