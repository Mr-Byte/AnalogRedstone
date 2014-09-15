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

package com.theenginerd.randomredstone.proxy

import com.theenginerd.randomredstone.client.item.renderer.VariableSwitchItemRenderer
import com.theenginerd.randomredstone.client.renderer.VariableSwitchTileEntityRenderer
import com.theenginerd.randomredstone.common.block.VariableSwitchBlock
import com.theenginerd.randomredstone.common.tileEntity.VariableSwitchTileEntity
import cpw.mods.fml.client.registry.ClientRegistry
import net.minecraft.item.Item
import net.minecraftforge.client.MinecraftForgeClient

class ClientModProxy extends ModProxy
{
    def setupRendering() =
    {
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[VariableSwitchTileEntity], VariableSwitchTileEntityRenderer)
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(VariableSwitchBlock), VariableSwitchItemRenderer)
    }

    override def registerTileEntities() =
    {
        super.registerTileEntities()

        setupRendering()
    }
}

object ClientModProxy
{
    final val NAME = "com.theenginerd.randomredstone.proxy.ClientModProxy"
}
