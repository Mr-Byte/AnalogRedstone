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

trait MappedRefCells
{
    private var lastRefCellId: Byte = 0
    protected var refCellMap: Map[Byte, RefCell] = Map()

    sealed abstract class RefCell
    {
        type Value

        protected var value: Value
        val id = lastRefCellId

        def unary_~ = value
        def :=(other: Value): Unit = value = other

        refCellMap += (lastRefCellId -> this)
        lastRefCellId = (lastRefCellId + 1).toByte
    }

    case class BooleanRefCell(protected var value: Boolean) extends RefCell
    {
        type Value = Boolean
    }

    case class ByteRefCell(protected var value: Byte) extends RefCell
    {
        type Value = Byte
    }

    case class ShortRefCell(protected var value: Short) extends RefCell
    {
        type Value = Short
    }

    case class IntRefCell(protected var value: Int) extends RefCell
    {
        type Value = Int
    }

    case class FloatRefCell(protected var value: Float) extends RefCell
    {
        type Value = Float
    }
}
