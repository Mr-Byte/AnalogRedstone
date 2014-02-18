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

package com.theenginerd.analogredstone.client.renderer.tileentity

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import com.theenginerd.analogredstone.tileentity.VariableSwitchTileEntity
import org.lwjgl.opengl.GL11
import com.theenginerd.analogredstone.block.VariableSwitchBlock
import net.minecraftforge.common.util.ForgeDirection.{DOWN, UP, NORTH, SOUTH, WEST, EAST}
import net.minecraftforge.common.util.ForgeDirection
import com.theenginerd.analogredstone.client.model.tileentity.VariableSwitchModel
import cpw.mods.fml.client.FMLClientHandler
import net.minecraft.world.IBlockAccess
import net.minecraft.client.renderer.{RenderHelper, Tessellator}

object VariableSwitchTileEntityRenderer extends TileEntitySpecialRenderer
{
    def renderTileEntityAt(tileentity: TileEntity, x: Double, y: Double, z: Double, tick: Float): Unit =
    {
        val variableSwitch = tileentity.asInstanceOf[VariableSwitchTileEntity]
        val metadata = variableSwitch.getBlockMetadata
        val direction = VariableSwitchBlock.getDirection(metadata)
        val orientation = VariableSwitchBlock.getOrientation(metadata)

        setBrightness(variableSwitch)

        GL11.glPushMatrix()

        transformOrientation(x, y, z, direction, orientation)
        VariableSwitchModel.render(~variableSwitch.isActive: Boolean, ~variableSwitch.powerOutput: Byte)

        GL11.glPopMatrix()

        resetBrightness(variableSwitch)
    }

    private def resetBrightness(variableSwitch: VariableSwitchTileEntity)
    {
        if (~variableSwitch.isActive)
        {
            RenderHelper.enableStandardItemLighting()
        }
    }

    private def setBrightness(variableSwitch: VariableSwitchTileEntity)
    {
        val tessellator = Tessellator.instance
        val blockAccess: IBlockAccess = FMLClientHandler.instance().getWorldClient
        val brightness = variableSwitch.getBlockType.getMixedBrightnessForBlock(blockAccess, variableSwitch.xCoord, variableSwitch.yCoord, variableSwitch.zCoord)

        if (~variableSwitch.isActive)
        {
            RenderHelper.disableStandardItemLighting()
        }

        tessellator.setBrightness(brightness)
        tessellator.setColorOpaque_F(1F, 1F, 1F)
    }

    private def transformOrientation(x: Double, y: Double, z: Double, direction: ForgeDirection, orientation: Int)
    {
        direction match
        {
            case DOWN if orientation == 0 =>
                GL11.glTranslatef(x.toFloat, y.toFloat + 1.0f, z.toFloat + 1.0f)
                GL11.glRotated(180, 1, 0, 0)

            case DOWN if orientation == 1 =>
                GL11.glTranslatef(x.toFloat + 1, y.toFloat + 1.0f, z.toFloat + 1)
                GL11.glRotated(90, 0, 1, 0)
                GL11.glRotated(180, 1, 0, 0)

            case UP if orientation == 0 =>
                GL11.glTranslatef(x.toFloat, y.toFloat, z.toFloat)

            case UP if orientation == 1 =>
                GL11.glTranslatef(x.toFloat, y.toFloat, z.toFloat + 1.0f)
                GL11.glRotated(90, 0, 1, 0)

            case NORTH =>
                GL11.glTranslatef(x.toFloat + 1.0f, y.toFloat + 1.0f, z.toFloat + 1.0f)
                GL11.glRotated(-90, 1, 0, 0)
                GL11.glScalef(-1, 1, -1)

            case SOUTH =>
                GL11.glTranslatef(x.toFloat, y.toFloat + 1.0f, z.toFloat)
                GL11.glRotated(90, 1, 0, 0)

            case WEST =>
                GL11.glTranslatef(x.toFloat + 1.0f, y.toFloat + 1.0f, z.toFloat)
                GL11.glRotated(90, 0, 0, 1)
                GL11.glRotated(90, 0, 1, 0)
                GL11.glScalef(-1, 1, -1)

            case EAST =>
                GL11.glTranslatef(x.toFloat, y.toFloat + 1.0f, z.toFloat + 1.0f)
                GL11.glRotated(-90, 0, 0, 1)
                GL11.glRotated(90, 0, 1, 0)

            case _ =>
                GL11.glTranslatef(x.toFloat, y.toFloat, z.toFloat)
        }
    }
}
