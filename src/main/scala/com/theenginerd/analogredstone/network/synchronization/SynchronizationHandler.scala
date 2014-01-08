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

package com.theenginerd.analogredstone.network.synchronization

import net.minecraft.network.packet.Packet250CustomPayload
import net.minecraft.entity.player.EntityPlayer
import cpw.mods.fml.common.FMLLog
import java.io.{DataInputStream, ByteArrayInputStream}
import com.theenginerd.analogredstone.network.data.PropertyTypeIds

object SynchronizationHandler
{
    def handlePacket(packet: Packet250CustomPayload, player: EntityPlayer) =
    {
        packet.data match
        {
            case Array(SynchronizationIds.TILE_SYNCHRONIZATION_ID, _*) =>
                handleTileSynchronizationPacket(packet, player)

            case Array(id, _*) =>
                FMLLog warning s"Received synchronization packet with unexpected id of $id."

            case Array() =>
                FMLLog warning "Received empty synchronization packet."
        }
    }

    private def handleTileSynchronizationPacket(packet: Packet250CustomPayload, player: EntityPlayer) =
    {
        val byteStream = new ByteArrayInputStream(packet.data, 1, packet.data.length)
        val input = new DataInputStream(byteStream)
        
        try
        {
            val position = (input.readInt(), input.readInt(), input.readInt())
            for(tile <- getSynchronizedTile(player.worldObj, position))
            {
                while(input.available() > 0)
                {
                    for(property <- tile.getPropertyById(input.readByte()))
                    {
                        property := readValue(input).asInstanceOf[property.Value]
                    }
                }
            }
        }
        finally
        {
            input.close()
            byteStream.close()
        }
    }

    def readValue(input: DataInputStream): AnyVal =
    {
        import PropertyTypeIds._

        input.readByte() match
        {
            case BOOLEAN_ID =>
                input.readBoolean()

            case BYTE_ID =>
                input.readByte()

            case SHORT_ID =>
                input.readShort()

            case INT_ID =>
                input.readInt()

            case FLOAT_ID =>
                input.readFloat()
        }
    }
}
