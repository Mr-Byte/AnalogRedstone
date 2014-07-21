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

package com.theenginerd.randomredstone.client.model.builder.shapes

import com.theenginerd.randomredstone.client.model.builder._
import com.theenginerd.randomredstone.client.model.builder.FaceGroup

case class Box(width: Int, height: Int, depth: Int, x: Int = 0, y: Int = 0, z: Int = 0)
    extends Shape
    with QuadBuilder
{
    private val vertices = Vector(
        //Bottom
        ((-width + x) / 32D, y / 32D, (-depth + z) / 32D),
        ((-width + x) / 32D, y / 32D, (depth + z) / 32D),
        ((width + x) / 32D, y / 32D, (depth + z) / 32D),
        ((width + x) / 32D, y / 32D, (-depth + z) / 32D),
        //Top
        ((-width + x) / 32D, (2 * height + y) / 32D, (-depth + z) / 32D),
        ((-width + x) / 32D, (2 * height + y) / 32D, (depth + z) / 32D),
        ((width + x) / 32D, (2 * height + y) / 32D, (depth + z) / 32D),
        ((width + x) / 32D, (2 * height + y) / 32D, (-depth + z) / 32D)
    )

    private def defaultTextureCoordinates(side: BoxSide): (Int, Int, Int, Int) =
        side match
        {
            case BoxFront | BoxBack =>
                (x + 8 - width / 2, y, width, height)

            case BoxLeft | BoxRight =>
                (z + 8 - depth / 2, y, depth, height)

            case BoxTop | BoxBottom =>
                (x + 8 - width / 2, z + 8 - depth / 2, width, depth)

            case _ =>
                (0, 0, 16, 16)
        }

    private var sides: Map[BoxSide, SideInfo] = Map(BoxTop -> SideInfo(TextureDefault, None),
                                                    BoxBottom -> SideInfo(TextureDefault, None),
                                                    BoxLeft -> SideInfo(TextureDefault, None),
                                                    BoxRight -> SideInfo(TextureDefault, None),
                                                    BoxFront -> SideInfo(TextureDefault, None),
                                                    BoxBack -> SideInfo(TextureDefault, None))

    def setSideInfo(boxSides: BoxSide*)(textureCoordinates: TextureCoordinates = TextureDefault, groupName: Option[String] = None): Box =
    {
        sides ++= boxSides.map(_ -> SideInfo(textureCoordinates, groupName))
        this
    }

    def toFaceGroups: Seq[FaceGroup] =
    {
        var faceGroups: Map[Option[String], Seq[Face]] = Map()

        for ((side, sideInfo) <- sides)
        {
            val faceGroupName = sideInfo.faceGroupName
            val faces = getFacesForSide(side, sideInfo)
            faceGroups += faceGroupName -> faceGroups.get(faceGroupName).map(_ ++ faces).getOrElse(faces)
        }

        (for ((group, faces) <- faceGroups) yield FaceGroup(group, faces)).toSeq
    }

    private def getFacesForSide(side: BoxSide, sideInfo: SideInfo): Seq[Face] =
    {
        val textureCoordinates = sideInfo.textureCoordinates.getOrElse(defaultTextureCoordinates(side))

        side match
        {
            case BoxBottom => buildQuad(vertices, TopLeft(0), TopRight(3), BottomRight(2), BottomLeft(1), textureCoordinates)
            case BoxTop => buildQuad(vertices, TopLeft(4), BottomLeft(5), BottomRight(6), TopRight(7), textureCoordinates)
            case BoxLeft => buildQuad(vertices, BottomLeft(0), BottomRight(1), TopRight(5), TopLeft(4), textureCoordinates)
            case BoxRight => buildQuad(vertices, BottomRight(2), BottomLeft(3), TopLeft(7), TopRight(6), textureCoordinates)
            case BoxBack => buildQuad(vertices, BottomLeft(0), TopLeft(4), TopRight(7), BottomRight(3), textureCoordinates)
            case BoxFront => buildQuad(vertices, BottomLeft(1), BottomRight(2), TopRight(6), TopLeft(5), textureCoordinates)
        }
    }
}
