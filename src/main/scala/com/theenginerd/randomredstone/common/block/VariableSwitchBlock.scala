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

package com.theenginerd.randomredstone.common.block

import net.minecraft.block.{Block, BlockContainer}
import net.minecraft.block.material.Material
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.common.util.ForgeDirection.{DOWN, UP, NORTH, SOUTH, WEST, EAST, UNKNOWN}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{MathHelper, AxisAlignedBB}
import com.theenginerd.randomredstone.common.utility.HitBox
import net.minecraft.client.renderer.texture.IIconRegister
import com.theenginerd.randomredstone.RandomRedstoneMod.MOD_ID
import cpw.mods.fml.relauncher.{Side, SideOnly}
import java.util.Random
import com.theenginerd.randomredstone.client.tileentity.renderer.RenderIds
import com.theenginerd.randomredstone.common.blockEntity.VariableSwitchBlockEntity

object VariableSwitchBlock extends BlockContainer(Material.circuits)
{
    setCreativeTab(CreativeTabs.tabRedstone)

    protected abstract class Part
    case class Switch() extends Part
    case class PowerAdjuster() extends Part

    private final val DIRECTION_MASK = 0x07
    private final val ORIENTATION_MASK = 0x08

    def getDirection(metadata: Int): ForgeDirection =
        ForgeDirection.getOrientation(metadata & DIRECTION_MASK)

     def getOrientation(metadata: Int): Int =
        (metadata & ORIENTATION_MASK) >> 3


    override def registerBlockIcons(register: IIconRegister)
    {
        blockIcon = register.registerIcon(s"$MOD_ID:variable_switch_on")
    }

    override def createNewTileEntity(world: World, index: Int): TileEntity =
    {
        new VariableSwitchBlockEntity
    }

    override def isOpaqueCube = false

    override def getCollisionBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB = null

    /*
     * Checks to see if this block can be placed on the side of a block.
     */
    override def canPlaceBlockOnSide(world: World, x: Int, y: Int, z: Int, orientation: Int): Boolean =
        ForgeDirection.getOrientation(orientation) match
        {
            case DOWN => world.isSideSolid(x, y+1, z, DOWN)
            case UP => world.isSideSolid(x, y-1, z, UP)
            case NORTH => world.isSideSolid(x, y, z+1, NORTH)
            case SOUTH => world.isSideSolid(x, y, z-1, SOUTH)
            case WEST => world.isSideSolid(x+1, y, z, WEST)
            case EAST => world.isSideSolid(x-1, y, z, EAST)
            case UNKNOWN => false
        }

    /*
     * Checks to see if the block can be placed at the given coordinates.
     */
    override def canPlaceBlockAt(world: World, x: Int, y: Int, z: Int) =
        world.isSideSolid(x-1, y, z, EAST) ||
        world.isSideSolid(x+1, y, z, WEST) ||
        world.isSideSolid(x, y, z-1, SOUTH) ||
        world.isSideSolid(x, y, z+1, NORTH) ||
        world.isSideSolid(x, y-1, z, UP) ||
        world.isSideSolid(x, y+1, z, DOWN)

    override def canBlockStay(world: World, x: Int, y: Int, z: Int) =
    {
        val direction = getDirection(world.getBlockMetadata(x, y, z))

        direction match
        {
            case EAST => world.isSideSolid(x-1, y, z, EAST)
            case WEST => world.isSideSolid(x+1, y, z, WEST)
            case SOUTH => world.isSideSolid(x, y, z-1, SOUTH)
            case NORTH => world.isSideSolid(x, y, z+1, NORTH)
            case UP => world.isSideSolid(x, y-1, z, UP)
            case DOWN => world.isSideSolid(x, y+1, z, DOWN)
        }
    }


    override def onBlockPlaced(world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, metadata: Int): Int =
        ForgeDirection.getOrientation(side).ordinal

    override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, entity: EntityLivingBase, itemStack: ItemStack) =
    {
        var metadata = world.getBlockMetadata(x, y, z)
        val groundOrientation = (MathHelper.floor_double((entity.rotationYaw * 4.0F / 360.0F).asInstanceOf[Double] + 0.5F) & 0x01) << 3

        if(metadata <= 1)
            metadata = metadata | groundOrientation

        world.setBlockMetadataWithNotify(x, y, z, metadata, 2)
    }

    override def breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, metadata: Int) =
    {
        notifyNeighbors(world, x, y, z, getDirection(metadata))
        super.breakBlock(world, x, y, z, this, metadata)
    }

    override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean =
    {
        if(!world.isRemote)
        {
            val metadata = world.getBlockMetadata(x, y, z)

            val tileEntity = world.getTileEntity(x, y, z).asInstanceOf[VariableSwitchBlockEntity]

            for(part <- getActivatedPart(hitX, hitY, hitZ, metadata))
            {
                part match
                {
                    case Switch =>
                        tileEntity.toggleActive()
                        world.playSoundEffect(x.asInstanceOf[Double] + 0.5D, y.asInstanceOf[Double] + 0.5D, z.asInstanceOf[Double] + 0.5D, "random.click", 0.3F, 0.5F)

                    case PowerAdjuster => tileEntity.raisePower()
                }
            }

            world.setBlockMetadataWithNotify(x, y, z, metadata, 3)
            notifyNeighbors(world, x, y, z, getDirection(metadata))
        }

        true
    }

    private def getActivatedPart(hitX: Float, hitY: Float, hitZ: Float, metadata: Int) =
    {
        val direction = getDirection(metadata)
        val orientation = getOrientation(metadata)

        val hitBoxes = direction match
        {
            case WEST => (HitBox(0.875f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f), HitBox(0.875f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f))
            case EAST => (HitBox(0.0f, 0.0f, 0.5f, 0.125f, 1.0f, 1.0f), HitBox(0.0f, 0.0f, 0.0f, 0.125f, 1.0f, 0.5f))
            case NORTH => (HitBox(0.5f, 0.0f, 0.875f, 1.0f, 1.0f, 1.0f), HitBox(0.0f, 0.0f, 0.875f, 0.5f, 1.0f, 1.0f))
            case SOUTH => (HitBox(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 0.125f), HitBox(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.125f))
            case UP => orientation match
            {
                case 0 => (HitBox(0.0f, 0.0f, 0.0f, 0.5f, 0.125f, 1.0f), HitBox(0.5f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f))
                case 1 => (HitBox(0.0f, 0.0f, 0.5f, 1.0f, 0.125f, 1.0f), HitBox(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 0.5f))
            }
            case DOWN => orientation match
            {
                case 0 => (HitBox(0.0f, 0.875f, 0.0f, 0.5f, 1.0f, 1.0f), HitBox(0.5f, 0.875f, 0.0f, 1.0f, 1.0f, 1.0f))
                case 1 => (HitBox(0.0f, 0.875f, 0.5f, 1.0f, 1.0f, 1.0f), HitBox(0.0f, 0.875f, 0.0f, 1.0f, 1.0f, 0.5f))
            }
            case UNKNOWN => (HitBox(0,0,0,0,0,0), HitBox(0,0,0,0,0,0))
        }

        hitBoxes match
        {
            case (box, _) if box.isPointInside(hitX, hitY, hitZ) => Some(Switch)
            case (_, box) if box.isPointInside(hitX, hitY, hitZ) => Some(PowerAdjuster)
            case _ => None
        }
    }


    override def onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block) =
    {
        if(!canBlockStay(world, x, y, z))
        {
            val metadata = world.getBlockMetadata(x, y, z)
            dropBlockAsItem(world, x, y, z, metadata, 0)
            world.setBlockToAir(x, y, z)
            notifyNeighbors(world, x, y, z, getDirection(metadata))
        }
    }

    private def notifyNeighbors(world: World, x: Int, y: Int, z: Int, direction: ForgeDirection)
    {
        world.notifyBlocksOfNeighborChange(x, y, z, this)

        direction match
        {
            case DOWN => world.notifyBlocksOfNeighborChange(x, y + 1, z, this)
            case UP => world.notifyBlocksOfNeighborChange(x, y - 1, z, this)
            case WEST => world.notifyBlocksOfNeighborChange(x + 1, y, z, this)
            case EAST => world.notifyBlocksOfNeighborChange(x - 1, y, z, this)
            case SOUTH => world.notifyBlocksOfNeighborChange(x, y, z - 1, this)
            case NORTH => world.notifyBlocksOfNeighborChange(x, y, z + 1, this)
            case UNKNOWN => ()
        }
    }

    override def setBlockBoundsBasedOnState(blockAccess: IBlockAccess, x: Int, y: Int, z: Int) =
    {
        val direction = getDirection(blockAccess.getBlockMetadata(x, y, z))

        direction match
        {
            case DOWN => setBlockBounds(0.0f, 0.875f, 0.0f, 1.0f, 1.0f, 1.0f)
            case UP => setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f)
            case EAST => setBlockBounds(0.0f, 0.0f, 0.0f, 0.125f, 1.0f, 1.0f)
            case WEST => setBlockBounds(0.875f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f)
            case SOUTH => setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.125f)
            case NORTH => setBlockBounds(0.0f, 0.0f, 0.875f, 1.0f, 1.0f, 1.0f)
            case UNKNOWN => setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f)
        }
    }

    /*
     * Always provide weak power, regardless of orientation if that switch is in the on position.
     */
    override def isProvidingWeakPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int =
    {
        val tileEntity = blockAccess.getTileEntity(x, y, z).asInstanceOf[VariableSwitchBlockEntity]

        if (~tileEntity.isActive) ~tileEntity.powerOutput else 0
    }

    /*
     * Only provide strong to the block that the switch is directly attached to.
     */
    override def isProvidingStrongPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int =
    {
        val tileEntity = blockAccess.getTileEntity(x, y, z).asInstanceOf[VariableSwitchBlockEntity]

        getDirection(blockAccess.getBlockMetadata(x, y, z)) match
        {
            case direction @ (UP | DOWN) if direction != getDirection(side) => 0
            case _ if ~tileEntity.isActive => ~tileEntity.powerOutput
            case _ => 0
        }
    }

    override def canProvidePower = true

    override def renderAsNormalBlock = false

    override def getRenderType = RenderIds.variableSwitch

    override def getLightValue(blockAccess: IBlockAccess, x: Int, y: Int, z: Int) =
    {
        val tileEntity = blockAccess.getTileEntity(x, y, z).asInstanceOf[VariableSwitchBlockEntity]

        if(~tileEntity.isActive) 10 else 0
    }

    @SideOnly(Side.CLIENT)
    override def randomDisplayTick(world: World, x: Int, y: Int, z: Int, random: Random)
    {
        val tileEntity = world.getTileEntity(x, y, z).asInstanceOf[VariableSwitchBlockEntity]
        val metadata = world.getBlockMetadata(x, y, z)

        if (~tileEntity.isActive)
        {
            val direction = getDirection(metadata)
            val orientation = getOrientation(metadata)

            val xPosition: Double = x + (random.nextDouble - 0.5D) * 0.2D
            val yPosition: Double = y + (random.nextDouble - 0.5D) * 0.2D
            val zPosition: Double = z + (random.nextDouble - 0.5D) * 0.2D

            val (xOffset, yOffset, zOffset) = getTorchOffset(direction, orientation, ~tileEntity.powerOutput)

            world.spawnParticle("reddust", xPosition + xOffset, yPosition + yOffset, zPosition + zOffset, 0.0D, 0.0D, 0.0D)
        }
    }

    private def getTorchOffset(direction: ForgeDirection, orientation: Int, powerOutput: Byte) =
    {
        val scaledPower = 0.625D * (powerOutput / 15D)

        direction match
        {
            case DOWN if orientation == 0 => (0.75D, 0.6D, 0.25D + scaledPower)
            case DOWN if orientation == 1 => (0.25D + scaledPower, 0.6D, 0.25D)
            case UP if orientation == 0 => (0.75D, 0.4D, 0.75D - scaledPower)
            case UP if orientation == 1 => (0.75D - scaledPower, 0.4D, 0.25D)
            case NORTH => (0.25D, 0.25D + scaledPower, 0.6D)
            case SOUTH => (0.75D, 0.25D + scaledPower, 0.4D)
            case WEST => (0.6D, 0.25D + scaledPower, 0.75D)
            case EAST => (0.4D, 0.25D + scaledPower, 0.25D)
            case _ => (0D, 0D, 0D)
        }
    }
}
