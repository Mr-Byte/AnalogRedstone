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
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.{IBlockAccess, World}

class ModBlockAdapter(private val wrappedBlock: net.minecraft.block.Block) extends ModBlock
{
    override def canBePlacedOnSide(world: World, position: Position[Int], metadata: Int): Boolean =
    {
        val Position(x, y, z) = position

        wrappedBlock.canPlaceBlockOnSide(world, x, y, z, metadata)
    }

    override def getStrongRedstonePower(blockAccess: IBlockAccess, position: Position[Int], side: BlockSide): Int =
    {
        val Position(x, y, z) = position

        wrappedBlock.isProvidingStrongPower(blockAccess, x, y, z, side.value)
    }

    override def onBreak(world: World, position: Position[Int], block: ModBlock, metadata: Int): Unit =
    {
        val Position(x, y, z) = position
        val ModBlock(worldBlock) = block

        wrappedBlock.breakBlock(world, x, y, z, worldBlock, metadata)
    }

    override def onPlaceInWorldByEntity(world: World, position: Position[Int], entity: EntityLivingBase, itemStack: ItemStack): Unit = ???

    override def onPlacedInWorld(world: World, position: Position[Int], hitPosition: Position[Float], side: BlockSide, metadata: Int): BlockSide = ???

    override def canProvideRedstonePower: Boolean = ???

    override def isOpaque: Boolean =
    {
        wrappedBlock.isOpaqueCube
    }

    override def canStay(world: World, position: Position[Int]): Boolean = ???

    override def onNeighborChanged(world: World, position: Position[Int], neighbor: ModBlock): Unit = ???

    override def canBePlacedAt(world: World, position: Position[Int]): Boolean = ???

    override def onActivated(world: World, position: Position[Int], hitPosition: Position[Float], player: EntityPlayer, side: BlockSide): Boolean = ???

    override def getWeakRedstonePower(blockAccess: IBlockAccess, position: Position[Int], side: BlockSide): Int = ???

    def unwrap() = wrappedBlock
}
