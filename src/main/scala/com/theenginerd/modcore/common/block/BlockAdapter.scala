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

package com.theenginerd.modcore.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.World

abstract class BlockAdapter(material: Material) extends Block(material) with ModBlock
{
    /*
     * This section replaces all relevant methods from Block with calls to the ModBlock trait.
     * If the ModBlock method is not defined in a further derived trait, then the default implementation is used;
     * otherwise the implementation from the further derived trait is used.
     */
    override def canPlaceBlockOnSide(world: World, x: Int, y: Int, z: Int, metadata: Int): Boolean =
    {
        canBlockBePlacedOnSide(world, x, y, z, metadata)
    }

    /*
     * This section creates default implementations of the methods defined in ModBlock.  It defers the implementation to the super-class (Block).
     */
    def canBlockBePlacedOnSide(world: World, x: Int, y: Int, z: Int, metadata: Int) =
    {
        super.canPlaceBlockOnSide(world, x, y, z, metadata)
    }
}

