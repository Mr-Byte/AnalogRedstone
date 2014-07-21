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

package com.theenginerd.randomredstone.client.model.builder

import net.minecraft.util.Vec3

case class Normal(x: Float, y: Float, z: Float)
case class Vertex(position: (Double, Double, Double), uv: Option[(Float, Float)])

class Face(val vertices: IndexedSeq[Vertex])
{
    lazy val normal = {
        val (x0, y0, z0) = vertices(0).position
        val (x1, y1, z1) = vertices(1).position
        val (x2, y2, z2) = vertices(2).position

        val firstVector = Vec3.createVectorHelper(x1 - x0, y1 - y0, z1 - z0)
        val secondVector = Vec3.createVectorHelper(x2 - x0, y2 - y0, z2 - z0)
        val normal = (firstVector crossProduct secondVector).normalize

        Normal(normal.xCoord.toFloat, normal.yCoord.toFloat, normal.zCoord.toFloat)
    }
}

object Face
{
    def apply(vertices: Vertex*) = new Face(vertices.toVector)
}
