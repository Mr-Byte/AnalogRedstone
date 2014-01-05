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

package com.theenginerd.analogredstone.network.synchronization

import net.minecraft.tileentity.TileEntity
import cpw.mods.fml.common.network.PacketDispatcher
import net.minecraft.network.packet.Packet250CustomPayload
import java.io.{DataInputStream, DataOutputStream, ByteArrayOutputStream}
import com.theenginerd.analogredstone
import scala.language.implicitConversions

trait SynchronizedTile extends MappedRefCells
{
    self: TileEntity =>
    def synchronized(properties: RefCell*)(handler: => Unit = {}): Unit =
    {
        handler

        if(!worldObj.isRemote)
        {
            PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 64, worldObj.provider.dimensionId, buildUpdatePacket(properties))
        }
    }

    def handleUpdate(dataStream: DataInputStream)
    {
        while(dataStream.available() > 0)
        {
            val propertyId = dataStream.readByte()
            val typeId = dataStream.readByte()

            for(property <- readPropertyByType(typeId, dataStream))
            {
                val refProperty = refCellMap(propertyId)

                refProperty := (~property).asInstanceOf[refProperty.Value]
            }
        }
    }

    def readPropertyByType(typeId: Byte, dataStream: DataInputStream): Option[RefCell] =
    {
        typeId match
        {
            case 0 => Some(BooleanRefCell(dataStream.readBoolean()))
            case 1 => Some(ByteRefCell(dataStream.readByte()))
            case 2 => Some(ShortRefCell(dataStream.readShort()))
            case 3 => Some(IntRefCell(dataStream.readInt()))
            case 4 => Some(FloatRefCell(dataStream.readFloat()))
            case _ => None
        }
    }

    protected def buildUpdatePacket(properties: Seq[RefCell]): Packet250CustomPayload =
    {
        val byteStream = new ByteArrayOutputStream()
        val dataStream = new DataOutputStream(byteStream)

        dataStream.writeShort(actionIds.TILE_SYNCHRONIZATION_ACTION)
        dataStream.writeInt(xCoord)
        dataStream.writeInt(yCoord)
        dataStream.writeInt(zCoord)

        properties.foreach(writeProperty(dataStream, _))

        val data = byteStream.toByteArray
        dataStream.close()
        byteStream.close()

        val packet250 = new Packet250CustomPayload()
        packet250.channel = analogredstone.MOD_ID
        packet250.data = data
        packet250.length = data.length
        packet250.isChunkDataPacket = true

        packet250
    }

    protected def writeProperty(dataStream: DataOutputStream, property: RefCell) =
    {
        property match
        {
            case BooleanRefCell(value) =>
                dataStream.writeByte(property.id)
                dataStream.writeByte(0: Byte)
                dataStream.writeBoolean(value)

            case ByteRefCell(value) =>
                dataStream.writeByte(property.id)
                dataStream.writeByte(1: Byte)
                dataStream.writeByte(value)

            case ShortRefCell(value) =>
                dataStream.writeByte(property.id)
                dataStream.writeByte(2: Byte)
                dataStream.writeShort(value)

            case IntRefCell(value) =>
                dataStream.writeByte(property.id)
                dataStream.writeByte(3: Byte)
                dataStream.writeInt(value)

            case FloatRefCell(value) =>
                dataStream.writeByte(property.id)
                dataStream.writeByte(4: Byte)
                dataStream.writeFloat(value)
        }
    }
}
