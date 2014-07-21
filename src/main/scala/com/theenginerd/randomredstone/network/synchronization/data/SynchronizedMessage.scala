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

package com.theenginerd.randomredstone.network.synchronization.data

import io.netty.buffer.ByteBuf
import cpw.mods.fml.common.FMLLog

abstract class SynchronizedMessage
{
    protected var properties: Iterable[Property]

    def getProperties = properties

    protected def writeHeaderToBuffer(buffer: ByteBuf)
    protected def readHeaderFromBuffer(buffer: ByteBuf)

    def writeToBuffer(buffer: ByteBuf) =
    {
        writeHeaderToBuffer(buffer)

        for(property <- properties)
        {
            buffer.writeByte(property.id)
            buffer.writeByte(property.typeId)
            writePropertyToBuffer(property, buffer)
        }
    }

    private def writePropertyToBuffer(property: Property, buffer: ByteBuf): Any =
    {
        property.value match
        {
            case value: Boolean => buffer.writeBoolean(value)
            case value: Byte => buffer.writeByte(value)
            case value: Short => buffer.writeShort(value)
            case value: Int => buffer.writeInt(value)
            case value: Float => buffer.writeFloat(value)
            case unexpected =>
                val typ = unexpected.getClass
                FMLLog severe s"Unexpected serialization type found: $typ."
        }
    }

    def readFromBuffer(buffer: ByteBuf) =
    {
        readHeaderFromBuffer(buffer)

        var parsedProperties: List[Property] = List()

        while(buffer.readableBytes() > 0)
        {
            val propertyId = buffer.readByte()
            val propertyType = buffer.readByte()
            val propertyValue = readPropertyFromBuffer(propertyType, buffer)

            parsedProperties :+= new Property(propertyId, propertyType, propertyValue)
        }

        properties = parsedProperties
    }

    private def readPropertyFromBuffer(propertyType: Byte, buffer: ByteBuf): AnyVal =
    {
        import com.theenginerd.randomredstone.synchronization.PropertyTypeIds._

        propertyType match
        {
            case BOOLEAN_ID => buffer.readBoolean()
            case BYTE_ID => buffer.readByte()
            case SHORT_ID => buffer.readShort()
            case INT_ID => buffer.readInt()
            case FLOAT_ID => buffer.readFloat()
            case unexpected =>
                FMLLog severe s"Unexpected property type id of $unexpected encountered."
        }
    }
}
