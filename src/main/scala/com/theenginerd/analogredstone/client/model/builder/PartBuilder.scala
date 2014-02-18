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

import com.theenginerd.analogredstone.client.model.builder.shapes.Shape
import com.theenginerd.analogredstone.client.model.Part

class PartBuilder(private val partName: String, private val xOrigin: Int, private val yOrigin: Int, private val zOrigin: Int)
{
    private var mutableFaceGroups: Map[Option[String], FaceGroup] = Map()

    def addShape(shape: Shape): PartBuilder =
    {
        val faceGroups = shape.toFaceGroups

        for(faceGroup <- faceGroups)
        {
            val faceGroupName = faceGroup.name
            mutableFaceGroups += (faceGroupName -> mutableFaceGroups.get(faceGroupName).flatMap(_ combine faceGroup).getOrElse(faceGroup))
        }

        this
    }

    def toPart: Part =
        new Part
        {
            val origin = (2*xOrigin/32F, yOrigin/16F, 2*zOrigin/32F)

            val faceGroups = mutableFaceGroups

            val name = partName

            def drawFaceGroups(textureGroupNames: Option[String]*)(textureGroupHandler: (FaceGroup) => Unit) =
                textureGroupNames.foreach(faceGroups.get(_).map(textureGroupHandler))

            def drawAllFaceGroups(textureGroupHandler: (FaceGroup) => Unit) =
                faceGroups.foreach { case (_, group) => textureGroupHandler(group) }
        }
}
