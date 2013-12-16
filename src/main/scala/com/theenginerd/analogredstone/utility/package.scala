/*
 * Copyright 2013 Joshua R. Rodgers
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

package com.theenginerd.analogredstone

import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

package object utility
{
    def getBlockTileEntity[T <: TileEntity](world : World, position : (Int, Int, Int)) : Option[T] =
        Option(world.getBlockTileEntity(position._1, position._2, position._3).asInstanceOf[T])
}
