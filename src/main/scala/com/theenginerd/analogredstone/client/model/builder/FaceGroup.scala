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

package com.theenginerd.analogredstone.client.model.builder

import net.minecraft.client.renderer.Tessellator
import org.lwjgl.opengl.GL11

case class FaceGroup(name: Option[String], faces: Seq[Face])
{
    def render() =
    {
        val tessellator = Tessellator.instance

        tessellator.startDrawing(GL11.GL_TRIANGLES)

        for(face <- faces)
        {
            tessellator.setNormal(face.normal.x, face.normal.y, face.normal.z)

            for(vertex <- face.vertices)
            {
                val (x, y, z) = vertex.position

                vertex.uv match
                {
                    case Some((u, v)) => tessellator.addVertexWithUV(x, y, z, u, v)
                    case None => tessellator.addVertex(x, y, z)
                }
            }
        }

        tessellator.draw()
    }

    def combine(other: FaceGroup): Option[FaceGroup] =
    {
        other.name match
        {
            case `name` => Some(FaceGroup(name, faces ++ other.faces))
            case _ => None
        }
    }
}
