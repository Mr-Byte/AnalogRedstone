/*
 * Copyright 2013 Joshua R. Rodgers
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

package com.theenginerd.analogredstone.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.ForgeDirection
import net.minecraftforge.common.ForgeDirection.{DOWN, UP, NORTH, SOUTH, WEST, EAST}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack

object VariableSwitchBlock extends Block(VARIABLE_SWITCH_ID, Material.circuits)
{
    setCreativeTab(CreativeTabs.tabRedstone)

    override def isOpaqueCube = false

    /*
     * Checks to see if this block can be placed on the side of a block.
     */
    override def canPlaceBlockOnSide(world: World, x: Int, y: Int, z: Int, orientation: Int): Boolean =
        ForgeDirection.getOrientation(orientation) match
        {
            case DOWN => world.isBlockSolidOnSide(x, y+1, z, DOWN)
            case UP => world.isBlockSolidOnSide(x, y-1, z, UP)
            case NORTH => world.isBlockSolidOnSide(x, y, z+1, NORTH)
            case SOUTH => world.isBlockSolidOnSide(x, y, z-1, SOUTH)
            case WEST => world.isBlockSolidOnSide(x+1, y, z, WEST)
            case EAST => world.isBlockSolidOnSide(x-1, y, z, EAST)
        }

    /*
     * Checks to see if the block can be placed at the given coordinates.
     */
    override def canPlaceBlockAt(world: World, x: Int, y: Int, z: Int) =
        world.isBlockSolidOnSide(x-1, y, z, EAST) ||
        world.isBlockSolidOnSide(x+1, y, z, WEST) ||
        world.isBlockSolidOnSide(x, y, z-1, SOUTH) ||
        world.isBlockSolidOnSide(x, y, z+1, NORTH) ||
        world.isBlockSolidOnSide(x, y-1, z, UP) ||
        world.isBlockSolidOnSide(x, y+1, z, DOWN)

    override def onBlockPlaced(world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, metadata: Int): Int =
        ForgeDirection.getOrientation(side).ordinal

    override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, entity: EntityLivingBase, itemStack: ItemStack) =
        world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z), 2)

    override def breakBlock(world: World, x: Int, y: Int, z: Int, blockID: Int, metadata: Int) =
    {
        world.notifyBlockOfNeighborChange(x, y, z, this.blockID)

        ForgeDirection.getOrientation(metadata) match
        {
            case DOWN => world.notifyBlockOfNeighborChange(x, y-1, z, this.blockID)
            case UP => world.notifyBlockOfNeighborChange(x, y+1, z, this.blockID)
            case WEST => world.notifyBlockOfNeighborChange(x+1, y, z, this.blockID)
            case EAST => world.notifyBlockOfNeighborChange(x-1, y, z, this.blockID)
            case SOUTH => world.notifyBlockOfNeighborChange(x, y, z-1, this.blockID)
            case NORTH => world.notifyBlockOfNeighborChange(x, y, z+1, this.blockID)
        }

        super.breakBlock(world, x, y, z, blockID, metadata)
    }

    override def renderAsNormalBlock = false

    override def getRenderType = 12

    /*
     * Always provide weak power, regardless of orientation if that swicth is in the on position.
     */
    override def isProvidingWeakPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int = 15

    /*
     * Only provide strong to the block that the switch is directly attached to.
     */
    override def isProvidingStrongPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int =
        ForgeDirection.getOrientation(blockAccess.getBlockMetadata(x, y, z)) match
        {
            case direction @ (UP | DOWN) if direction != ForgeDirection.getOrientation(side) => 0
            case _ => 15
        }

    override def canProvidePower = true
}
