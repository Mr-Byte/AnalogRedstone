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

import cpw.mods.fml.common.{SidedProxy, FMLLog, Mod}
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLPreInitializationEvent, FMLInitializationEvent}
import java.io.File

import com.theenginerd.analogredstone.proxy.{ModProxy, ClientModProxy, ServerModProxy}

@Mod(name = MOD_NAME, modid = MOD_ID, version = "1.1.0-1.7.2", modLanguage = "scala")
object AnalogRedstoneMod
{
    @SidedProxy(clientSide = ClientModProxy.NAME, serverSide = ServerModProxy.NAME)
    var proxy: ModProxy = null

    @EventHandler
    def preInit(event: FMLPreInitializationEvent)
    {
        FMLLog info s"Preparing to load $MOD_NAME."
        FMLLog info s"Loaded the proxy $proxy."

        loadConfiguration(event)

        block.registerBlocks()
        crafting.registerRecipes()
    }

    def loadConfiguration(event: FMLPreInitializationEvent)
    {
        val configuration = new AnalogRedstoneConfiguration(new File(event.getModConfigurationDirectory, "analogredstone/main.conf"))

        try
        {
            configuration.load()
        }
        catch
        {
            case exception: Exception =>
                FMLLog warning s"$MOD_NAME failed to load its configuration."
        }
        finally
        {
            configuration.save()
        }
    }

    @EventHandler
    def init(event: FMLInitializationEvent)
    {
        FMLLog info s"Loading $MOD_NAME"

        proxy.registerTileEntities()
        proxy.setupRendering()
    }

    @EventHandler
    def postInit(event: FMLPostInitializationEvent)
    {
        FMLLog info s"Finished loading $MOD_NAME."
    }
}
