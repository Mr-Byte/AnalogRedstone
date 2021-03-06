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

package com.theenginerd.randomredstone.common

import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import com.theenginerd.randomredstone.RandomRedstoneMod.MOD_ID
import net.minecraft.creativetab.CreativeTabs

package object block
{
    def registerBlock(block: Block) =
    {
        val name = block.getClass.getSimpleName.replace("Block", "").replace("$", "").toLowerCase
        GameRegistry.registerBlock(block.setBlockName(s"$MOD_ID:$name"), s"Block$name")
    }

    def registerBlocks()
    {
        registerBlock(VariableSwitchBlock)
        registerBlock(TestBlock)

        TestBlock.setCreativeTab(CreativeTabs.tabMisc)
        VariableSwitchBlock.setCreativeTab(CreativeTabs.tabRedstone)
    }
}
