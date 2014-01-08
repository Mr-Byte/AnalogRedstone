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
import cpw.mods.fml.common.FMLLog
import com.google.common.io.{ByteArrayDataOutput, ByteStreams}
import com.theenginerd.analogredstone.network.synchronization.SynchronizationIds
import com.theenginerd.analogredstone.network.PacketHandler

trait SynchronizedTileSerializer
{
    def serializeToPacket(xCoord: Int, yCoord: Int, zCoord: Int, properties: Seq[PropertyCell]): Packet250CustomPayload
}

class DataStreamSynchronizedTileSerializer extends SynchronizedTileSerializer
{

    def serializeToPacket(xCoord: Int, yCoord: Int, zCoord: Int, properties: Seq[PropertyCell]): Packet250CustomPayload =
    {
        val data = serializeToByteArray(xCoord, yCoord, zCoord, properties)
        buildPacket(data)
    }

    def buildPacket(data: Array[Byte]): Packet250CustomPayload =
    {
        val packet250 = new Packet250CustomPayload()
        packet250.channel = PacketHandler.CHANNEL_SYNCHRONIZATION
        packet250.data = data
        packet250.length = data.length
        packet250.isChunkDataPacket = true
        packet250
    }

    private def serializeToByteArray(xCoord: Int, yCoord: Int, zCoord: Int, properties: Seq[PropertyCell]): Array[Byte] =
    {
        val output: ByteArrayDataOutput = ByteStreams.newDataOutput()

        serializeHeader(output, xCoord, yCoord, zCoord)
        serializeBody(properties, output)

        output.toByteArray
    }

    private def serializeHeader(output: ByteArrayDataOutput, xCoord: Int, yCoord: Int, zCoord: Int)
    {
        output.writeByte(SynchronizationIds.TILE_SYNCHRONIZATION_ID)
        output.writeInt(xCoord)
        output.writeInt(yCoord)
        output.writeInt(zCoord)
    }

    private def serializeBody(properties: Seq[PropertyCell], output: ByteArrayDataOutput)
    {
        import com.theenginerd.analogredstone.network.data.PropertyTypeIds._

        for (property <- properties)
        {
            output.write(property.id)

            ~property match
            {
                case value: Boolean =>
                    output.writeByte(BOOLEAN_ID)
                    output.writeBoolean(value)

                case value: Byte =>
                    output.writeByte(BYTE_ID)
                    output.writeByte(value)

                case value: Short =>
                    output.writeByte(SHORT_ID)
                    output.writeShort(value)

                case value: Int =>
                    output.writeByte(INT_ID)
                    output.writeInt(value)

                case value: Float =>
                    output.writeByte(FLOAT_ID)
                    output.writeFloat(value)

                case unexpected =>
                    val typ = unexpected.getClass
                    FMLLog warning s"Unexpected serialization type found: $typ."
            }
        }
    }
}
