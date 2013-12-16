/*
 * Copyright 2013 Joshua R. Rodgers
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

package com.theenginerd.analogredstone.network.packet

import java.io.{ByteArrayInputStream, DataInputStream, DataOutputStream, ByteArrayOutputStream}
import net.minecraft.network.INetworkManager
import cpw.mods.fml.common.network.Player
import net.minecraft.entity.player.EntityPlayer
import com.theenginerd.analogredstone.MOD_ID
import com.theenginerd.analogredstone.utility.getBlockTileEntity
import com.theenginerd.analogredstone.tileentity.VariableSwitchTileEntity
import net.minecraft.network.packet.{Packet250CustomPayload, Packet}

abstract class AnalogRedstonePacket
{
    def toByteArray: Array[Byte]
    def packetId: Short

    def writePacketId(dataStream: DataOutputStream)
    {
        dataStream.writeShort(packetId)
    }

    def toPacket: Packet =
    {
        val data = toByteArray
        val packet250 = new Packet250CustomPayload()
        packet250.channel = MOD_ID
        packet250.data = data
        packet250.length = data.length
        packet250.isChunkDataPacket = true

        packet250
    }
}

abstract class TileUpdatePacket(val position: (Int, Int, Int)) extends AnalogRedstonePacket
{
    def writePosition(dataStream: DataOutputStream)
    {
        val (x, y, z) = position
        dataStream.writeInt(x)
        dataStream.writeInt(y)
        dataStream.writeInt(z)
    }

    def handleTileUpdate(networkManager: INetworkManager, player: Player)
}

case class VariableSwitchUpdatePacket(isActive: Boolean, powerOutput: Int)(implicit position: (Int, Int, Int)) extends TileUpdatePacket(position)
{
    def packetId = 1

    def toByteArray: Array[Byte] =
    {
        val byteStream = new ByteArrayOutputStream()
        val dataStream = new DataOutputStream(byteStream)

        writePacketId(dataStream)
        writePosition(dataStream)
        dataStream.writeBoolean(isActive)
        dataStream.writeByte(powerOutput)

        byteStream.toByteArray
    }

    def handleTileUpdate(networkManager: INetworkManager, player: Player)
    {
        for(tileEntity <- getBlockTileEntity[VariableSwitchTileEntity](player.asInstanceOf[EntityPlayer].worldObj, position))
        {
            tileEntity.isActive = isActive
            tileEntity.powerOutput = powerOutput
        }
    }
}

object VariableSwitchUpdatePacket
{
    def apply(dataInput: DataInputStream): VariableSwitchUpdatePacket =
    {
        implicit val position = (dataInput.readInt, dataInput.readInt, dataInput.readInt)
        val isActive = dataInput.readBoolean()
        val powerOutput = dataInput.readByte()

        VariableSwitchUpdatePacket(isActive, powerOutput)
    }
}

object AnalogRedstonePacket
{
    def apply(data: Array[Byte]): Option[AnalogRedstonePacket] =
    {
        val byteStream = new ByteArrayInputStream(data)
        val dataInput = new DataInputStream(byteStream)

        dataInput.readShort() match
        {
            case 1 => Some(VariableSwitchUpdatePacket(dataInput))
            case _ => None
        }
    }
}
