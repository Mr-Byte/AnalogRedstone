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

package com.theenginerd.randomredstone.common.synchronization

import net.minecraft.tileentity.TileEntity
import com.theenginerd.randomredstone.common.network.PacketHandler
import com.theenginerd.randomredstone.common.network.synchronization.data.{SynchronizedTileMessage, SynchronizedMessage, Property}

class SynchronizedTileEntity extends TileEntity with Synchronized
{
    override def getDescriptionPacket =
        PacketHandler.convertMessageToPacket(buildSynchronizedMessage(getAllProperties))

    protected override def buildSynchronizedMessage(properties: Iterable[PropertyCell]): SynchronizedMessage =
    {
        new SynchronizedTileMessage(xCoord, yCoord, zCoord, convertToMessageProperties(properties))
    }

    private def convertToMessageProperties(propertyCells: Iterable[PropertyCell]) =
    {
        for(propertyCell <- propertyCells)
            yield new Property(propertyCell.id, propertyCell.getTypeId, ~propertyCell)
    }

    protected override def sendSynchronizedMessage(message: => SynchronizedMessage) =
    {
        if(!worldObj.isRemote)
        {
            PacketHandler.sendToAllAround(message, worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64.0D)
        }
    }
}
