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

package main.scala.com.theenginerd.analogredstone.client.model

import cpw.mods.fml.relauncher.SideOnly
import cpw.mods.fml.relauncher.Side
import net.minecraftforge.client.model.AdvancedModelLoader
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import cpw.mods.fml.common.FMLLog

@SideOnly(Side.CLIENT)
object VariableSwitchModel
{
    private lazy val resource = new ResourceLocation("/models/VariableSwitch.obj")
    private lazy val model = AdvancedModelLoader.loadModel(resource.getResourcePath)

    def render(isActive: Boolean, powerOutput: Int)
    {
        model.renderPart("Base")

        GL11.glPushMatrix()
        GL11.glTranslatef(0, 0, -0.625f * (powerOutput.toFloat / 15))
        model.renderPart("PowerAdjuster")
        GL11.glPopMatrix()

        GL11.glPushMatrix()

        if (isActive)
            GL11.glRotated(-30, 1, 0, 0)
        else
            GL11.glRotated(30, 1, 0, 0)

        model.renderPart("Switch")

        GL11.glPopMatrix()
    }
}
