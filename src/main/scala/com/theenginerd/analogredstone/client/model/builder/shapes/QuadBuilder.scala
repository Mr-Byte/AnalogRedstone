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

package com.theenginerd.analogredstone.client.model.builder.shapes

import com.theenginerd.analogredstone.client.model.builder.{Vertex, Face}

trait QuadBuilder
{
    protected def buildQuad(vertices: IndexedSeq[(Double, Double, Double)], first: QuadCorner, second: QuadCorner, third: QuadCorner, fourth: QuadCorner, textureCoordinates: (Int, Int, Int, Int)): Seq[Face] =
    {
        val firstVertex = getVertex(vertices, first, textureCoordinates)
        val secondVertex = getVertex(vertices, second, textureCoordinates)
        val thirdVertex = getVertex(vertices, third, textureCoordinates)
        val fourthVertex = getVertex(vertices, fourth, textureCoordinates)

        Vector(Face(firstVertex, secondVertex, thirdVertex),
               Face(firstVertex, thirdVertex, fourthVertex))
    }

    private def getVertex(vertices: IndexedSeq[(Double, Double, Double)], corner: QuadCorner, textureCoordinates: (Int, Int, Int, Int)): Vertex =
    {
        val (left, bottom, width, height) = textureCoordinates

        lazy val uMin = left / 16F
        lazy val vMin = 1 - (bottom + height) / 16F
        lazy val uMax = (left + width) / 16F
        lazy val vMax = 1 - bottom / 16F

        corner match
        {
            case BottomLeft(index) =>
                Vertex(vertices(index), Some((uMin, vMax)))

            case TopLeft(index) =>
                Vertex(vertices(index), Some((uMin, vMin)))

            case BottomRight(index) =>
                Vertex(vertices(index), Some((uMax, vMax)))

            case TopRight(index) =>
                Vertex(vertices(index), Some((uMax, vMin)))

        }
    }

    sealed abstract class QuadCorner

    case class BottomLeft(index: Int) extends QuadCorner

    case class TopLeft(index: Int) extends QuadCorner

    case class BottomRight(index: Int) extends QuadCorner

    case class TopRight(index: Int) extends QuadCorner
}
