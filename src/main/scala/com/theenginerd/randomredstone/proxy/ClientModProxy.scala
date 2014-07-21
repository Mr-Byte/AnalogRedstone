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

import cpw.mods.fml.client.registry.{RenderingRegistry, ClientRegistry}
import com.theenginerd.randomredstone.tileentity.VariableSwitchTileEntity
import com.theenginerd.randomredstone.client.renderer.tileentity.VariableSwitchTileEntityRenderer
import com.theenginerd.randomredstone.client.renderer.item.VariableSwitchItemRenderer
import com.theenginerd.randomredstone.block.VariableSwitchBlock

import net.minecraftforge.client.MinecraftForgeClient
import net.minecraft.item.Item

class ClientModProxy extends ModProxy
{
    override def setupRendering() =
    {
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(VariableSwitchBlock), VariableSwitchItemRenderer)
    }

    override def registerTileEntities() =
    {
        super.registerTileEntities()

        ClientRegistry.bindTileEntitySpecialRenderer(classOf[VariableSwitchTileEntity], VariableSwitchTileEntityRenderer)
    }
}

object ClientModProxy
{
    final val NAME = "com.theenginerd.randomredstone.proxy.ClientModProxy"
}
