package com.starv.task.api.chat.chat;

import com.starv.task.api.chat.protocol.MessageObject;
import com.starv.task.api.chat.protocol.MessageStatus;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * 
 * @author Alan
 * @createTime 2018年3月27日 下午9:29:19
 * 
 */
@Slf4j
public class ChatClientHandler extends ChannelHandlerAdapter {
	private Channel client;
	
	public ChatClientHandler() {
		super();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.client = ctx.channel();
		//登录
		MessageObject message = new MessageObject(MessageStatus.LOGIN, System.currentTimeMillis(), "Derry");
		sendMsg(message);
		log.info("成功连接至服务器，已执行登录动作");
		session();
	}

	private void session() {
		new Thread() {
			
			@Override
			public void run() {
				log.info("Derry,你好，请在控制台输入消息内容");
				Scanner sc = new Scanner(System.in);
				MessageObject message = null;
				do{
					String content = sc.nextLine();
					if("exit".equals(content)){
						//logout
						message = new MessageObject(MessageStatus.LOGOUT, System.currentTimeMillis(), "Derry");
					}else{
						message = new MessageObject(MessageStatus.CHAT, System.currentTimeMillis(), "Derry", content);
					}
				} while(sendMsg(message));
				sc.close();
			}

		}.start();
	}

	private boolean sendMsg(MessageObject msg) {
		this.client.writeAndFlush(msg);
		log.info("消息发送成功，请继续输入：");
		return true;
	}
}
