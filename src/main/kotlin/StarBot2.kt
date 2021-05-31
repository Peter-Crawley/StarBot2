import discord.DiscordHandler
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.apache.logging.log4j.LogManager
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*
import kotlin.system.exitProcess

private val logger = LogManager.getLogger("StarBot2")

fun main() {
	// I like having fancy log files.
	logger.info("Starting StarBot2")

	logger.info("Loading Authentication Data")

	val discordToken: String
	val minecraftEmail: String
	val minecraftPassword: String

	// There is no need to implement this... at all, because this software is design for 1 person to run.
	try {
		val authenticationData = Properties()

		authenticationData.load(FileInputStream("authentication.sb2d"))

		discordToken = authenticationData.getProperty("discordToken")
		minecraftEmail = authenticationData.getProperty("minecraftEmail")
		minecraftPassword = authenticationData.getProperty("minecraftPassword")

	} catch (e: FileNotFoundException) {
		logger.fatal("Authentication Data does not exist.")

		val newAuthenticationData = Properties()

		newAuthenticationData.setProperty("discordToken", "")
		newAuthenticationData.setProperty("minecraftEmail", "")
		newAuthenticationData.setProperty("minecraftPassword", "")

		newAuthenticationData.store(FileOutputStream("authentication.sb2d"), "StarBot2 Authentication Data")

		logger.info("A new blank Authentication Data file has been created.")

		exitProcess(0)

	}

	logger.info("Authentication Data loaded")
	logger.info("Connecting to Discord")

	// Temporarily commented out to get IntelliJ to shut up
	val discord: JDA = JDABuilder.createDefault(discordToken).build()
	discord.addEventListener(DiscordHandler())

	logger.info("Connected to Discord")
}