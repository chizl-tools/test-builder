package codes.chizl.testbuilder.core

import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import java.util.*

class TestOutBuilder {

    fun TestSetup.testSetupFunction(key: String) {
        key store { 55 }
    }

    @Test
    fun `builder attempt 1`() =
        test {
            val SETUP_OBJECT = "SETUP_OBJECT"
            val OUTSIDE = "OUTSIDE"
            setup {
                testSetupFunction(OUTSIDE)
                SETUP_OBJECT store { UUID.randomUUID() }
                act("Creating More Setup") { println("More Setup") }
            }
            actions {
                val storedUUID = setup.testSetupState[SETUP_OBJECT]
                act("Verify UUID stored from Setup is of class UUID") {
                    assertInstanceOf(UUID::class.java, storedUUID)
                }
                act("Get Another thing from Store") {
                    println(setup.testSetupState[OUTSIDE])
                    println("Action4")
                }
            }
            cleanup {
                act("Clean up the Setup") { setup.testSetupState.remove(SETUP_OBJECT) }
            }
        }

    @Test
    fun `builder attempt 2`() =
        test {
            setup {
                "SETUP" store { UUID.randomUUID() }
                act { println("More Setup") }
            }
            actions {
                act {
                    println("Random UUID from Setup")
                    println(setup.testSetupState["SETUP"])
                    val storedUUID = setup.testSetupState["SETUP"]
                    assertInstanceOf(UUID::class.java, storedUUID)
                }
                act { println("Action4") }
            }
            cleanup {
                setup.testSetupState.remove("SETUP").also { println("Cleaning up SETUP") }
            }
        }
}

fun test(init: BaseTestBuilder.() -> Unit) {
    val builder = BaseTestBuilder()
    builder.init()
    builder.build()
}

abstract class TestBuilderAbstract() {
    open lateinit var setup: TestSetup
    open lateinit var actions: TestActions
    open lateinit var cleanup: TestCleanUp

    abstract fun build(): TestBuilderAbstract

    fun setup(init: TestSetup.() -> Unit) {
        setup = TestSetup()
        setup.init()
    }

    fun actions(init: TestActions.() -> Unit) {
        actions = TestActions()
        actions.init()
    }

    fun cleanup(init: TestCleanUp.() -> Unit) {
        cleanup = TestCleanUp()
        cleanup.init()
    }

    companion object {
        val TEST_LOG = mutableMapOf<TestElement, String>()
    }
}

class BaseTestBuilder() : TestBuilderAbstract() {
    override lateinit var setup: TestSetup
    override lateinit var actions: TestActions
    override lateinit var cleanup: TestCleanUp
    lateinit var assertion: TestAssertion

    private constructor(setup: TestSetup, actions: TestActions, cleanup: TestCleanUp) : this() {
        this.setup = setup
        this.actions = actions
        this.cleanup = cleanup
    }

    override fun build(): BaseTestBuilder = BaseTestBuilder(setup, actions, cleanup)
}

interface TestElement

abstract class TestSection : TestElement {
    val actionStore = mutableListOf<() -> Unit>()
    fun act(description: String = "", act: () -> Unit) {
        println("${actionStore.count() + 1} :: ${this.javaClass.simpleName} :: $description")
        actionStore.add(act)
        act.invoke()
    }
}

class TestCleanUp : TestSection()

class TestAssertion

class TestSetup : TestSection() {
    val testSetupState = mutableMapOf<String, Any>()

    infix fun String.store(init: () -> Any) {
        testSetupState[this] = init()
    }
}

class TestActions : TestSection()

class TestAction(val action: () -> Unit) : TestElement

class TestClients : TestSection() {
    val clients = mutableSetOf<TestClient>()

    fun client(init: () -> TestClient) {
        clients.add(init())
    }
}

abstract class TestClient(val name: String)

class APIClient() : TestClient("API")
