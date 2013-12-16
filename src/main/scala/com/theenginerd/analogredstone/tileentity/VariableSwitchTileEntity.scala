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
import com.theenginerd.analogredstone.network.packet.VariableSwitchUpdatePacket
import net.minecraft.network.packet.Packet
import cpw.mods.fml.common.network.PacketDispatcher

class VariableSwitchTileEntity extends TileEntity
{
    final val IS_ACTIVE_FIELD: String = "isActive"
    final val POWER_OUTPUT_FIELD: String = "powerOutput"

    var powerOutput: Int = 0
    var isActive: Boolean = false

    private def getPacket(): Packet =
    {
        VariableSwitchUpdatePacket(isActive, powerOutput)(xCoord, yCoord, zCoord).toPacket
    }

    def sendTileUpdate = PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 64, worldObj.provider.dimensionId, getDescriptionPacket())

    override def getDescriptionPacket() = getPacket()

    def toggleActive() =
    {
        isActive = !isActive
        sendTileUpdate
    }

    @inline private def clamp(value: Int, min: Int, max: Int): Int =
        if (value > max)
            max
        else if(value < min)
            min
        else
            value

    def lowerPower =
    {
        powerOutput = clamp(powerOutput-1, 0, 15)
        sendTileUpdate
    }

    def raisePower =
    {
        powerOutput = clamp(powerOutput+1, 0, 15)
        sendTileUpdate
    }

    override def writeToNBT(tag: NBTTagCompound)
    {
        super.writeToNBT(tag)

        tag.setByte(POWER_OUTPUT_FIELD, powerOutput.toByte)
        tag.setBoolean(IS_ACTIVE_FIELD, isActive)
    }
    
    override def readFromNBT(tag: NBTTagCompound)
    {
        super.readFromNBT(tag)

        powerOutput = tag.getByte(POWER_OUTPUT_FIELD)
        isActive = tag.getBoolean(IS_ACTIVE_FIELD)
    }
}
