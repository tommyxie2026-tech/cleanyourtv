# The test runner's Error Prone annotations describe javac modifiers. Android has no javac model,
# and these compiler annotations are not used by the instrumentation tests at runtime.
-dontwarn javax.lang.model.element.Modifier
