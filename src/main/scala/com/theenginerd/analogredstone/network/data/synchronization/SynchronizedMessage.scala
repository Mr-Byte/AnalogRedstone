package com.theenginerd.analogredstone.network.data.synchronization

import io.netty.buffer.ByteBuf

abstract class SynchronizedMessage
{
    var properties: Seq[Property]

    def writeToBuffer(buffer: ByteBuf)
    def readFromBuffer(buffer: ByteBuf)
}
