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
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.{IBlockAccess, World}

abstract class ModBlockBase(material: Material) extends net.minecraft.block.Block(material) with ModBlock
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

        onBreak(world, position, new ModBlockAdapter(block), metadata)
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
        onNeighborChanged(world, position, new ModBlockAdapter(neighbor))
    }

    override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float) =
    {
        val position = Position(x, y, z)
        val hitPosition = Position(hitX, hitY, hitZ)

        onActivated(world, position, hitPosition, player, side)
    }

    /* Delegate Block methods to super implementation */
    override def canBePlacedOnSide(world: World, position: Position[Int], metadata: Int): Boolean =
    {
        val Position(x, y, z) = position
        super.canPlaceBlockOnSide(world, x, y, z, metadata)
    }

    override def getWeakRedstonePower(blockAccess: IBlockAccess, position: Position[Int], side: BlockSide): Int =
    {
        val Position(x, y, z) = position

        super.isProvidingWeakPower(blockAccess, x, y, z, side)
    }

    override def onActivated(world: World, position: Position[Int], hitPosition: Position[Float], player: EntityPlayer, side: BlockSide): Boolean =
    {
        val Position(x, y, z) = position
        val Position(hitX, hitY, hitZ) = position

        super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)
    }

    override def canBePlacedAt(world: World, position: Position[Int]): Boolean =
    {
        val Position(x, y, z) = position
        super.canPlaceBlockAt(world, x, y, z)
    }

    override def onNeighborChanged(world: World, position: Position[Int], neighbor: ModBlock): Unit =
    {
        val Position(x, y, z) = position
        val ModBlock(block) = neighbor

        super.onNeighborBlockChange(world, x, y, z, block)
    }

    override def canProvideRedstonePower: Boolean =
    {
        super.canProvidePower
    }

    override def canStay(world: World, position: Position[Int]): Boolean =
    {
        val Position(x, y, z) = position
        super.canBlockStay(world, x, y, z)
    }

    override def isOpaque: Boolean =
    {
        super.isOpaqueCube
    }

    override def onPlacedInWorld(world: World, position: Position[Int], hitPosition: Position[Float], side: BlockSide, metadata: Int): BlockSide =
    {
        val Position(x, y, z) = position
        val Position(hitX, hitY, hitZ) = hitPosition

        super.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, metadata)
    }

    override def onPlaceInWorldByEntity(world: World, position: Position[Int], entity: EntityLivingBase, itemStack: ItemStack): Unit =
    {
        val Position(x, y, z) = position
        super.onBlockPlacedBy(world, x, y, z, entity, itemStack)
    }

    override def onBreak(world: World, position: Position[Int], block: ModBlock, metadata: Int): Unit =
    {
        val Position(x, y, z) = position
        val ModBlock(worldBlock) = block

        super.breakBlock(world, x, y, z, worldBlock, metadata)
    }

    override def getStrongRedstonePower(blockAccess: IBlockAccess, position: Position[Int], side: BlockSide): Int =
    {
        val Position(x, y, z) = position
        super.isProvidingStrongPower(blockAccess, x, y, z, side)
    }
}

