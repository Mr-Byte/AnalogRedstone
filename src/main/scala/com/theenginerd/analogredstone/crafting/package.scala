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

package com.theenginerd.analogredstone

import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.item.ItemStack
import com.theenginerd.analogredstone.block.VariableSwitchBlock
import net.minecraft.init.{Blocks, Items}

package object crafting
{
    def registerRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(VariableSwitchBlock, 1),
                              "SRB",
                              "CCC",
                              'S': Character, new ItemStack(Items.stick),
                              'R': Character, new ItemStack(Items.redstone),
                              'B': Character, new ItemStack(Blocks.wooden_button),
                              'C': Character, new ItemStack(Blocks.cobblestone))
    }
}
