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

package com.theenginerd.modcore.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.{World, IBlockAccess}
import net.minecraftforge.common.util.ForgeDirection

abstract class BlockAdapter(material: Material) extends Block(material) with ModBlock
{
    /*
     * This section replaces all relevant methods from Block with calls to the ModBlock trait.
     * If the ModBlock method is not defined in a further derived trait, then the default implementation is used;
     * otherwise the implementation from the further derived trait is used.
     */
    override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, entity: EntityLivingBase, itemStack: ItemStack) =
        onPlaceInWorldByEntity(world, x, y, z, entity, itemStack)

    override def breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, metadata: Int) =
        onBreak(world, x, y, z, block, metadata)

    override def canPlaceBlockOnSide(world: World, x: Int, y: Int, z: Int, metadata: Int) =
        canBePlacedOnSide(world, x, y, z, metadata)

    override def onBlockPlaced(world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, metadata: Int) =
        onPlacedInWorld(world, x, y, z, side, hitX, hitY, hitZ, metadata).ordinal()

    override def canPlaceBlockAt(world: World, x: Int, y: Int, z: Int) =
        canBePlacedAt(world, x, y, z)

    override def canProvidePower = canProvideRedstonePower

    override def isProvidingWeakPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int) =
        getWeakRedstonePower(blockAccess, x, y, z, side)

    override def isProvidingStrongPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int) =
        getStrongRedstonePower(blockAccess, x, y, z, side)

    override def isOpaqueCube = isOpaque

    override def canBlockStay(world: World, x: Int, y: Int, z: Int) =
        canStay(world, x, y, z)

    override def onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, neighbor: Block) =
        onNeighborChanged(world, x, y, z, neighbor, this)

    override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float) =
        onActivated(world, x, y, z, player, side, hitX, hitY, hitZ, this)

    /*
     * This section creates default implementations of the methods defined in ModBlock.  It defers the implementation to the super-class (Block).
     */
    def onPlaceInWorldByEntity(world: World, x: Int, y: Int, z: Int, entity: EntityLivingBase, itemStack: ItemStack): Unit =
        super.onBlockPlacedBy(world, x, y, z, entity, itemStack)

    def onBreak(world: World, x: Int, y: Int, z: Int, block: Block, metadata: Int): Unit =
        super.breakBlock(world, x, y, z, block, metadata)

    def canBePlacedOnSide(world: World, x: Int, y: Int, z: Int, metadata: Int): Boolean = 
        super.canPlaceBlockOnSide(world, x, y, z, metadata)

    def onPlacedInWorld(world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, metadata: Int): ForgeDirection = 
        ForgeDirection.getOrientation(super.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, metadata))

    def canBePlacedAt(world: World, x: Int, y: Int, z: Int): Boolean = 
        super.canPlaceBlockAt(world, x, y, z)

    def canProvideRedstonePower: Boolean = 
        super.canProvidePower

    def getWeakRedstonePower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int =
        super.isProvidingWeakPower(blockAccess, x, y, z, side)

    def getStrongRedstonePower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int =
        super.isProvidingStrongPower(blockAccess, x, y, z, side)

    def isOpaque: Boolean =
        super.isOpaqueCube

    def canStay(world: World, x: Int, y: Int, z: Int): Boolean =
        super.canBlockStay(world, x, y, z)

    def onNeighborChanged(world: World, x: Int, y: Int, z: Int, neighbor: Block, self: Block): Unit =
        super.onNeighborBlockChange(world, x, y, z, neighbor)

    def onActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float, block: Block): Boolean =
        super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)

    final def dropAsItem(world: World, x: Int, y: Int, z: Int, metadata: Int, what: Int) =
        dropBlockAsItem(world, x, y, z, metadata, what)
}

