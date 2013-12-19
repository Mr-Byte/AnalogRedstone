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

import java.io.{ByteArrayInputStream, DataInputStream, DataOutputStream}
import com.theenginerd.analogredstone
import net.minecraft.network.packet.{Packet250CustomPayload, Packet}

abstract class SynchronizationAction(private val isChunkDataPacket: Boolean)
{
    def toByteArray: Array[Byte]
    val actionId: Short

    def writeActionId(dataStream: DataOutputStream)
    {
        dataStream.writeShort(actionId)
    }

    def toPacket: Packet =
    {
        val data = toByteArray
        val packet250 = new Packet250CustomPayload()

        packet250.channel = analogredstone.MOD_ID
        packet250.data = data
        packet250.length = data.length
        packet250.isChunkDataPacket = isChunkDataPacket

        packet250
    }
}

object SynchronizationAction
{
    val actions: Map[Int, (DataInputStream) => SynchronizationAction] =
        Map((actionIds.VARIABLE_SWITCH_SYNCHRONIZATION_ACTION, VariableSwitchSynchronizationAction(_)))

    def apply(data: Array[Byte]): Option[SynchronizationAction] =
    {
        val byteStream = new ByteArrayInputStream(data)
        val dataInput = new DataInputStream(byteStream)

        actions.get(dataInput.readShort()).map(_(dataInput))
    }
}
