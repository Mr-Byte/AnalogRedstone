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

import com.theenginerd.randomredstone.client.utility.model.builder.shapes._
import com.theenginerd.randomredstone.client.utility.model.builder.shapes.Box
import com.theenginerd.randomredstone.client.utility.model.builder.shapes.TextureRectangle

package object parts
{
    def torchPart(torchHeight: Int)(partBuilder: PartBuilder): Unit =
    {
        val torchTopOffset = 2 * torchHeight
        val torchSideOffset = 2 * torchHeight - 4

        partBuilder.addShape
        {
            Box(2, torchHeight, 2)
            .setSideInfo(BoxLeft, BoxRight, BoxFront, BoxBack)(TextureRectangle(7, 10 - torchHeight, 2, torchHeight))
            .setSideInfo(BoxTop)(TextureRectangle(7, 8, 2, 2))
        }
        //Front
        .addShape(Plane(PlaneFront)(XAxis, 2, 1, y = torchTopOffset, z = 2, textureCoordinates = TextureRectangle(7, 10, 2, 1)))
        .addShape(Plane(PlaneFront)(XAxis, 1, 2, y = torchSideOffset, x = 3, z = 2, textureCoordinates = TextureRectangle(9, 8, 1, 2)))
        .addShape(Plane(PlaneFront)(XAxis, 1, 2, y = torchSideOffset, x = -3, z = 2, textureCoordinates = TextureRectangle(6, 8, 1, 2)))
        //Back
        .addShape(Plane(PlaneBack)(XAxis, 2, 1, y = torchTopOffset, z = -2, textureCoordinates = TextureRectangle(7, 10, 2, 1)))
        .addShape(Plane(PlaneBack)(XAxis, 1, 2, y = torchSideOffset, x = 3, z = -2, textureCoordinates = TextureRectangle(9, 8, 1, 2)))
        .addShape(Plane(PlaneBack)(XAxis, 1, 2, y = torchSideOffset, x = -3, z = -2, textureCoordinates = TextureRectangle(6, 8, 1, 2)))
        //Front
        .addShape(Plane(PlaneFront)(ZAxis, 2, 1, y = torchTopOffset, x = -2, textureCoordinates = TextureRectangle(7, 10, 2, 1)))
        .addShape(Plane(PlaneFront)(ZAxis, 1, 2, y = torchSideOffset, z = 3, x = -2, textureCoordinates = TextureRectangle(9, 8, 1, 2)))
        .addShape(Plane(PlaneFront)(ZAxis, 1, 2, y = torchSideOffset, z = -3, x = -2, textureCoordinates = TextureRectangle(6, 8, 1, 2)))
        //Back
        .addShape(Plane(PlaneBack)(ZAxis, 2, 1, y = torchTopOffset, x = 2, textureCoordinates = TextureRectangle(7, 10, 2, 1)))
        .addShape(Plane(PlaneBack)(ZAxis, 1, 2, y = torchSideOffset, z = 3, x = 2, textureCoordinates = TextureRectangle(9, 8, 1, 2)))
        .addShape(Plane(PlaneBack)(ZAxis, 1, 2, y = torchSideOffset, z = -3, x = 2, textureCoordinates = TextureRectangle(6, 8, 1, 2)))
    }
}
