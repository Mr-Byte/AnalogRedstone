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
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

//TODO: Start cleaning up this interface.  Wrap up the World, IBlockAccess, EntityLivingBase, and EntityPlayer classes
// behind traits that expose their functionality.
trait ModBlock
{
    def canBePlacedOnSide(world: World, x: Int, y: Int, z: Int, metadata: Int): Boolean
    def isOpaque: Boolean
    def canBePlacedAt(world: World, x: Int, y: Int, z: Int): Boolean
    def canStay(world: World, x: Int, y: Int, z: Int): Boolean

    def getWeakRedstonePower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int
    def getStrongRedstonePower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int
    def canProvideRedstonePower: Boolean

    def onPlacedInWorld(world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, metadata: Int): ForgeDirection
    def onPlaceInWorldByEntity(world: World, x: Int, y: Int, z: Int, entity: EntityLivingBase, itemStack: ItemStack): Unit
    def onBreak(world: World, x: Int, y: Int, z: Int, block: Block, metadata: Int): Unit
    def onActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float, block: Block): Boolean
    def onNeighborChanged(world: World, x: Int, y: Int, z: Int, neighbor: Block, self: Block): Unit

    def dropAsItem(world: World, x: Int, y: Int, z: Int, metadata: Int, what: Int)
}