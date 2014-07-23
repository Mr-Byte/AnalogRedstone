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

package com.theenginerd.randomredstone.common.blockentity

import net.minecraft.nbt.NBTTagCompound

trait VariableSwitch extends BlockEntity
{
    final val IS_ACTIVE_FIELD: String = "isActive"
    final val POWER_OUTPUT_FIELD: String = "powerOutput"

    val powerOutput: BytePropertyCell = BytePropertyCell(value = 0)
    var isActive: BooleanPropertyCell = BooleanPropertyCell(value = false)

    def toggleActive() =
        synchronized(isActive)
        {
            isActive := !(~isActive)
        }

    def raisePower() =
        synchronized(powerOutput)
        {
            powerOutput := ((~powerOutput + 1) % 16).toByte
        }

    override def unload(tag: NBTTagCompound)
    {
        tag.setByte(POWER_OUTPUT_FIELD, ~powerOutput)
        tag.setBoolean(IS_ACTIVE_FIELD, ~isActive)
    }

    override def load(tag: NBTTagCompound)
    {
        powerOutput := tag.getByte(POWER_OUTPUT_FIELD)
        isActive := tag.getBoolean(IS_ACTIVE_FIELD)
    }
}