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

package com.theenginerd.core.client.model

import org.lwjgl.opengl.GL11

trait Model
{
    private var parts: Map[Option[String], Part] = Map()

    protected trait PartBuilder
    {
        var name: Option[String] = None
        var origin: (Float, Float, Float) = (0, 0, 0)

        def atOrigin(origin: (Float, Float, Float)): PartBuilder =
        {
            val (x, y, z) = origin
            this.origin = (2*x/32F, y/16F, 2*z/32F)
            this
        }

        def withShapes(part: Part) =
        {
            parts += (name -> part.copy(origin))
        }
    }

    protected def addPart = new PartBuilder {
        def withName(name: String): PartBuilder =
        {
            this.name = Some(name)
            this
        }
    }

    def drawAllParts(partHandler: (Part) => Unit): Unit =
    {
        drawParts(parts.values, partHandler)
    }

    def drawParts(partNames: String*)(partHandler: (Part) => Unit): Unit =
    {
        drawParts(for (key <- partNames; part <- parts.get(Some(key))) yield part, partHandler)
    }

    private def drawParts(partsToDraw: Iterable[Part], partHandler: (Part) => Unit): Unit =
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
