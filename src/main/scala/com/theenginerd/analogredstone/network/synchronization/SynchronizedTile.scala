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

import net.minecraft.tileentity.TileEntity
import com.theenginerd.analogredstone.network.data.synchronization.{Property, SynchronizedTileMessage, SynchronizedMessage}
import com.theenginerd.analogredstone.network.PacketHandler

class SynchronizedTile extends TileEntity with Synchronized
{
    protected def buildSynchronizedMessage(properties: Seq[MappedPropertyCell]): SynchronizedMessage =
    {
        new SynchronizedTileMessage(xCoord, yCoord, zCoord, convertToMessageProperties(properties))
    }

    private def convertToMessageProperties(propertyCells: Seq[MappedPropertyCell]) =
    {
        var properties: List[Property] = List()

        for(propertyCell <- propertyCells)
        {
            properties :+= new Property(propertyCell.id, propertyCell.getTypeId, ~propertyCell)
        }

        properties
    }

    protected def sendSynchronizedMessage(message: => SynchronizedMessage) =
    {
        if(!worldObj.isRemote)
        {
            PacketHandler.sendToAllAround(message, worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64.0D)
        }
    }
}
