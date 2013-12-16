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

import cpw.mods.fml.common.{SidedProxy, FMLLog, Mod}
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLPreInitializationEvent, FMLInitializationEvent}
import cpw.mods.fml.common.network.NetworkMod

import com.theenginerd.analogredstone.proxy.{ModProxy, ClientModProxy, ServerModProxy}
import com.theenginerd.analogredstone.network.PacketHandler

@Mod(name = MOD_NAME, modid = MOD_ID, version = "1.0", modLanguage = "scala")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = Array(MOD_ID), packetHandler = classOf[PacketHandler])
object AnalogRedstoneMod
{
    @SidedProxy(clientSide = ClientModProxy.NAME, serverSide = ServerModProxy.NAME)
    var proxy: ModProxy = null

    @EventHandler
    def preInit(event: FMLPreInitializationEvent)
    {
        FMLLog info s"Preparing to load $MOD_NAME."
        FMLLog info s"Loaded the proxy $proxy."

        block.registerBlocks
    }

    @EventHandler
    def init(event: FMLInitializationEvent)
    {
        FMLLog info s"Loading $MOD_NAME"

        proxy.registerTileEntities
        proxy.setupRendering()
    }

    @EventHandler
    def postInit(event: FMLPostInitializationEvent)
    {
        FMLLog info s"Finished loading $MOD_NAME."
    }
}
