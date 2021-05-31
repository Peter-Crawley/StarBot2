package discord

@Retention(AnnotationRetention.RUNTIME) // Apparently we need this otherwise the annotation is not accessible in code at runtime.
annotation class Command(val subCommand: Boolean = false, val subCommands: Array<String> = [])