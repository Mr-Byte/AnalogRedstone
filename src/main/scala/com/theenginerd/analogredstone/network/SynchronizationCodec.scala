/*
 * Copyright 2014 Joshua R. Rodgers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================================
 */

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
