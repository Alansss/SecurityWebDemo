package com.starv.task.api.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

public class SFPDecoder extends ByteToMessageDecoder {
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		try {
			// 实现反序列化
			int length = in.readableBytes();
			byte[] array = new byte[length];
			String content = new String(array, in.readerIndex(), length);
			
			if(content != null && !"".equals(content.trim())){
				//判断是否是我的自定义协议
				if(!MessageStatus.isSFP(content)){
					ctx.channel().pipeline().remove(this);
					return;
				}
			}

			in.getBytes(in.readerIndex(), array, 0, length);
			out.add(new MessagePack().read(array, MessageObject.class));
			in.clear();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ctx.channel().pipeline().remove(this);
		}
	}
	
}
