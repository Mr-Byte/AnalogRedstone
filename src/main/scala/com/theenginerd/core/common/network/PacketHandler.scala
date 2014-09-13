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

package com.theenginerd.core.common.network

import cpw.mods.fml.common.network.{FMLOutboundHandler, NetworkRegistry}
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.relauncher.{SideOnly, Side}
import com.theenginerd.core.common.network.synchronization.{SynchronizedHandler, SynchronizationCodec}
import com.theenginerd.core.common.network.synchronization.data.SynchronizedMessage


class PacketHandler(modId: String)
{
    final val CHANNEL_SYNCHRONIZATION = s"$modId|s"

    val synchronizationChannel = NetworkRegistry.INSTANCE.newChannel(CHANNEL_SYNCHRONIZATION, new SynchronizationCodec())

    if(FMLCommonHandler.instance().getSide == Side.CLIENT)
    {
        addClientHandler()
    }

    @SideOnly(Side.CLIENT)
    private def addClientHandler() =
    {
        val clientChannel = synchronizationChannel.get(Side.CLIENT)
        val codec = clientChannel.findChannelHandlerNameForType(classOf[SynchronizationCodec])
        clientChannel.pipeline().addAfter(codec, "ClientHandler", SynchronizedHandler)
    }

    def sendToAllAround(message: SynchronizedMessage, dimension: Int, x: Double, y: Double, z: Double, range: Double) =
    {
        val point = new NetworkRegistry.TargetPoint(dimension, x, y, z, range)

        synchronizationChannel.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT)
        synchronizationChannel.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point)
        synchronizationChannel.get(Side.SERVER).writeAndFlush(message)
    }

    def convertMessageToPacket(message: SynchronizedMessage) =
        synchronizationChannel.get(Side.SERVER).generatePacketFrom(message)
}

object PacketHandler
{
    private var modId: String = ""

    def initialize(modId: String) = this.modId = modId

    lazy val instance: PacketHandler = new PacketHandler(modId)
}
