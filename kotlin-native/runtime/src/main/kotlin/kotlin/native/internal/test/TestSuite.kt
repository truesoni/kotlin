/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

package kotlin.native.internal.test

import kotlin.experimental.ExperimentalNativeApi
import kotlin.IllegalArgumentException

/**
 * Represents a test case, that is a Kotlin test method marked with `@Test` annotaion.
 *
 * @see kotlin.test.Test
 */
@ExperimentalNativeApi
public interface TestCase {
    public val name: String

    /**
     * Shows if this test is ignored by the `@Ignore` annotation.
     *
     * @see kotlin.test.Ignore
     */
    public val ignored: Boolean

    /**
     * Test Suite the test belongs to.
     */
    public val suite: TestSuite

    /**
     * Runs all methods that were marked with `@BeforeTest` annotation.
     *
     * @see kotlin.test.BeforeTest
     */
    public fun doBefore()

    /**
     * Runs the test method itself
     *
     * @see kotlin.test.Test
     */
    public fun doRun()

    /**
     * Runs test with its before and after functions.
     */
    public fun run() {
        try {
            doBefore()
            doRun()
        } finally {
            doAfter()
        }
    }

    /**
     * Runs all methods that were marked with `@AfterTest` annotation.
     *
     * @see kotlin.test.AfterTest
     */
    public fun doAfter()
}

@ExperimentalNativeApi
internal val TestCase.prettyName get() = "${suite.name}.$name"

/**
 * Represents Test suite, a suite that contains test cases.
 *
 * Test suite can be either test class containing test cases
 * or a top level suite for top level methods.
 */
@ExperimentalNativeApi
public interface TestSuite {
    /**
     * Test suite name.
     */
    public val name: String

    /**
     * Shows if the suite is ignored with `@Ignore` annotation.
     *
     * @see kotlin.test.Ignore
     */
    public val ignored: Boolean

    /**
     * Test cases this test suite contains
     */
    public val testCases: Map<String, TestCase>

    /**
     * Number of test cases
     */
    public val size : Int

    /**
     * Executes all methods marked with `@BeforeClass` annotation
     *
     * @see kotlin.test.BeforeClass
     */
    public fun doBeforeClass()

    /**
     * Executes all methods marked with `@AfterClass` annotation
     *
     * @see @see kotlin.test.AfterClass
     */
    public fun doAfterClass()
}

@ExperimentalNativeApi
public enum class TestFunctionKind {
    BEFORE_TEST,
    AFTER_TEST,
    BEFORE_CLASS,
    AFTER_CLASS
}

@ExperimentalNativeApi
public abstract class AbstractTestSuite<F: Function<Unit>>(override val name: String, override val ignored: Boolean)
    : TestSuite {
    override fun toString(): String = name

    // TODO: Make inner and remove the type param when the bug is fixed.
    public abstract class BasicTestCase<F: Function<Unit>>(
            override val name: String,
            override val suite: AbstractTestSuite<F>,
            public val testFunction: F,
            override val ignored: Boolean
    ) : TestCase {
        override fun toString(): String = "$name ($suite)"
    }

    private val _testCases = mutableMapOf<String, BasicTestCase<F>>()
    override val testCases: Map<String, BasicTestCase<F>>
        get() = _testCases

    private fun registerTestCase(testCase: BasicTestCase<F>) = _testCases.put(testCase.name, testCase)

    public fun registerTestCase(name: String, testFunction: F, ignored: Boolean): BasicTestCase<F>? =
            registerTestCase(createTestCase(name, testFunction, ignored))

    public abstract fun createTestCase(name: String, testFunction: F, ignored: Boolean):  BasicTestCase<F>

    init {
        registerSuite(this)
    }

    override val size: Int
        get() = testCases.size
}

@ExperimentalNativeApi
public abstract class BaseClassSuite<INSTANCE, COMPANION>(name: String, ignored: Boolean)
    : AbstractTestSuite<INSTANCE.() -> Unit>(name, ignored) {

    public class TestCase<INSTANCE, COMPANION>(name: String,
                                               override val suite: BaseClassSuite<INSTANCE, COMPANION>,
                                               testFunction: INSTANCE.() -> Unit,
                                               ignored: Boolean)
        : BasicTestCase<INSTANCE.() -> Unit>(name, suite, testFunction, ignored) {

        internal val instance: INSTANCE by lazy { suite.createInstance() }

        override fun doBefore() {
            suite.before.forEach { instance.it() }
        }

        override fun doAfter() {
            suite.after.forEach { instance.it() }
        }

        override fun doRun() {
            instance.testFunction()
        }
    }

    // These two methods are overridden in test suite classes generated by the compiler.
    public abstract fun createInstance(): INSTANCE
    public open fun getCompanion(): COMPANION = throw NotImplementedError("Test class has no companion object")

    public companion object {
        public val INSTANCE_KINDS: List<TestFunctionKind> = listOf(TestFunctionKind.BEFORE_TEST, TestFunctionKind.AFTER_TEST)
        public val COMPANION_KINDS: List<TestFunctionKind> = listOf(TestFunctionKind.BEFORE_CLASS, TestFunctionKind.AFTER_CLASS)
    }

    private val instanceFunctions = mutableMapOf<TestFunctionKind, MutableSet<INSTANCE.() -> Unit>>()
    private fun getInstanceFunctions(kind: TestFunctionKind): MutableCollection<INSTANCE.() -> Unit> {
        check(kind in INSTANCE_KINDS)
        return instanceFunctions.getOrPut(kind) { mutableSetOf() }
    }

    private val companionFunction = mutableMapOf<TestFunctionKind, MutableSet<COMPANION.() -> Unit>>()
    private fun getCompanionFunctions(kind: TestFunctionKind): MutableCollection<COMPANION.() -> Unit> {
        check(kind in COMPANION_KINDS)
        return companionFunction.getOrPut(kind) { mutableSetOf() }
    }

    public val before:      Collection<INSTANCE.() -> Unit>  get() = getInstanceFunctions(TestFunctionKind.BEFORE_TEST)
    public val after:       Collection<INSTANCE.() -> Unit>  get() = getInstanceFunctions(TestFunctionKind.AFTER_TEST)

    public val beforeClass: Collection<COMPANION.() -> Unit>  get() = getCompanionFunctions(TestFunctionKind.BEFORE_CLASS)
    public val afterClass:  Collection<COMPANION.() -> Unit>  get() = getCompanionFunctions(TestFunctionKind.AFTER_CLASS)

    @Suppress("UNCHECKED_CAST")
    public fun registerFunction(kind: TestFunctionKind, function: Function1<*, Unit>): Boolean =
            when (kind) {
                in INSTANCE_KINDS -> getInstanceFunctions(kind).add(function as INSTANCE.() -> Unit)
                in COMPANION_KINDS -> getCompanionFunctions(kind).add(function as COMPANION.() -> Unit)
                else -> throw IllegalArgumentException("Unknown function kind: $kind")
            }

    override fun doBeforeClass(): Unit = beforeClass.forEach { getCompanion().it() }
    override fun doAfterClass(): Unit = afterClass.forEach { getCompanion().it() }

    override fun createTestCase(name: String, testFunction: INSTANCE.() -> Unit, ignored: Boolean)
            : BasicTestCase<INSTANCE.() -> Unit> =
            TestCase<INSTANCE, COMPANION>(name, this, testFunction, ignored)
}

private typealias TopLevelFun = () -> Unit

@ExperimentalNativeApi
public class TopLevelSuite(name: String): AbstractTestSuite<TopLevelFun>(name, false) {

    public class TestCase(name: String, override val suite: TopLevelSuite, testFunction: TopLevelFun, ignored: Boolean)
        : BasicTestCase<TopLevelFun>(name, suite, testFunction, ignored) {
        override fun doBefore() {
            suite.before.forEach { it() }
        }

        override fun doAfter() {
            suite.after.forEach { it() }
        }

        override fun doRun() {
            testFunction()
        }
    }

    private val specialFunctions = mutableMapOf<TestFunctionKind, MutableSet<TopLevelFun>>()
    private fun getFunctions(type: TestFunctionKind) = specialFunctions.getOrPut(type) { mutableSetOf() }

    public val before:      Collection<TopLevelFun>  get() = getFunctions(TestFunctionKind.BEFORE_TEST)
    public val after:       Collection<TopLevelFun>  get() = getFunctions(TestFunctionKind.AFTER_TEST)
    public val beforeClass: Collection<TopLevelFun>  get() = getFunctions(TestFunctionKind.BEFORE_CLASS)
    public val afterClass:  Collection<TopLevelFun>  get() = getFunctions(TestFunctionKind.AFTER_CLASS)

    public fun registerFunction(kind: TestFunctionKind, function: TopLevelFun): Boolean = getFunctions(kind).add(function)

    override fun doBeforeClass(): Unit = beforeClass.forEach { it() }
    override fun doAfterClass(): Unit = afterClass.forEach { it() }

    override fun createTestCase(name: String, testFunction: TopLevelFun, ignored: Boolean)
            : BasicTestCase<TopLevelFun> = TestCase(name, this, testFunction, ignored)
}