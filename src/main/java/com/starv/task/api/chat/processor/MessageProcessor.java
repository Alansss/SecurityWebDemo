package com.starv.task.api.chat.processor;

import com.starv.task.api.chat.protocol.MessageCodec;
import com.starv.task.api.chat.protocol.MessageObject;
import com.starv.task.api.chat.protocol.MessageStatus;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Five
 * @createTime 2018年3月25日 下午10:03:52
 * 
 */
@Slf4j
public class MessageProcessor {

	//用于记录/管理所有客户端的Channel
	private static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	private MessageCodec codec = new MessageCodec();
	
	//设置一些Channel的属性
	private AttributeKey<String> nickName = AttributeKey.valueOf("nickName");
	
	public void messageHandler(Channel client, String message){
		if(message == null || "".equals(message.trim())){
			return ;
		}
		log.info("客户端发送的消息："+message);
		
		MessageObject msgObj = codec.decoder(message);
		if(msgObj.getCmd().equals(MessageStatus.LOGIN)){
			//为Channel绑定昵称属性
			client.attr(nickName).set(msgObj.getNickName());
			
			users.add(client); //将用户的channel添加到ChannelGroup中
			//将用户登陆的消息发给所有的其他用户
			for (Channel channel : users) {
				//封装一个System的消息对象
				if(channel == client){
					msgObj = new MessageObject(MessageStatus.SYSTEM, System.currentTimeMillis(),
							msgObj.getNickName(), "已经与服务器建立连接", users.size());
				}else{
					msgObj = new MessageObject(MessageStatus.SYSTEM, System.currentTimeMillis(),
							msgObj.getNickName(), msgObj.getNickName()+"加入了聊天室", users.size());
				}
				//将消息发送给所有客户端
				channel.writeAndFlush(new TextWebSocketFrame(codec.encoder(msgObj)));
			}
		} else if(msgObj.getCmd().equals(MessageStatus.CHAT)) {
			
			for (Channel channel : users) {
				if(channel == client){
					//发送给自己
					//msgObj.setNickName("SELF");
					msgObj.setNickName("SELF-" + client.attr(nickName).get());
				}else{
					msgObj.setNickName(client.attr(nickName).get());
				}
				//重新编码
				String content = codec.encoder(msgObj);
				channel.writeAndFlush(new TextWebSocketFrame(content));
			}
		}
	}
	
	public void messageHandler(Channel client, MessageObject message){
		messageHandler(client, codec.encoder(message));
	}
	
	public void logout(Channel client){
		//封装一个登出指令发送给客户端
		users.remove(client);
		//获得客户的绑定的昵称
		String userName = client.attr(nickName).get();
		if(userName!=null && !userName.equals("")){
			MessageObject messageObject = new MessageObject(MessageStatus.SYSTEM, System.currentTimeMillis(), null, userName+"退出了聊天室", users.size());
			String content = codec.encoder(messageObject);
			for (Channel channel : users) {
				channel.writeAndFlush(new TextWebSocketFrame(content));
			}
		}
	}
}
