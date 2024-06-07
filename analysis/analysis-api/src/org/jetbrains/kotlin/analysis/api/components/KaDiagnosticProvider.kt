/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.components

import org.jetbrains.kotlin.analysis.api.diagnostics.KaDiagnosticWithPsi
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile

public interface KaDiagnosticProvider {
    public fun KtElement.diagnostics(filter: KaDiagnosticCheckerFilter): Collection<KaDiagnosticWithPsi<*>>

    @Deprecated("Use 'diagnostics()' instead.", replaceWith = ReplaceWith("diagnostic(filter)"))
    public fun KtElement.getDiagnostics(filter: KaDiagnosticCheckerFilter): Collection<KaDiagnosticWithPsi<*>> {
        return diagnostics(filter)
    }

    public fun KtFile.collectDiagnostics(filter: KaDiagnosticCheckerFilter): Collection<KaDiagnosticWithPsi<*>>

    @Deprecated("Use 'collectDiagnostics()' instead.", replaceWith = ReplaceWith("collectDiagnostics(filter)"))
    public fun KtFile.collectDiagnosticsForFile(filter: KaDiagnosticCheckerFilter): Collection<KaDiagnosticWithPsi<*>> {
        return collectDiagnostics(filter)
    }
}

public enum class KaDiagnosticCheckerFilter {
    ONLY_COMMON_CHECKERS,
    ONLY_EXTENDED_CHECKERS,
    EXTENDED_AND_COMMON_CHECKERS,
}

public typealias KtDiagnosticCheckerFilter = KaDiagnosticCheckerFilter