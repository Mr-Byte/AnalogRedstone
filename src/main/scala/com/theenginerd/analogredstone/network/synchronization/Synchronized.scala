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

package com.theenginerd.analogredstone.network.synchronization

import com.theenginerd.analogredstone.network.data.MappedProperties
import net.minecraft.network.packet.Packet250CustomPayload

trait Synchronized extends MappedProperties
{
    protected def buildSynchronizationPacket(properties: Seq[MappedPropertyCell]): Packet250CustomPayload
    protected def sendSynchronizationPacket(packet: => Packet250CustomPayload)

    def synchronized(properties: MappedPropertyCell*)(handler: => Unit = {}): Unit =
    {
        handler
        sendSynchronizationPacket(buildSynchronizationPacket(properties))
    }
}
