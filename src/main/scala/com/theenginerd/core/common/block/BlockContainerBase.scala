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

package com.theenginerd.core.common.block

import com.theenginerd.core.common.world.Position
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class BlockContainerBase[T: Manifest](material: Material) extends BlockBase(material) with ITileEntityProvider
{
    isBlockContainer = true

    //TODO: Override ModBlock equivalent.
    override def onBlockAdded(world: World, x: Int, y: Int, z: Int)
    {
        super.onBlockAdded(world, x, y, z)
    }

    override def onBlockEventReceived(world: World, x: Int, y: Int, z: Int, eventId: Int, eventArgument: Int): Boolean =
    {
        super.onBlockEventReceived(world, x, y, z, eventId, eventArgument)
        val tileEntity = world.getTileEntity(x, y, z)

        tileEntity != null && tileEntity.receiveClientEvent(eventId, eventArgument)
    }

    override def onBreak(world: World, position: Position[Int], block: Block, metadata: Int) =
    {
        val Position(x, y, z) = position
        super.onBreak(world, position, block, metadata)
        world.removeTileEntity(x, y, z)
    }

    def createNewTileEntity(world: World, metadata : Int): TileEntity =
    {
        implicitly[Manifest[T]].runtimeClass.newInstance().asInstanceOf[TileEntity]
    }
}
