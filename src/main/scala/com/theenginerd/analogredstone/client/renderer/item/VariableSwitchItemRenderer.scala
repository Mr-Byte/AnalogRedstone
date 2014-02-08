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

package com.theenginerd.analogredstone.client.renderer.item

import com.theenginerd.analogredstone.client.model.VariableSwitchModel
import net.minecraftforge.client.IItemRenderer
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer.{ItemRendererHelper, ItemRenderType}
import net.minecraftforge.client.IItemRenderer.ItemRenderType.{ENTITY, EQUIPPED, EQUIPPED_FIRST_PERSON, INVENTORY}
import org.lwjgl.opengl.GL11

object VariableSwitchItemRenderer extends IItemRenderer
{
    override def handleRenderType(item: ItemStack, renderType: ItemRenderType) = true

    override def shouldUseRenderHelper(renderType: ItemRenderType, item: ItemStack, helper: ItemRendererHelper) = true

    override def renderItem(renderType: ItemRenderType, item: ItemStack, data: AnyRef*)
    {
        renderType match
        {
            case ENTITY =>
                render(0.0f, 0.5f, 0.0f, 1.4f)

            case EQUIPPED =>
                render(0f, 0.25f, 0.25f, 1.4f)

            case EQUIPPED_FIRST_PERSON =>
                render(0.3f, 0.5f, 0.5f, 1.4f)

            case INVENTORY =>
                render(0.0f, -0.1f, 0.0f, 1.0f)

            case _ => ()
        }
    }

    private def render(x: Float, y: Float, z: Float, scale: Float)
    {
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_LIGHTING)

        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(x, y, z)

        VariableSwitchModel.render(isActive = false, 0)

        GL11.glEnable(GL11.GL_LIGHTING)
        GL11.glPopMatrix()
    }
}
