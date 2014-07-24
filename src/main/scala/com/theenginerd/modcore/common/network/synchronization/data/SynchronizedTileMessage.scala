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

package com.theenginerd.modcore.common.network.synchronization.data

import io.netty.buffer.ByteBuf

class SynchronizedTileMessage(var x: Int, var y: Int, var z: Int, var properties: Iterable[Property]) extends SynchronizedMessage
{
    def this() = this(0, 0, 0, List())

    protected override def writeHeaderToBuffer(buffer: ByteBuf) =
    {
        buffer.writeInt(x)
        buffer.writeInt(y)
        buffer.writeInt(z)
    }

    protected override def readHeaderFromBuffer(buffer: ByteBuf) =
    {
        x = buffer.readInt()
        y = buffer.readInt()
        z = buffer.readInt()
    }
}
