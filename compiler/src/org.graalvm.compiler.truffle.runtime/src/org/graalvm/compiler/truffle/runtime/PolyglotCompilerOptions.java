/*
 * Copyright (c) 2017, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.graalvm.compiler.truffle.runtime;

import org.graalvm.collections.EconomicMap;
import org.graalvm.collections.Equivalence;
import org.graalvm.options.OptionCategory;
import org.graalvm.options.OptionDescriptors;
import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionValues;
import org.graalvm.polyglot.Engine;

import com.oracle.truffle.api.Option;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * Compiler options that can be configured per {@link Engine engine} instance.
 */
@Option.Group("compiler")
public final class PolyglotCompilerOptions {

    // @formatter:off

    // USER OPTIONS

    // EXPERT OPTIONS

    @Option(help = "Minimum number of invocations or loop iterations needed to compile a guest language root.",
                    category = OptionCategory.EXPERT)
    public static final OptionKey<Integer> CompilationThreshold = new OptionKey<>(1000);

    @Option(help = "Minimum number of invocations or loop iterations needed to compile a guest language root in low tier mode.",
            category = OptionCategory.EXPERT)
    public static final OptionKey<Integer> FirstTierCompilationThreshold = new OptionKey<>(100);

    /*
     * TODO planned options:
     *
    @Option(help = "Enable automatic inlining of guest language roots.",
                    category = OptionCategory.EXPERT)
    public static final OptionKey<Boolean> InliningEnabled = new OptionKey<>(true);

    @Option(help = "Maximum number of inlined non-trivial AST nodes per compilation unit.",
                    category = OptionCategory.EXPERT)
    public static final OptionKey<Integer> InliningNodeBudget = new OptionKey<>(2250);

    @Option(help = "Maximum depth for recursive inlining.",
                    category = OptionCategory.EXPERT)
    public static final OptionKey<Integer> InliningRecursionDepth = new OptionKey<>(4);

    @Option(help = "Enable automatic duplication of compilation profiles (splitting).",
                    category = OptionCategory.EXPERT)
    public static final OptionKey<Boolean> SplittingEnabled = new OptionKey<>(true);

    @Option(help = "Enable automatic on-stack-replacement of loops.",
                    category = OptionCategory.EXPERT)
    public static final OptionKey<Boolean> OSREnabled = new OptionKey<>(true);

    // DEBUG OPTIONS

    @Option(help = "Trace compilation decisions to the standard output.",
                    category = OptionCategory.DEBUG)
    public static final OptionKey<Boolean> TraceCompilation = new OptionKey<>(false);

    @Option(help = "Trace inlining decisions to the standard output.",
                    category = OptionCategory.DEBUG)
    public static final OptionKey<Boolean> TraceInlining = new OptionKey<>(false);

    @Option(help = "Trace splitting decisions to the standard output.",
                    category = OptionCategory.DEBUG)
    public static final OptionKey<Boolean> TraceSplitting = new OptionKey<>(false);

    @Option(help = "Trace deoptimization of compilation units.",
                    category = OptionCategory.DEBUG)
    public static final OptionKey<Boolean> TraceDeoptimization = new OptionKey<>(false);
    */

    // @formatter:on

    private static final EconomicMap<OptionKey<?>, OptionKey<?>> POLYGLOT_TO_TRUFFLE = EconomicMap.create(Equivalence.IDENTITY);
    static {
        initializePolyglotToGraalMapping();
    }

    private static void initializePolyglotToGraalMapping() {
        POLYGLOT_TO_TRUFFLE.put(CompilationThreshold, SharedTruffleRuntimeOptions.TruffleCompilationThreshold);
    }

    static OptionValues getPolyglotValues(RootNode root) {
        return OptimizedCallTarget.runtime().getTvmci().getCompilerOptionValues(root);
    }

    @SuppressWarnings("unchecked")
    static <T> T getValue(OptionValues polyglotValues, OptionKey<T> key) {
        if (polyglotValues != null && polyglotValues.hasBeenSet(key)) {
            return polyglotValues.get(key);
        } else {
            OptionKey<?> truffleKey = POLYGLOT_TO_TRUFFLE.get(key);
            if (truffleKey != null) {
                return (T) TruffleRuntimeOptions.getValue(truffleKey);
            }
        }
        return key.getDefaultValue();
    }

    static <T> T getValue(RootNode rootNode, OptionKey<T> key) {
        OptionValues polyglotValues = OptimizedCallTarget.runtime().getTvmci().getCompilerOptionValues(rootNode);
        return getValue(polyglotValues, key);
    }

    static OptionDescriptors getDescriptors() {
        return new PolyglotCompilerOptionsOptionDescriptors();
    }

}
