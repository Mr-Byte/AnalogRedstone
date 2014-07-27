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

package com.theenginerd.modcore.client.model

import com.theenginerd.modcore.client.model.builder.FaceGroup
import com.theenginerd.modcore.client.model.builder.shapes.Shape

case class Part(origin: (Float, Float, Float) = (0, 0, 0), private var faceGroups: Map[Option[String], FaceGroup] = Map())
{
    protected def addShape(shape: Shape) =
    {
        val shapeFaceGroups = shape.toFaceGroups

        for(faceGroup <- shapeFaceGroups)
        {
            val faceGroupName = faceGroup.name
            faceGroups += (faceGroupName -> faceGroups.get(faceGroupName)
                                                      .flatMap(_ combine faceGroup)
                                                      .getOrElse(faceGroup))
        }
    }

    def drawFaceGroups(textureGroupNames: Option[String]*)(textureGroupHandler: (FaceGroup) => Unit) =
        textureGroupNames.foreach(faceGroups.get(_).map(textureGroupHandler))

    def drawAllFaceGroups(textureGroupHandler: (FaceGroup) => Unit) =
        faceGroups.foreach { case (_, group) => textureGroupHandler(group) }
}
