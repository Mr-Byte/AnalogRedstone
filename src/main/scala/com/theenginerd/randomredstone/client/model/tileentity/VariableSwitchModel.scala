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

package com.theenginerd.randomredstone.client.model.tileentity

import cpw.mods.fml.relauncher.SideOnly
import cpw.mods.fml.relauncher.Side
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import cpw.mods.fml.client.FMLClientHandler
import com.theenginerd.randomredstone.client.model.builder._
import com.theenginerd.randomredstone.client.model.builder.shapes._
import com.theenginerd.randomredstone.client.model.builder.shapes.Box
import com.theenginerd.randomredstone.client.model.builder.parts.torchPart

@SideOnly(Side.CLIENT)
object VariableSwitchModel
{
    private final val COBBLE_TEXTURE = new ResourceLocation("minecraft", "textures/blocks/cobblestone.png")
    private final val LEVER_TEXTURE = new ResourceLocation("minecraft", "textures/blocks/lever.png")
    private final val REDSTONE_TORCH_OFF_TEXTURE = new ResourceLocation("minecraft", "textures/blocks/redstone_torch_off.png")
    private final val REDSTONE_TORCH_ON_TEXTURE = new ResourceLocation("minecraft", "textures/blocks/redstone_torch_on.png")
    private final val VARIABLE_SWITCH_OFF_TEXTURE = new ResourceLocation("randomredstone", "textures/blocks/variable_switch_off.png")
    private final val VARIABLE_SWITCH_ON_TEXTURE = new ResourceLocation("randomredstone", "textures/blocks/variable_switch_on.png")

    private val model = new ModelBuilder()
                        .addPart("base")
                        {
                            _.addShape(Box(16, 2, 16))
                        }
                        .addPart("leverBox")
                        {
                            _.addShape(Box(4, 2, 8, x = -8, y = 4))
                        }
                        .addPart("lever", xOrigin = -4, yOrigin = 2)
                        {
                            _.addShape
                            {
                                Box(2, 10, 2)
                                .setSideInfo(BoxTop)(TextureRectangle(7, 8, 2, 2))
                            }
                        }
                        .addPart("torch", xOrigin = 4, yOrigin = 2, zOrigin = 5)(torchPart(5))
                        .toModel

    def render(isActive: Boolean, powerOutput: Byte)
    {
        model.drawParts("base")
        {
            part =>
                FMLClientHandler.instance().getClient.renderEngine.bindTexture(if (isActive) VARIABLE_SWITCH_ON_TEXTURE else VARIABLE_SWITCH_OFF_TEXTURE)
                part.drawAllFaceGroups(_.render())
        }

        model.drawParts("leverBox")
        {
            part =>
                FMLClientHandler.instance().getClient.renderEngine.bindTexture(COBBLE_TEXTURE)
                part.drawAllFaceGroups(_.render())
        }

        model.drawParts("lever")
        {
            part =>
                GL11.glPushMatrix()
                if (isActive)
                    GL11.glRotated(40, 1, 0, 0)
                else
                    GL11.glRotated(-40, 1, 0, 0)

                FMLClientHandler.instance().getClient.renderEngine.bindTexture(LEVER_TEXTURE)
                part.drawAllFaceGroups(_.render())

                GL11.glPopMatrix()
        }

        model.drawParts("torch")
        {
            part =>
                GL11.glPushMatrix()
                GL11.glTranslatef(0, 0, -0.625f * (powerOutput.toFloat / 15))

                FMLClientHandler.instance().getClient.renderEngine.bindTexture(if (isActive) REDSTONE_TORCH_ON_TEXTURE else REDSTONE_TORCH_OFF_TEXTURE)
                part.drawAllFaceGroups(_.render())

                GL11.glPopMatrix()
        }
    }
}
