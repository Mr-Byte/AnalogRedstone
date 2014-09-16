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

import net.minecraft.block.material.Material

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

class define_block(material: Material) extends StaticAnnotation
{
    def macroTransform(annottees: Any*): Any = macro define_block.macroImpl
}

object define_block
{
    def macroImpl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] =
    {
        import c.universe._

        val result: Tree =
        {
            annottees.map(_.tree) match
            {
                case (blockTrait: ClassDef) :: Nil =>
                    try
                    {
                        val q"${_} trait ${traitName: TypeName} extends ..${_} { ..${_} }" = blockTrait
                        val q"new ${_}(material = $material).${_}(${_})" = c.macroApplication

                        val blockName = TermName(traitName.toString)
                        val companionObject = q"object $blockName extends com.theenginerd.core.common.block.ModBlockBase($material) with $traitName"

                        q"""
                           $blockTrait
                           $companionObject
                         """
                    }
                    catch
                        {
                            case _: MatchError =>
                                c.abort(c.enclosingPosition, "This annotation is only valid on trait types.")
                        }

                case _ =>
                    c.abort(c.enclosingPosition, "This type cannot be annotated as a block.")
            }
        }

        c.Expr[Any](result)
    }
}