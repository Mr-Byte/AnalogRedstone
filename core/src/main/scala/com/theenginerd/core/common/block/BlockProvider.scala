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

package com.theenginerd.core.common.block

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

object BlockProvider
{
    def apply(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] =
    {
        import c.universe._

        c.info(c.enclosingPosition, "Hello, world!", true)

        val result: Tree =
        {
            annottees.map(_.tree) match
            {
                case (blockTrait: ClassDef) :: Nil =>
                    try
                    {
                        val q"$flags trait $blockName extends ..$bases { ..$body }" = blockTrait
                    }
                    catch
                    {
                        case _: MatchError =>
                            c.abort(c.enclosingPosition, "This annotation is only valid on trait types.")
                    }

                    q"$blockTrait"

                case _ =>
                    c.abort(c.enclosingPosition, "This type cannot be annotated as a block.")
            }
        }

        c.Expr[Any](result)
    }
}
