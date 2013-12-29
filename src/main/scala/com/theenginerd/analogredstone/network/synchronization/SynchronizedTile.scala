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

package com.theenginerd.analogredstone.network.synchronization

import net.minecraft.tileentity.TileEntity
import cpw.mods.fml.common.network.PacketDispatcher
import net.minecraft.network.packet.Packet250CustomPayload
import scala.reflect.runtime.universe._
import java.io.{DataInputStream, DataOutputStream, ByteArrayOutputStream}
import com.theenginerd.analogredstone
import scala.language.implicitConversions

trait SynchronizedTile
{
    self: TileEntity =>
    implicit def position = (xCoord, yCoord, zCoord)

    private var lastId: Short = 0
    private var propertyMap: Map[Short, SynchronizedProperty[Any]] = Map()

    class SynchronizedProperty[T](private var value: T)(implicit val tag: TypeTag[T])
    {
        val propertyId = lastId
        def unary_~ = value
        def @=(other: T) = value = other

        propertyMap = propertyMap + (lastId -> this.asInstanceOf[SynchronizedProperty[Any]])
        lastId = (lastId + 1).toShort
    }

    object SynchronizedProperty
    {
        def asBoolean(value: Boolean) = new SynchronizedProperty[Boolean](value)
        def asByte(value: Byte) = new SynchronizedProperty[Byte](value)
        def asShort(value: Short) = new SynchronizedProperty[Short](value)
        def asInt(value: Int) = new SynchronizedProperty[Int](value)

        implicit def toValue[T](value: SynchronizedProperty[T]): T = ~value
    }

    def synchronized(properties: SynchronizedProperty[_]*)(handler: => Unit = {}): Unit =
    {
        handler

        if(!worldObj.isRemote)
        {
            PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 64, worldObj.provider.dimensionId, buildUpdatePacket(properties))
        }
    }

    def handleUpdate(dataStream: DataInputStream)
    {
        while(dataStream.available() > 0)
        {
            val propertyId = dataStream.readShort()
            val typeId = dataStream.readShort()

            propertyMap(propertyId) @= readPropertyByType(typeId, dataStream).get
        }
    }

    def readPropertyByType(typeId: Short, dataStream: DataInputStream): Option[Any] =
    {
        typeId match
        {
            case 0 => Some(dataStream.readBoolean())
            case 1 => Some(dataStream.readByte())
            case 2 => Some(dataStream.readShort())
            case 3 => Some(dataStream.readInt())
            case _ => None
        }
    }

    protected def buildUpdatePacket(properties: Seq[SynchronizedProperty[_]]): Packet250CustomPayload =
    {
        val byteStream = new ByteArrayOutputStream()
        val dataStream = new DataOutputStream(byteStream)
        val (x, y, z) = position

        dataStream.writeShort(actionIds.TILE_SYNCHRONIZATION_ACTION)
        dataStream.writeInt(x)
        dataStream.writeInt(y)
        dataStream.writeInt(z)

        for(property <- properties)
        {
            for(typeId <- getTypeId(property.tag))
            {
                dataStream.writeShort(property.propertyId)
                dataStream.writeShort(typeId)
                writePropertyByType(dataStream, typeId, ~property)
            }
        }

        val data = byteStream.toByteArray
        dataStream.close()
        byteStream.close()

        val packet250 = new Packet250CustomPayload()
        packet250.channel = analogredstone.MOD_ID
        packet250.data = data
        packet250.length = data.length
        packet250.isChunkDataPacket = true

        packet250
    }

    protected def writePropertyByType(dataStream: DataOutputStream, typeId: Short, value: Any) =
    {
        typeId match
        {
            case 0 => dataStream.writeBoolean(value.asInstanceOf[Boolean])
            case 1 => dataStream.writeByte(value.asInstanceOf[Byte])
            case 2 => dataStream.writeShort(value.asInstanceOf[Short])
            case 3 => dataStream.writeInt(value.asInstanceOf[Int])
        }
    }

    protected def getTypeId(typeTag: TypeTag[_]): Option[Short] =
        typeTag.tpe match
        {
            case tpe if tpe =:= typeOf[Boolean] => Some(0: Short)
            case tpe if tpe =:= typeOf[Byte] => Some(1: Short)
            case tpe if tpe =:= typeOf[Short] => Some(2: Short)
            case tpe if tpe =:= typeOf[Int] => Some(3: Short)
            case _ => None
        }
}
