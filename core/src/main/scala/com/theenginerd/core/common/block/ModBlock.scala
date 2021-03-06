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
import net.minecraft.block
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.{IBlockAccess, World}


//TODO: Start cleaning up this interface.  Wrap up the World, IBlockAccess, EntityLivingBase, and EntityPlayer classes
// behind traits that expose their functionality.
trait ModBlock
{
    def canBePlacedOnSide(world: World, position: Position[Int], metadata: Int): Boolean
    def isOpaque: Boolean
    def canBePlacedAt(world: World, position: Position[Int]): Boolean
    def canStay(world: World, position: Position[Int]): Boolean

    def getWeakRedstonePower(blockAccess: IBlockAccess, position: Position[Int], side: BlockSide): Int
    def getStrongRedstonePower(blockAccess: IBlockAccess, position: Position[Int], side: BlockSide): Int
    def canProvideRedstonePower: Boolean

    def onPlacedInWorld(world: World, position: Position[Int], hitPosition: Position[Float], side: BlockSide, metadata: Int): BlockSide
    def onPlaceInWorldByEntity(world: World, position: Position[Int], entity: EntityLivingBase, itemStack: ItemStack): Unit
    def onBreak(world: World, position: Position[Int], block: ModBlock, metadata: Int): Unit
    def onActivated(world: World, position: Position[Int], hitPosition: Position[Float], player: EntityPlayer, side: BlockSide): Boolean
    def onNeighborChanged(world: World, position: Position[Int], neighbor: ModBlock): Unit

    /* NOTE:  This can only be done for final methods in the Minecraft Block itself.
     * Attempting to do this with non-final methods will cause infinite recursion!
     */
    final def dropAsItem(world: World, position: Position[Int], metadata: Int, what: Int) =
    {
        val ModBlock(worldBlock) = this
        val Position(x, y, z) = position

        worldBlock.dropBlockAsItem(world, x, y, z, metadata, what)
    }
}

object ModBlock
{
    var blockAdapterCache: Map[net.minecraft.block.Block, ModBlock] = Map()

    def unapply(block: ModBlock): Option[net.minecraft.block.Block] =
    {
        block match
        {
            case blockBase: ModBlockBase => Some(blockBase)
            case blockAdapter: ModBlockAdapter => Some(blockAdapter.unwrap())
            case _ => None
        }
    }

    def apply(minecraftBlock: net.minecraft.block.Block): ModBlock =
    {
        minecraftBlock match
        {
            case block: ModBlock => block
            case _ => getAdapterForBlock(minecraftBlock)
        }
    }

    private def getAdapterForBlock(minecraftBlock: block.Block): ModBlock =
    {
        blockAdapterCache.get(minecraftBlock) match
        {
            case Some(block) => block
            case None =>
                val adapter = new ModBlockAdapter(minecraftBlock)
                blockAdapterCache += (minecraftBlock -> adapter)
                adapter
        }
    }
}