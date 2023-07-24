/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.library.abi;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.library.abi.GenerateLibraryAbiReaderTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("compiler/util-klib-abi/testData/content")
@TestDataPath("$PROJECT_ROOT")
public class ClassicJsLibraryAbiReaderTestGenerated extends AbstractClassicJsLibraryAbiReaderTest {
    @Test
    public void testAllFilesPresentInContent() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("compiler/util-klib-abi/testData/content"), Pattern.compile("^(.+)\\.kt$"), null, TargetBackend.JS_IR, true);
    }

    @Test
    @TestMetadata("callables.kt")
    public void testCallables() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/callables.kt");
    }

    @Test
    @TestMetadata("classifiers.kt")
    public void testClassifiers() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/classifiers.kt");
    }

    @Test
    @TestMetadata("excluded_classes_1.kt")
    public void testExcluded_classes_1() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/excluded_classes_1.kt");
    }

    @Test
    @TestMetadata("excluded_classes_2.kt")
    public void testExcluded_classes_2() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/excluded_classes_2.kt");
    }

    @Test
    @TestMetadata("excluded_classes_3.kt")
    public void testExcluded_classes_3() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/excluded_classes_3.kt");
    }

    @Test
    @TestMetadata("excluded_packages_non_root_1.kt")
    public void testExcluded_packages_non_root_1() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/excluded_packages_non_root_1.kt");
    }

    @Test
    @TestMetadata("excluded_packages_non_root_2.kt")
    public void testExcluded_packages_non_root_2() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/excluded_packages_non_root_2.kt");
    }

    @Test
    @TestMetadata("excluded_packages_non_root_3.kt")
    public void testExcluded_packages_non_root_3() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/excluded_packages_non_root_3.kt");
    }

    @Test
    @TestMetadata("excluded_packages_non_root_4.kt")
    public void testExcluded_packages_non_root_4() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/excluded_packages_non_root_4.kt");
    }

    @Test
    @TestMetadata("excluded_packages_root_1.kt")
    public void testExcluded_packages_root_1() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/excluded_packages_root_1.kt");
    }

    @Test
    @TestMetadata("excluded_packages_root_2.kt")
    public void testExcluded_packages_root_2() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/excluded_packages_root_2.kt");
    }

    @Test
    @TestMetadata("inheritance.kt")
    public void testInheritance() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/inheritance.kt");
    }

    @Test
    @TestMetadata("root_package.kt")
    public void testRoot_package() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/root_package.kt");
    }

    @Test
    @TestMetadata("type_parameters.kt")
    public void testType_parameters() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/type_parameters.kt");
    }

    @Test
    @TestMetadata("value_parameters.kt")
    public void testValue_parameters() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/value_parameters.kt");
    }

    @Test
    @TestMetadata("visibilities.kt")
    public void testVisibilities() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/visibilities.kt");
    }

    @Test
    @TestMetadata("with_non_public_markers_1.kt")
    public void testWith_non_public_markers_1() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/with_non_public_markers_1.kt");
    }

    @Test
    @TestMetadata("with_non_public_markers_2.kt")
    public void testWith_non_public_markers_2() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/with_non_public_markers_2.kt");
    }

    @Test
    @TestMetadata("with_non_public_markers_3.kt")
    public void testWith_non_public_markers_3() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/with_non_public_markers_3.kt");
    }

    @Test
    @TestMetadata("with_non_public_markers_4.kt")
    public void testWith_non_public_markers_4() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/with_non_public_markers_4.kt");
    }

    @Test
    @TestMetadata("with_non_public_markers_5.kt")
    public void testWith_non_public_markers_5() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/with_non_public_markers_5.kt");
    }

    @Test
    @TestMetadata("with_non_public_markers_private_annotations.kt")
    public void testWith_non_public_markers_private_annotations() throws Exception {
        runTest("compiler/util-klib-abi/testData/content/with_non_public_markers_private_annotations.kt");
    }
}
