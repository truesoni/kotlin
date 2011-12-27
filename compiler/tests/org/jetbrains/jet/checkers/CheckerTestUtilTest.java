package org.jetbrains.jet.checkers;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.JetLiteFixture;
import org.jetbrains.jet.lang.cfg.pseudocode.JetControlFlowDataTraceFactory;
import org.jetbrains.jet.lang.diagnostics.Diagnostic;
import org.jetbrains.jet.lang.psi.JetFile;
import org.jetbrains.jet.lang.resolve.BindingContext;
import org.jetbrains.jet.lang.resolve.java.AnalyzerFacade;

import java.util.Collections;
import java.util.List;

/**
 * @author abreslav
 */
public class CheckerTestUtilTest extends JetLiteFixture {

    public CheckerTestUtilTest() {
        super("diagnostics/checkerTestUtil");
    }

    protected void doTest(TheTest theTest) throws Exception {
        prepareForTest("test");
        theTest.test(myFile);
    }

    public void testEquals() throws Exception {
        doTest(new TheTest() {
            @Override
            protected void makeTestData(List<Diagnostic> diagnostics, List<CheckerTestUtil.DiagnosedRange> diagnosedRanges) {
            }
        });
    }

    public void testMissing() throws Exception {
        doTest(new TheTest("Missing TYPE_MISMATCH at 56 to 57") {
            @Override
            protected void makeTestData(List<Diagnostic> diagnostics, List<CheckerTestUtil.DiagnosedRange> diagnosedRanges) {
                diagnostics.remove(1);
            }
        });
    }

    public void testUnexpected() throws Exception {
        doTest(new TheTest("Unexpected TYPE_MISMATCH at 56 to 57") {
            @Override
            protected void makeTestData(List<Diagnostic> diagnostics, List<CheckerTestUtil.DiagnosedRange> diagnosedRanges) {
                diagnosedRanges.remove(1);
            }
        });
    }

    public void testBoth() throws Exception {
        doTest(new TheTest("Unexpected TYPE_MISMATCH at 56 to 57", "Missing UNRESOLVED_REFERENCE at 164 to 166") {
            @Override
            protected void makeTestData(List<Diagnostic> diagnostics, List<CheckerTestUtil.DiagnosedRange> diagnosedRanges) {
                diagnosedRanges.remove(1);
                diagnostics.remove(diagnostics.size() - 1);
            }
        });
    }

    public void testMissingInTheMiddle() throws Exception {
        doTest(new TheTest("Unexpected NONE_APPLICABLE at 120 to 121", "Missing TYPE_MISMATCH at 159 to 167") {
            @Override
            protected void makeTestData(List<Diagnostic> diagnostics, List<CheckerTestUtil.DiagnosedRange> diagnosedRanges) {
                diagnosedRanges.remove(4);
                diagnostics.remove(diagnostics.size() - 3);
            }
        });
    }

    private static abstract class TheTest {
        private final String[] expected;

        protected TheTest(String... expectedMessages) {
            this.expected = expectedMessages;
        }

        public void test(final @NotNull PsiFile psiFile) {
            BindingContext bindingContext = AnalyzerFacade.analyzeOneFileWithJavaIntegration(
                    (JetFile) psiFile,
                    JetControlFlowDataTraceFactory.EMPTY);

            String expectedText = CheckerTestUtil.addDiagnosticMarkersToText(psiFile, CheckerTestUtil.getDiagnosticsIncludingSyntaxErrors(bindingContext, psiFile)).toString();

            List<CheckerTestUtil.DiagnosedRange> diagnosedRanges = Lists.newArrayList();
            CheckerTestUtil.parseDiagnosedRanges(expectedText, diagnosedRanges);
            for (CheckerTestUtil.DiagnosedRange diagnosedRange : diagnosedRanges) {
                diagnosedRange.setFile(psiFile);
            }

            List<Diagnostic> diagnostics = CheckerTestUtil.getDiagnosticsIncludingSyntaxErrors(bindingContext, psiFile);
            Collections.sort(diagnostics, CheckerTestUtil.DIAGNOSTIC_COMPARATOR);

            makeTestData(diagnostics, diagnosedRanges);

            final List<String> expectedMessages = Lists.newArrayList(expected);
            final List<String> actualMessages = Lists.newArrayList();

            CheckerTestUtil.diagnosticsDiff(diagnosedRanges, diagnostics, new CheckerTestUtil.DiagnosticDiffCallbacks() {
                @NotNull
                @Override
                public PsiFile getFile() {
                    return psiFile;
                }

                @Override
                public void missingDiagnostic(String type, int expectedStart, int expectedEnd) {
                    String message = "Missing " + type + " at " + expectedStart + " to " + expectedEnd;
                    actualMessages.add(message);
                }

                @Override
                public void unexpectedDiagnostic(String type, int actualStart, int actualEnd) {
                    String message = "Unexpected " + type + " at " + actualStart + " to " + actualEnd;
                    actualMessages.add(message);
                }
            });

            assertEquals(listToString(expectedMessages), listToString(actualMessages));
        }

        private String listToString(List<String> expectedMessages) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String expectedMessage : expectedMessages) {
                stringBuilder.append(expectedMessage).append("\n");
            }
            return stringBuilder.toString();
        }

        protected abstract void makeTestData(List<Diagnostic> diagnostics, List<CheckerTestUtil.DiagnosedRange> diagnosedRanges);
    }

}
