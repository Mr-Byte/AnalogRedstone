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

import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.ForgeDirection
import net.minecraftforge.common.ForgeDirection.{DOWN, UP, NORTH, SOUTH, WEST, EAST, UNKNOWN}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import com.theenginerd.analogredstone.tileentity.VariableSwitchTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{MathHelper, AxisAlignedBB}
import com.theenginerd.analogredstone.utility.HitBox
import com.theenginerd.analogredstone.client.renderer.RenderIds
import net.minecraft.client.renderer.texture.IconRegister
import com.theenginerd.analogredstone.MOD_ID

object VariableSwitchBlock extends BlockContainer(VARIABLE_SWITCH_ID, Material.circuits)
{
    setUnlocalizedName(s"$MOD_ID.variableSwitch")
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


    override def registerIcons(register: IconRegister)
    {
        blockIcon = register.registerIcon(s"$MOD_ID:variableswitch")
    }

    override def createNewTileEntity(world: World): TileEntity = new VariableSwitchTileEntity

    override def isOpaqueCube = false

    override def getCollisionBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB = null

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
            case UNKNOWN => false
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
    {
        var metadata = world.getBlockMetadata(x, y, z)
        val groundOrientation = (MathHelper.floor_double((entity.rotationYaw * 4.0F / 360.0F).asInstanceOf[Double] + 0.5F) & 0x01) << 3

        if(metadata <= 1)
            metadata = metadata | groundOrientation

        world.setBlockMetadataWithNotify(x, y, z, metadata, 2)
    }

    override def breakBlock(world: World, x: Int, y: Int, z: Int, blockID: Int, metadata: Int) =
    {
        notifyNeighbors(world, x, y, z, getDirection(metadata))
        super.breakBlock(world, x, y, z, blockID, metadata)
    }

    override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean =
    {
        if(!world.isRemote)
        {
            val metadata = world.getBlockMetadata(x, y, z)
            val tileEntity = world.getBlockTileEntity(x, y, z).asInstanceOf[VariableSwitchTileEntity]
            val sneaking = player.isSneaking()

            for(part <- getActivatedPart(hitX, hitY, hitZ, metadata))
            {
                part match
                {
                    case Switch =>
                        tileEntity.toggleActive()
                        world.playSoundEffect(x.asInstanceOf[Double] + 0.5D, y.asInstanceOf[Double] + 0.5D, z.asInstanceOf[Double] + 0.5D, "random.click", 0.3F, 0.5F)

                    case PowerAdjuster => if(sneaking) tileEntity.lowerPower else tileEntity.raisePower
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
                case 1 => (HitBox(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 0.5f), HitBox(0.0f, 0.0f, 0.5f, 1.0f, 0.125f, 1.0f))
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

    private def notifyNeighbors(world: World, x: Int, y: Int, z: Int, direction: ForgeDirection)
    {
        world.notifyBlocksOfNeighborChange(x, y, z, this.blockID)

        direction match
        {
            case DOWN => world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID)
            case UP => world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID)
            case WEST => world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID)
            case EAST => world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID)
            case SOUTH => world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID)
            case NORTH => world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID)
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
        val tileEntity = blockAccess.getBlockTileEntity(x, y, z).asInstanceOf[VariableSwitchTileEntity]

        if (tileEntity.isActive) tileEntity.powerOutput else 0
    }

    /*
     * Only provide strong to the block that the switch is directly attached to.
     */
    override def isProvidingStrongPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int =
    {
        val tileEntity = blockAccess.getBlockTileEntity(x, y, z).asInstanceOf[VariableSwitchTileEntity]

        getDirection(blockAccess.getBlockMetadata(x, y, z)) match
        {
            case direction @ (UP | DOWN) if direction != getDirection(side) => 0
            case _ if tileEntity.isActive => tileEntity.powerOutput
            case _ => 0
        }
    }

    override def canProvidePower = true

    override def renderAsNormalBlock = false

    override def getRenderType = RenderIds.variableSwitch
}
