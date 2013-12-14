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

package com.theenginerd.analogredstone.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.World
import net.minecraftforge.common.ForgeDirection
import net.minecraftforge.common.ForgeDirection.{DOWN, UP, NORTH, SOUTH, WEST, EAST}
import net.minecraft.creativetab.CreativeTabs

object VariableSwitchBlock extends Block(VARIABLE_SWITCH_ID, Material.circuits)
{
    setCreativeTab(CreativeTabs.tabRedstone)

    override def isOpaqueCube = false

    override def canPlaceBlockOnSide(world: World, positionX: Int, positionY: Int, positionZ: Int, orientation: Int): Boolean =
    {
        val direction = ForgeDirection.getOrientation(orientation)

        (direction == DOWN && world.isBlockSolidOnSide(positionX, positionY+1, positionZ, DOWN)) ||
        (direction == UP && world.isBlockSolidOnSide(positionX, positionY-1, positionZ, UP)) ||
        (direction == NORTH && world.isBlockSolidOnSide(positionX, positionY, positionZ+1, NORTH)) ||
        (direction == SOUTH && world.isBlockSolidOnSide(positionX, positionY, positionZ-1, SOUTH)) ||
        (direction == WEST && world.isBlockSolidOnSide(positionX+1, positionY, positionZ, WEST)) ||
        (direction == EAST && world.isBlockSolidOnSide(positionX-1, positionY, positionZ, EAST))
    }

    override def canProvidePower = true
}
