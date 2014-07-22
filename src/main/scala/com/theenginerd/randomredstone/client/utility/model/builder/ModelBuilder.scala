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

package com.theenginerd.randomredstone.client.utility.model.builder

import org.lwjgl.opengl.GL11
import com.theenginerd.randomredstone.client.utility.model.{Part, Model}

class ModelBuilder
{
    private var mutableParts: Map[String, Part] = Map()

    def addPart(name: String, xOrigin: Int = 0, yOrigin: Int = 0, zOrigin: Int = 0)(partBuilder: (PartBuilder) => Unit): ModelBuilder =
    {
        val builder = new PartBuilder(name, xOrigin, yOrigin, zOrigin)
        partBuilder(builder)
        mutableParts += (name -> builder.toPart)
        this
    }

    def toModel: Model =
        new Model
        {
            val parts = mutableParts

            def drawAllParts(partHandler: (Part) => Unit) =
                drawParts(parts.values, partHandler)

            def drawParts(partNames: String*)(partHandler: (Part) => Unit) =
                drawParts(for(key <- partNames; part <- parts.get(key)) yield part, partHandler)

            private def drawParts(partsToDraw: Iterable[Part], partHandler: (Part) => Unit)
            {
                GL11.glPushMatrix()
                GL11.glTranslatef(0.5f, 0, 0.5f)

                for(part <- partsToDraw)
                {
                    val (x, y, z) = part.origin

                    GL11.glPushMatrix()
                    GL11.glTranslatef(x, y, z)

                    partHandler(part)

                    GL11.glPopMatrix()
                }

                GL11.glPopMatrix()
            }
        }

}
