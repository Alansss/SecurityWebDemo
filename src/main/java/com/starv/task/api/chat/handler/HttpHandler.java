package com.starv.task.api.chat.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 
 * @author Alan
 * @createTime 2018年3月25日 下午8:14:41
 * 
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		//处理客户端的Http请求
		String uri = request.getUri();
		String source = uri.equals("/")?"chat.html":uri;
		//拿到资源文件
		RandomAccessFile file;
		try {
			file = new RandomAccessFile(getResource(source), "r");
		} catch (Exception e) {
			//继续下一次请求
			ctx.fireChannelRead(request.retain());
			return ;
		}
		//将资源响应给客户端
		HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
		//设置响应头的ContentType
		String contentType = "text/html";
		if(uri.endsWith(".js")){
			contentType = "text/javascript";
		}else if(uri.endsWith(".css")){
			contentType = "text/css";
		}else if(uri.toLowerCase().matches("(jpg|png|gif|ico)$")){
			String type = uri.substring(uri.lastIndexOf("."));
			contentType = "image/"+type;
		}
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType+"; charset=utf-8");
		boolean keepAlive = HttpHeaders.isKeepAlive(request);
		if(keepAlive){
			//如果请求是一个长连接
			response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
			response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}
		//向客户端响应消息头
		ctx.write(response);
		//向客户端响应消息体
		ctx.write(new ChunkedNioFile(file.getChannel()));
		//响应结束添加Http响应结束标记
		ChannelFuture writeAndFlush = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		if(!keepAlive){
			writeAndFlush.addListener(ChannelFutureListener.CLOSE);
		}
		//收尾
		file.close();
	}

	private String getResource(String source) throws URISyntaxException {
		//class文件的地址
		URL location = HttpHandler.class.getProtectionDomain().getCodeSource().getLocation();
		String webroot = "WEB-INF/templates";
		String path = location.toURI()+webroot+"/"+source;
		path = path.replace("file:", "");
		return path;
	}

}
