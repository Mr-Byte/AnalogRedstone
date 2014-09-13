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

package com.theenginerd.core.common.tileEntity

import net.minecraft.nbt.NBTTagCompound
import com.theenginerd.core.common.network.synchronization.data.{Property, SynchronizedTileMessage, SynchronizedMessage}
import com.theenginerd.core.common.synchronization.PropertyCell
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import com.theenginerd.core.common.network.PacketHandler

/**
 * Base adapter class that adapts the implementation of TileEntity to the BlockEntity trait.
 * This also includes the ability to synchronize the TileEntity over the network.
 */
abstract class ModTileEntityAdapter extends TileEntity with ModTileEntity
{
    override def getDescriptionPacket =
        PacketHandler.instance.convertMessageToPacket(buildSynchronizedMessage(getAllProperties))

    protected override def buildSynchronizedMessage(properties: Iterable[PropertyCell]): SynchronizedMessage =
    {
        new SynchronizedTileMessage(xCoord, yCoord, zCoord, convertToMessageProperties(properties))
    }

    private def convertToMessageProperties(propertyCells: Iterable[PropertyCell]) =
    {
        for (propertyCell <- propertyCells)
        yield new Property(propertyCell.id, propertyCell.getTypeId, ~propertyCell)
    }

    protected override def sendSynchronizedMessage(message: => SynchronizedMessage) =
    {
        if (!worldObj.isRemote)
        {
            PacketHandler.instance.sendToAllAround(message, worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64.0D)
        }
    }

    override def writeToNBT(tag: NBTTagCompound) =
    {
        super.writeToNBT(tag)
        unload(tag)
    }

    override def readFromNBT(tag: NBTTagCompound) =
    {
        super.readFromNBT(tag)
        load(tag)
    }
}

object ModTileEntityAdapter
{
    def find(world : World, x: Int, y: Int, z: Int) : Option[ModTileEntityAdapter] =
        Option(world.getTileEntity(x, y, z).asInstanceOf[ModTileEntityAdapter])
}
