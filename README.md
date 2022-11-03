# test-builder

## Purpose
To create a structured test flow that is easily understandable at a glance what is being tested.

Test structure base would follow an "Arrange, Act, Assert" and "Given, Then, When" style

This project will be done in Native Kotlin, so it can run multiplatform: JS, Java, Android, Swift/ios.

Example:
```kotlin
test {
    val SETUP_OBJECT = "SETUP_OBJECT"
    setup {
        SETUP_OBJECT store { UUID.randomUUID() }
        act("Creating More Setup") { println("More Setup") }
    }
    actions {
        val storedUUID = setup.testSetupState[SETUP_OBJECT]
        act("Verify UUID stored from Setup is of class UUID") {
            assertInstanceOf(UUID::class.java, storedUUID)
        }
    }
    cleanup {
        act("Clean up the Setup") { setup.testSetupState.remove(SETUP_OBJECT) }
    }
}
```