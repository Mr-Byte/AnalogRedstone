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

package com.theenginerd.analogredstone.network.data.serialization

import net.minecraft.network.packet.Packet250CustomPayload
import com.theenginerd.analogredstone.network.data.PropertyCell
import java.io.{DataOutputStream, ByteArrayOutputStream}
import com.theenginerd.analogredstone
import com.theenginerd.analogredstone.network.synchronization.packetIds
import cpw.mods.fml.common.FMLLog

trait SynchronizedTileSerializer
{
    def serializeToPacket(xCoord: Int, yCoord: Int, zCoord: Int, properties: Seq[PropertyCell]): Packet250CustomPayload
}

class DataStreamSynchronizedTileSerializer extends SynchronizedTileSerializer
{
    final val BOOLEAN_ID = 0
    final val BYTE_ID = 1
    final val SHORT_ID = 2
    final val INT_ID = 3
    final val FLOAT_ID = 4

    def serializeToPacket(xCoord: Int, yCoord: Int, zCoord: Int, properties: Seq[PropertyCell]): Packet250CustomPayload =
    {
        val data = serializeToByteArray(xCoord, yCoord, zCoord, properties)
        buildPacket(data)
    }

    def buildPacket(data: Array[Byte]): Packet250CustomPayload =
    {
        val packet250 = new Packet250CustomPayload()
        packet250.channel = analogredstone.MOD_ID
        packet250.data = data
        packet250.length = data.length
        packet250.isChunkDataPacket = true
        packet250
    }

    private def serializeToByteArray(xCoord: Int, yCoord: Int, zCoord: Int, properties: Seq[PropertyCell]): Array[Byte] =
    {
        val byteStream = new ByteArrayOutputStream()
        val dataStream = new DataOutputStream(byteStream)

        try
        {
            serializeHeader(dataStream, xCoord, yCoord, zCoord)
            serializeBody(properties, dataStream)

            byteStream.toByteArray
        }
        finally
        {
            dataStream.close()
            byteStream.close()
        }

    }

    private def serializeHeader(dataStream: DataOutputStream, xCoord: Int, yCoord: Int, zCoord: Int)
    {
        dataStream.writeByte(packetIds.TILE_SYNCHRONIZATION_PACKET)
        dataStream.writeInt(xCoord)
        dataStream.writeInt(yCoord)
        dataStream.writeInt(zCoord)
    }

    private def serializeBody(properties: Seq[PropertyCell], dataStream: DataOutputStream)
    {
        for (property <- properties)
        {
            dataStream.write(property.id)

            ~property match
            {
                case value: Boolean =>
                    dataStream.writeByte(BOOLEAN_ID)
                    dataStream.writeBoolean(value)

                case value: Byte =>
                    dataStream.writeByte(BYTE_ID)
                    dataStream.writeByte(value)

                case value: Short =>
                    dataStream.writeByte(SHORT_ID)
                    dataStream.writeShort(value)

                case value: Int =>
                    dataStream.writeByte(INT_ID)
                    dataStream.writeInt(value)

                case value: Float =>
                    dataStream.writeByte(FLOAT_ID)
                    dataStream.writeFloat(value)

                case unexpected =>
                    val typ = unexpected.getClass
                    FMLLog warning s"Unexpected serialization type found: $typ."
            }
        }
    }
}
