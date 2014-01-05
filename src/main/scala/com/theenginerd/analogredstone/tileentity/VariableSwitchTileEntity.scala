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

package com.theenginerd.analogredstone.tileentity

import net.minecraft.tileentity.TileEntity
import net.minecraft.nbt.NBTTagCompound
import com.theenginerd.analogredstone.network.synchronization._

class VariableSwitchTileEntity extends TileEntity with SynchronizedTile
{
    final val IS_ACTIVE_FIELD: String = "isActive"
    final val POWER_OUTPUT_FIELD: String = "powerOutput"

    val powerOutput: ByteRefCell = ByteRefCell(value = 0)
    var isActive: BooleanRefCell = BooleanRefCell(value = false)

    override def getDescriptionPacket = buildUpdatePacket(Array(powerOutput, isActive))

    def toggleActive() =
        synchronized(isActive)
        {
            isActive := !(~isActive)
        }

    @inline private def clamp(value: Int, min: Int, max: Int): Int =
        if (value > max)
            max
        else if(value < min)
            min
        else
            value

    def lowerPower() =
        synchronized(powerOutput)
        {
            powerOutput := clamp(~powerOutput-1, 0, 15).toByte
        }

    def raisePower() =
        synchronized(powerOutput)
        {
            powerOutput := clamp(~powerOutput + 1, 0, 15).toByte
        }

    override def writeToNBT(tag: NBTTagCompound)
    {
        super.writeToNBT(tag)

        tag.setByte(POWER_OUTPUT_FIELD, ~powerOutput)
        tag.setBoolean(IS_ACTIVE_FIELD, ~isActive)
    }
    
    override def readFromNBT(tag: NBTTagCompound)
    {
        super.readFromNBT(tag)

        powerOutput := tag.getByte(POWER_OUTPUT_FIELD)
        isActive := tag.getBoolean(IS_ACTIVE_FIELD)
    }
}
