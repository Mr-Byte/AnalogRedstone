package com.theenginerd.analogredstone.network

import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec
import com.theenginerd.analogredstone.network.data.synchronization.{SynchronizedTileMessage, SynchronizedMessage}
import io.netty.channel.ChannelHandlerContext
import io.netty.buffer.ByteBuf

class SynchronizationCodec extends FMLIndexedMessageToMessageCodec[SynchronizedMessage]
{
    addDiscriminator(0, classOf[SynchronizedTileMessage])

    def encodeInto(channel: ChannelHandlerContext, message: SynchronizedMessage, target: ByteBuf) =  message.writeToBuffer(target)
    def decodeInto(channel: ChannelHandlerContext, source: ByteBuf, message: SynchronizedMessage) = message.readFromBuffer(source)
}
