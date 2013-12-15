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

package analogredstone.client.renderer.tileentity

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.client.renderer.Tessellator
import com.theenginerd.analogredstone.tileentity.VariableSwitchTileEntity
import org.lwjgl.opengl.GL11

object VariableSwitchTileEntityRenderer extends TileEntitySpecialRenderer
{
    def renderTileEntityAt(tileentity: TileEntity, x: Double, y: Double, z: Double, tick: Float): Unit =
    {
        val tessellator = Tessellator.instance
        val variableSwitch = tileentity.asInstanceOf[VariableSwitchTileEntity]

        GL11.glPushMatrix()

        GL11.glTranslatef(x.toFloat, y.toFloat, z.toFloat)

        drawBase(tessellator)

        GL11.glPopMatrix()
    }

    def drawBase(tessellator: Tessellator)
    {
        tessellator.startDrawingQuads()

        val uMin = 0
        val uMax = 0
        val vMin = 0
        val vMax = 0

        //Front
        tessellator.addVertexWithUV(0, 0, 0, uMax, 0)
        tessellator.addVertexWithUV(0, 0.125, 0, uMax, 0.125)
        tessellator.addVertexWithUV(1, 0.125, 0, 0, 0.125)
        tessellator.addVertexWithUV(1, 0, 0, 0, 0)

        //Top
        tessellator.addVertexWithUV(0, 0.125, 0, uMax, vMin)
        tessellator.addVertexWithUV(0, 0.125, 1, uMax, vMax)
        tessellator.addVertexWithUV(1, 0.125, 1, uMin, vMax)
        tessellator.addVertexWithUV(1, 0.125, 0, uMin, vMin)

        tessellator.draw()
    }
}
