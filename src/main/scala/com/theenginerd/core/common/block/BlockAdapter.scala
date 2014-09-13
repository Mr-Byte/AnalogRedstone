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

import com.theenginerd.core.common.world.{BlockSide, Position}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.{IBlockAccess, World}

class BlockAdapter(private val block: net.minecraft.block.Block) extends AnyVal with Block
{
    override def canBePlacedOnSide(world: World, position: Position[Int], metadata: Int): Boolean = ???

    override def getStrongRedstonePower(blockAccess: IBlockAccess, position: Position[Int], side: BlockSide): Int = ???

    override def onBreak(world: World, position: Position[Int], block: Block, metadata: Int): Unit =
    {
        val Position(x, y, z) = position
        block.breakBlock(world, x, y, z, block, metadata)
    }

    override def onPlaceInWorldByEntity(world: World, position: Position[Int], entity: EntityLivingBase, itemStack: ItemStack): Unit = ???

    override def onPlacedInWorld(world: World, position: Position[Int], hitPosition: Position[Float], side: BlockSide, metadata: Int): BlockSide = ???

    override def dropAsItem(world: World, position: Position[Int], metadata: Int, what: Int): Unit = ???

    override def canProvideRedstonePower: Boolean = ???

    override def isOpaque: Boolean = ???

    override def canStay(world: World, position: Position[Int]): Boolean = ???

    override def onNeighborChanged(world: World, position: Position[Int], neighbor: Block): Unit = ???

    override def canBePlacedAt(world: World, position: Position[Int]): Boolean = ???

    override def onActivated(world: World, position: Position[Int], hitPosition: Position[Float], player: EntityPlayer, side: BlockSide): Boolean = ???

    override def getWeakRedstonePower(blockAccess: IBlockAccess, position: Position[Int], side: BlockSide): Int = ???

    def getMinecraftBlock = block
}
