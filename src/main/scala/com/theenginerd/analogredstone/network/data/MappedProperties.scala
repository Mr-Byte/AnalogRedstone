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

package com.theenginerd.analogredstone.network.data

abstract class PropertyCell
{
    type Value

    protected var value: Value
    val id: Byte

    def unary_~ = value
    def :=(other: Value): Unit = value = other
}

trait MappedProperties
{
    private var lastPropertyId: Byte = 0
    private var propertyMap: Map[Byte, PropertyCell] = Map()

    def getPropertyById(id: Byte): Option[PropertyCell] = propertyMap.get(id)

    sealed abstract class MappedPropertyCell extends PropertyCell
    {
        type Value

        protected var value: Value
        val id: Byte = lastPropertyId

        propertyMap += (lastPropertyId -> this)
        lastPropertyId = (lastPropertyId + 1).toByte
    }

    case class BooleanPropertyCell(protected var value: Boolean) extends MappedPropertyCell
    {
        type Value = Boolean
    }

    case class BytePropertyCell(protected var value: Byte) extends MappedPropertyCell
    {
        type Value = Byte
    }

    case class ShortPropertyCell(protected var value: Short) extends MappedPropertyCell
    {
        type Value = Short
    }

    case class IntPropertyCell(protected var value: Int) extends MappedPropertyCell
    {
        type Value = Int
    }

    case class FloatPropertyCell(protected var value: Float) extends MappedPropertyCell
    {
        type Value = Float
    }
}
