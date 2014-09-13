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

import java.util.Random

import com.theenginerd.core.common.block.{Block, BlockContainerBase}
import com.theenginerd.core.common.world.{BlockSide, Position}
import com.theenginerd.randomredstone.RandomRedstoneMod.MOD_ID
import com.theenginerd.randomredstone.client.tileentity.renderer.RenderIds
import com.theenginerd.randomredstone.common.blockEntity.VariableSwitchTileEntity
import com.theenginerd.randomredstone.common.utility.HitBox
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.{AxisAlignedBB, MathHelper}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.common.util.ForgeDirection.{DOWN, EAST, NORTH, SOUTH, UNKNOWN, UP, WEST}

trait VariableSwitchBlock extends Block
{
    protected abstract class Part
    case class Switch() extends Part
    case class PowerAdjuster() extends Part

    private final val DIRECTION_MASK = 0x07
    private final val ORIENTATION_MASK = 0x08

    def getDirection(metadata: Int): ForgeDirection =
        ForgeDirection.getOrientation(metadata & DIRECTION_MASK)

    def getOrientation(metadata: Int): Int =
        (metadata & ORIENTATION_MASK) >> 3

    override def canBePlacedOnSide(world: World, position: Position[Int], metadata: Int) =
    {
        val Position(x, y, z) = position

        ForgeDirection.getOrientation(metadata) match
        {
            case DOWN => world.isSideSolid(x, y+1, z, DOWN)
            case UP => world.isSideSolid(x, y-1, z, UP)
            case NORTH => world.isSideSolid(x, y, z+1, NORTH)
            case SOUTH => world.isSideSolid(x, y, z-1, SOUTH)
            case WEST => world.isSideSolid(x+1, y, z, WEST)
            case EAST => world.isSideSolid(x-1, y, z, EAST)
            case UNKNOWN => false
        }
    }

    override def isOpaque = false

    override def canBePlacedAt(world: World, position: Position[Int]): Boolean =
    {
        val Position(x, y, z) = position

        world.isSideSolid(x-1, y, z, EAST) ||
        world.isSideSolid(x+1, y, z, WEST) ||
        world.isSideSolid(x, y, z-1, SOUTH) ||
        world.isSideSolid(x, y, z+1, NORTH) ||
        world.isSideSolid(x, y-1, z, UP) ||
        world.isSideSolid(x, y+1, z, DOWN)
    }

    override def onPlaceInWorldByEntity(world: World, position: Position[Int], entity: EntityLivingBase, itemStack: ItemStack): Unit =
    {
        val Position(x, y, z) = position

        var metadata = world.getBlockMetadata(x, y, z)
        val groundOrientation = (MathHelper.floor_double((entity.rotationYaw * 4.0F / 360.0F).asInstanceOf[Double] + 0.5F) & 0x01) << 3

        if(metadata <= 1)
            metadata = metadata | groundOrientation

        world.setBlockMetadataWithNotify(x, y, z, metadata, 2)
    }

    abstract override def onBreak(world: World, position: Position[Int], block: Block, metadata: Int): Unit =
    {
        val Position(x, y, z) = position

        notifyNeighbors(world, x, y, z, block, getDirection(metadata))
        super.onBreak(world, position, block, metadata)
    }

    override def onPlacedInWorld(world: World, position: Position[Int], hitPosition: Position[Float], side: BlockSide, metadata: Int): BlockSide =
        side

    override def canStay(world: World, position: Position[Int]): Boolean =
    {
        val Position(x, y, z) = position
        val direction = getDirection(world.getBlockMetadata(x, y, z))

        //TODO: Convert to BlockSide
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

    override def onNeighborChanged(world: World, position: Position[Int], neighbor: Block): Unit =
    {
        val Position(x, y, z) = position

        if(!canStay(world, position))
        {
            val metadata = world.getBlockMetadata(x, y, z)
            dropAsItem(world, position, metadata, 0)
            world.setBlockToAir(x, y, z)
            notifyNeighbors(world, x, y, z, this, getDirection(metadata))
        }
    }

    override def onActivated(world: World, position: Position[Int], hitPosition: Position[Float], player: EntityPlayer, side: BlockSide): Boolean =
    {
        val Position(x, y, z) = position
        val Position(hitX, hitY, hitZ) = hitPosition

        if(!world.isRemote)
        {
            val metadata = world.getBlockMetadata(x, y, z)

            val tileEntity = world.getTileEntity(x, y, z).asInstanceOf[VariableSwitchTileEntity]

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
            notifyNeighbors(world, x, y, z, this, getDirection(metadata))
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

    private def notifyNeighbors(world: World, x: Int, y: Int, z: Int, block: Block, direction: ForgeDirection)
    {
        val Block(worldBlock) = block

        world.notifyBlocksOfNeighborChange(x, y, z, worldBlock)

        direction match
        {
            case DOWN => world.notifyBlocksOfNeighborChange(x, y + 1, z, worldBlock)
            case UP => world.notifyBlocksOfNeighborChange(x, y - 1, z, worldBlock)
            case WEST => world.notifyBlocksOfNeighborChange(x + 1, y, z, worldBlock)
            case EAST => world.notifyBlocksOfNeighborChange(x - 1, y, z, worldBlock)
            case SOUTH => world.notifyBlocksOfNeighborChange(x, y, z - 1, worldBlock)
            case NORTH => world.notifyBlocksOfNeighborChange(x, y, z + 1, worldBlock)
            case UNKNOWN => ()
        }
    }

    override def canProvideRedstonePower: Boolean = true

    override def getWeakRedstonePower(blockAccess: IBlockAccess, position: Position[Int], side: BlockSide): Int =
    {
        val Position(x, y, z) = position
        val tileEntity = blockAccess.getTileEntity(x, y, z).asInstanceOf[VariableSwitchTileEntity]

        if (~tileEntity.isActive) ~tileEntity.powerOutput else 0
    }

    override def getStrongRedstonePower(blockAccess: IBlockAccess, position: Position[Int], side: BlockSide): Int =
    {
        val Position(x, y, z) = position
        val tileEntity = blockAccess.getTileEntity(x, y, z).asInstanceOf[VariableSwitchTileEntity]

        getDirection(blockAccess.getBlockMetadata(x, y, z)) match
        {
            case direction @ (UP | DOWN) if direction != getDirection(side) => 0
            case _ if ~tileEntity.isActive => ~tileEntity.powerOutput
            case _ => 0
        }
    }
}

//TODO: Move some of this to ModBlock while decoupling some of the stuff related to rendering and collision detection.
object VariableSwitchBlock extends BlockContainerBase[VariableSwitchTileEntity](Material.circuits) with VariableSwitchBlock
{
    setCreativeTab(CreativeTabs.tabRedstone)

    override def registerBlockIcons(register: IIconRegister)
    {
        blockIcon = register.registerIcon(s"$MOD_ID:variable_switch_on")
    }

    override def getCollisionBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB = null

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

    override def renderAsNormalBlock = false

    override def getRenderType = RenderIds.variableSwitch

    //TODO: Move into ModBlock
    override def getLightValue(blockAccess: IBlockAccess, x: Int, y: Int, z: Int) =
    {
        val tileEntity = blockAccess.getTileEntity(x, y, z).asInstanceOf[VariableSwitchTileEntity]

        if(~tileEntity.isActive) 10 else 0
    }

    @SideOnly(Side.CLIENT)
    override def randomDisplayTick(world: World, x: Int, y: Int, z: Int, random: Random)
    {
        val tileEntity = world.getTileEntity(x, y, z).asInstanceOf[VariableSwitchTileEntity]
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
