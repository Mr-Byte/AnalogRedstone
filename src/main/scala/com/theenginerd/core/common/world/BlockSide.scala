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

package com.theenginerd.core.common.world

object BlockSide
{
    implicit def Int2BlockSide(value: Int) =
    {
        value match
        {
            case 0 => Bottom
            case 1 => Top
            case 2 => North
            case 3 => South
            case 4 => West
            case 5 => East
            case unknown => Unknown(value)
        }
    }

    implicit def BlockSide2Int(blockSide: BlockSide) =
    {
        blockSide.value
    }
}

sealed trait BlockSide
{
    val value: Int
}


case object North extends BlockSide
{
    val value = 2
}

case object South extends BlockSide
{
    val value = 3
}

case object West extends BlockSide
{
    val value = 4
}

case object East extends BlockSide
{
    val value = 5
}

case object Top extends BlockSide
{
    val value = 1
}

case object Bottom extends BlockSide
{
    val value = 0
}

case class Unknown(value: Int) extends BlockSide