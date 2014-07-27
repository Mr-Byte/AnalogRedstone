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

package com.theenginerd.randomredstone.client.model

import cpw.mods.fml.relauncher.SideOnly
import cpw.mods.fml.relauncher.Side
import net.minecraft.util.ResourceLocation
import com.theenginerd.modcore.client.model.builder.shapes._
import com.theenginerd.modcore.client.model.builder.shapes.Box
import com.theenginerd.modcore.client.model.builder.parts.TorchPart
import com.theenginerd.modcore.client.model.{Part, Model}
import cpw.mods.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
object VariableSwitchModel extends Model
{
    private final val COBBLE_TEXTURE = new ResourceLocation("minecraft", "textures/blocks/cobblestone.png")
    private final val LEVER_TEXTURE = new ResourceLocation("minecraft", "textures/blocks/lever.png")
    private final val REDSTONE_TORCH_OFF_TEXTURE = new ResourceLocation("minecraft", "textures/blocks/redstone_torch_off.png")
    private final val REDSTONE_TORCH_ON_TEXTURE = new ResourceLocation("minecraft", "textures/blocks/redstone_torch_on.png")
    private final val VARIABLE_SWITCH_OFF_TEXTURE = new ResourceLocation("randomredstone", "textures/blocks/variable_switch_off.png")
    private final val VARIABLE_SWITCH_ON_TEXTURE = new ResourceLocation("randomredstone", "textures/blocks/variable_switch_on.png")

    addPart withName "base" withShapes new Part {
        addShape { Box(width = 15, height = 2, depth = 16) }
    }

    addPart withName "leverBox" withShapes new Part {
        addShape { Box(4, 2, 8, x = -8, y = 4) }
    }

    addPart withName "lever" atOrigin (-4.0f, 2.0f, 0.0f) withShapes new Part {
        addShape
        {
            Box(2, 10, 2)
                .setSideInfo(BoxTop)(TextureRectangle(7, 8, 2, 2))
        }
    }

    addPart withName "torch" atOrigin(4.0f, 2.0f, 5.0f) withShapes new TorchPart(5)

    def render(isActive: Boolean, powerOutput: Byte)
    {
        drawParts("base")
        {
            part =>
                FMLClientHandler.instance().getClient.renderEngine.bindTexture(if (isActive) VARIABLE_SWITCH_ON_TEXTURE else VARIABLE_SWITCH_OFF_TEXTURE)
                part.drawAllFaceGroups(_.render())
        }

        drawParts("leverBox")
        {
            part =>
                FMLClientHandler.instance().getClient.renderEngine.bindTexture(COBBLE_TEXTURE)
                part.drawAllFaceGroups(_.render())
        }

        drawParts("lever")
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

        drawParts("torch")
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
