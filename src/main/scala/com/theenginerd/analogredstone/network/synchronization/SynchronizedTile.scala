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
import com.theenginerd.analogredstone.network.data.MappedProperties
import com.theenginerd.analogredstone.network.data.serialization.{SynchronizedTileSerializer, DataStreamSynchronizedTileSerializer}

trait SynchronizedTile extends MappedProperties
{
    self: TileEntity =>

    final private val serializer: SynchronizedTileSerializer = new DataStreamSynchronizedTileSerializer

    def synchronized(properties: MappedPropertyCell*)(handler: => Unit = {}): Unit =
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
                for(refProperty <- getPropertyById(propertyId))
                {
                    refProperty := (~property).asInstanceOf[refProperty.Value]
                }
            }
        }
    }

    def readPropertyByType(typeId: Byte, dataStream: DataInputStream): Option[MappedPropertyCell] =
    {
        typeId match
        {
            case 0 => Some(BooleanPropertyCell(dataStream.readBoolean()))
            case 1 => Some(BytePropertyCell(dataStream.readByte()))
            case 2 => Some(ShortPropertyCell(dataStream.readShort()))
            case 3 => Some(IntPropertyCell(dataStream.readInt()))
            case 4 => Some(FloatPropertyCell(dataStream.readFloat()))
            case _ => None
        }
    }

    protected def buildUpdatePacket(properties: Seq[MappedPropertyCell]): Packet250CustomPayload =
        serializer.serializeToPacket(xCoord, yCoord, zCoord, properties)
}
