package com.starv.task.api.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

public class SFPEncoder extends MessageToByteEncoder<MessageObject> {

	@Override
	protected void encode(ChannelHandlerContext ctx, MessageObject msg, ByteBuf out) throws Exception {
		// TODO Auto-generated method stub
		out.writeBytes(new MessagePack().write(msg));
	}


}
