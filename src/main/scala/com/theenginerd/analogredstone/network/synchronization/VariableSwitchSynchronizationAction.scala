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

import java.io.{DataOutputStream, ByteArrayOutputStream, DataInputStream}

case class VariableSwitchSynchronizationAction(isActive: Boolean, powerOutput: Int)(implicit position: (Int, Int, Int))
    extends TileSynchronizationAction(position)
{
    val actionId = actionIds.VARIABLE_SWITCH_SYNCHRONIZATION_ACTION

    def toByteArray: Array[Byte] =
    {
        val byteStream = new ByteArrayOutputStream()
        val dataStream = new DataOutputStream(byteStream)

        writeActionId(dataStream)
        writePosition(dataStream)
        dataStream.writeBoolean(isActive)
        dataStream.writeByte(powerOutput)

        byteStream.toByteArray
    }
}

object VariableSwitchSynchronizationAction
{
    def apply(dataInput: DataInputStream): VariableSwitchSynchronizationAction =
    {
        implicit val position = (dataInput.readInt, dataInput.readInt, dataInput.readInt)
        val isActive = dataInput.readBoolean()
        val powerOutput = dataInput.readByte()

        VariableSwitchSynchronizationAction(isActive, powerOutput)
    }
}
