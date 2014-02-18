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

import com.theenginerd.analogredstone.client.model.builder.{Face, FaceGroup}

class Plane(axis: Axis, width: Int, height: Int, x: Int, y: Int, z: Int )
    extends Shape
    with QuadBuilder
{
    private val vertices: Vector[(Double, Double, Double)] = axis match
    {
        case XAxis =>
            Vector(
                ((-width + x) / 32D, (2 * height + y) / 32D, z / 32D),
                ((width + x) / 32D, (2 * height + y) / 32D, z / 32D),
                ((width + x) / 32D, y / 32D, z / 32D),
                ((-width + x) / 32D, y / 32D, z / 32D)
            )

        case YAxis =>
            Vector(
                ((-width + x) / 32D, y / 32D, (-height + z) / 32D),
                ((width + x) / 32D, y / 32D, (height + z) / 32D),
                ((width + x) / 32D, y / 32D, (height + z) / 32D),
                ((-width + x) / 32D, y / 32D, (-height + z) / 32D)
            )

        case ZAxis =>
            Vector(
                (x / 32D, (2 * height + y) / 32D, (-width + z) / 32D),
                (x / 32D, (2 * height + y) / 32D, (width + z) / 32D),
                (x / 32D, y / 32D, (width + z) / 32D),
                (x / 32D, y / 32D, (-width + z) / 32D)
            )
    }

    private var planeSides: Map[PlaneSide, SideInfo] = Map()

    private def defaultTextureCoordinates(): (Int, Int, Int, Int) =
        axis match
        {
            case XAxis => (x / 2 + 8 - width / 2, y / 2, width, height)
            case YAxis => (x / 2 + 8 - width / 2, z / 2 + 8 - height / 2, width, height)
            case ZAxis => (z / 2 + 8 - width / 2, y / 2, width, height)
        }

    def setSideInfo(sides: PlaneSide*)(textureCoordinates: TextureCoordinates = TextureDefault, groupName: Option[String] = None): Plane =
    {
        planeSides ++= sides.map(_ -> SideInfo(textureCoordinates, groupName))
        this
    }

    override def toFaceGroups: Seq[FaceGroup] =
    {
        var faceGroups: Map[Option[String], Seq[Face]] = Map()

        for ((side, sideInfo) <- planeSides)
        {
            val faceGroupName = sideInfo.faceGroupName
            val faces = getFacesForSide(side, sideInfo)
            faceGroups += faceGroupName -> faceGroups.get(faceGroupName).map(_ ++ faces).getOrElse(faces)
        }

        (for ((group, faces) <- faceGroups) yield FaceGroup(group, faces)).toSeq
    }

    private def getFacesForSide(side: PlaneSide, sideInfo: SideInfo): Seq[Face] =
    {
        val textureCoordinates = sideInfo.textureCoordinates.getOrElse(defaultTextureCoordinates())

        side match
        {
            case PlaneBack => buildQuad(vertices, TopLeft(0), TopRight(1), BottomRight(2), BottomLeft(3), textureCoordinates)
            case PlaneFront => buildQuad(vertices, BottomLeft(3), BottomRight(2), TopRight(1), TopLeft(0), textureCoordinates)
        }
    }
}

object Plane
{
    /**
     * Generates a plane that includes the specified faces.
     * @param sides The faces to generate a quad for (PlaneFront, Plane Back)
     * @param axis The axis to generate the plane along.
     * @param width The width of the plane (in units of 1/16).
     * @param height The height of the plane (in units of 1/16).
     * @param x The x-position of the plane (in units of 1/32).
     * @param y The y-position of the plane (in units of 1/32).
     * @param z The z-position of the plane (in units of 1/32).
     * @param textureCoordinates TextureCoordinates for the plane.
     * @param faceGroup The face group the plane faces belong to.
     * @return A new plane.
     */
    def apply(sides: PlaneSide*)(axis: Axis, width: Int, height: Int, x: Int = 0, y: Int = 0, z: Int = 0, textureCoordinates: TextureCoordinates = TextureDefault, faceGroup: Option[String] = None) =
    {
        new Plane(axis, width, height, x, y, z).setSideInfo(sides: _*)(textureCoordinates, faceGroup)
    }
}
