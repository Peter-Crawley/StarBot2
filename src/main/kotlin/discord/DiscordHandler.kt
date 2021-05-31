package discord

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.apache.logging.log4j.LogManager
import org.reflections.Reflections
import org.reflections.ReflectionsException
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Method

class DiscordHandler : ListenerAdapter() {
	private val logger = LogManager.getLogger("StarBot2 (Discord)")
	private val registeredCommands: ArrayList<Method> = arrayListOf()

	init {
		// TODO: Once a newer version of reflections is released, stop using a configuration builder. This is just a hacky work around because of an error where the Store class is never created properly.
		val reflections = Reflections(ConfigurationBuilder()
			.setUrls(ClasspathHelper.forPackage("discord.commands"))
			.setScanners(MethodAnnotationsScanner())
		)

		val commands: Set<Method>

		try {
			commands = reflections.getMethodsAnnotatedWith(Command::class.java)

			for (command in commands) {
				if (!command.getAnnotation(Command::class.java).subCommand) {
					logger.info("Registered command: " + command.name)

					registeredCommands.add(command)
				}
			}

		} catch (e: ReflectionsException) {
			logger.warn("This is probably nothing to worry about: " + e.message)
		}

		logger.info("Registered " + registeredCommands.size + " command(s).")
	}

	override fun onMessageReceived(event: MessageReceivedEvent) {
		val message: String = event.message.contentDisplay

		// If it is a command, then handle it.
		if (message.startsWith(".")) handleCommand(message.substring(1), null, event)
	}

	private fun handleCommand(command: String, parentCommand: Method?, event: MessageReceivedEvent) {
		// This is where the real fun begins.
		val possibleCommands: ArrayList<Method> = when (parentCommand == null) {
			true -> registeredCommands // Not a sub-command, get the list of commands
			false -> parentCommand.declaringClass.methods.toList() as ArrayList<Method> // Is a sub-command, the potential sub-commands will be in the same class as the parent
		}

		val targetCommand = command.split(" ")[0]

		for (possibleCommand in possibleCommands) {
			val annotation = possibleCommand.getAnnotation(Command::class.java)

			if (possibleCommand.name == targetCommand) { // Does it look like the command we are looking for?
				if (annotation.subCommands.isEmpty()) { // Are there no more sub-commands
					possibleCommand.invoke(possibleCommand.declaringClass.getDeclaredConstructor().newInstance(), event)

				} else { // There are sub-commands
					handleCommand(command.substring(targetCommand.length).replaceFirst(" ", ""), possibleCommand, event)

				}

				return
			}
		}

		// We did not call any sub-commands, just run that command
		if (parentCommand != null) {
			parentCommand.invoke(parentCommand.declaringClass.getDeclaredConstructor().newInstance(), event)

			logger.info("User " + event.member?.id + " (" + event.message.author.name + ") ran the command " + event.message.contentDisplay)
		}

	}
}