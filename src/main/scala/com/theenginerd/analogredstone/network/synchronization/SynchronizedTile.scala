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
import net.minecraft.entity.player.EntityPlayer

trait SynchronizedTile
{
    self: TileEntity =>
    implicit def position = (xCoord, yCoord, zCoord)

    def synchronized(handler: => Unit): Unit =
    {
        handler

        if(!worldObj.isRemote)
        {
            PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 64, worldObj.provider.dimensionId, getSynchronizationAction.toPacket)
        }
    }

    def getSynchronizationAction: TileSynchronizationAction
    def processSynchronizationAction(updateAction: TileSynchronizationAction, player: EntityPlayer)
}
