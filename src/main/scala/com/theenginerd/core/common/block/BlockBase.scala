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
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.{IBlockAccess, World}

abstract class BlockBase(material: Material) extends net.minecraft.block.Block(material) with Block
{
    /*
     * This section replaces all relevant methods from Block with calls to the ModBlock trait.
     * If the ModBlock method is not defined in a further derived trait, then the default implementation is used;
     * otherwise the implementation from the further derived trait is used.
     */
    override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, entity: EntityLivingBase, itemStack: ItemStack) =
    {
        val position = Position(x, y, z)

        onPlaceInWorldByEntity(world, position, entity, itemStack)
    }

    override def breakBlock(world: World, x: Int, y: Int, z: Int, block: net.minecraft.block.Block, metadata: Int) =
    {
        val position = Position(x, y, z)

        onBreak(world, position, new BlockAdapter(block), metadata)
    }

    override def canPlaceBlockOnSide(world: World, x: Int, y: Int, z: Int, metadata: Int) =
    {
        val position = Position(x, y, z)

        canBePlacedOnSide(world, position, metadata)
    }

    override def onBlockPlaced(world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, metadata: Int) =
    {
        val position = Position(x, y, z)
        val hitPosition = Position(hitX, hitY, hitZ)

        onPlacedInWorld(world, position, hitPosition, side, metadata).value
    }

    override def canPlaceBlockAt(world: World, x: Int, y: Int, z: Int) =
    {
        val position = Position(x, y, z)
        canBePlacedAt(world, position)
    }

    override def canProvidePower = canProvideRedstonePower

    override def isProvidingWeakPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int) =
    {
        val position = Position(x, y, z)
        getWeakRedstonePower(blockAccess, position, side)
    }

    override def isProvidingStrongPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int) =
    {
        val position = Position(x, y, z)
        getStrongRedstonePower(blockAccess, position, side)
    }

    override def isOpaqueCube = isOpaque

    override def canBlockStay(world: World, x: Int, y: Int, z: Int) =
    {
        val position = Position(x, y, z)
        canStay(world, position)
    }

    override def onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, neighbor: net.minecraft.block.Block): Unit =
    {
        val position = Position(x, y, z)
        onNeighborChanged(world, position, new BlockAdapter(neighbor))
    }

    override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float) =
    {
        val position = Position(x, y, z)
        val hitPosition = Position(hitX, hitY, hitZ)

        onActivated(world, position, hitPosition, player, side)
    }

    /* Delegate Block methods to super implementation */
    override def canBePlacedOnSide(world: World, position: Position[Int], metadata: Int): Boolean = ???

    override def getWeakRedstonePower(blockAccess: IBlockAccess, position: Position[Int], side: BlockSide): Int = ???

    override def onActivated(world: World, position: Position[Int], hitPosition: Position[Float], player: EntityPlayer, side: BlockSide): Boolean = ???

    override def canBePlacedAt(world: World, position: Position[Int]): Boolean = ???

    override def onNeighborChanged(world: World, position: Position[Int], neighbor: Block): Unit = ???

    override def canProvideRedstonePower: Boolean = ???

    override def canStay(world: World, position: Position[Int]): Boolean = ???

    override def isOpaque: Boolean = ???

    final def dropAsItem(world: World, position: Position[Int], metadata: Int, what: Int): Unit =
    {
        val Position(x, y, z) = position
        super.dropBlockAsItem(world, x, y, z, metadata, what)
    }

    override def onPlacedInWorld(world: World, position: Position[Int], hitPosition: Position[Float], side: BlockSide, metadata: Int): BlockSide = ???

    override def onPlaceInWorldByEntity(world: World, position: Position[Int], entity: EntityLivingBase, itemStack: ItemStack): Unit = ???

    override def onBreak(world: World, position: Position[Int], block: Block, metadata: Int): Unit =
    {
        val Position(x, y, z) = position
        super.breakBlock(world, x, y, z, block, metadata)
    }

    override def getStrongRedstonePower(blockAccess: IBlockAccess, position: Position[Int], side: BlockSide): Int = ???
}

