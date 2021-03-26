package net.termer.twinehits

import io.vertx.core.json.Json
import net.termer.twine.Events
import net.termer.twine.modules.TwineModule
import net.termer.twine.utils.files.BlockingReader
import net.termer.twine.utils.files.BlockingWriter
import net.termer.twinehits.controller.apiNotFoundController
import net.termer.twinehits.controller.counterController
import net.termer.twinehits.controller.hitsController
import net.termer.twinehits.db.*
import net.termer.twinehits.middleware.headersMiddleware
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

/**
 * Module main class
 * @author termer
 * @since 2.0.0
 */
class Module: TwineModule {
	companion object {
		/**
		 * Returns the module's config contents
		 * @since 2.0.0
		 */
		var config: HitsConfig = HitsConfig()

		/**
		 * Module logger class
		 * @since 2.0.0
		 */
		val logger = LoggerFactory.getLogger(Module::class.java)
	}

	// Module metadata methods
	override fun name() = "twine-hits"
	override fun priority() = TwineModule.Priority.MEDIUM
	override fun twineVersion() = "2.0+"

	override fun preinitialize() {}
	override fun initialize() {
		try {
			// Configure the server
			configure()

			// Register server config reload hook
			Events.on(Events.Type.CONFIG_RELOAD) {
				try {
					configure()
				} catch(e: IOException) {
					logger.error("Failed to load twine-hits config")
					e.printStackTrace()
				}
			}

			// Setup database
			logger.info("Setting up database...")
			dbInit()
			// Run migration if db_auto_migrate is true
			if(config.db_auto_migrate) {
				logger.info("Running database migrations...")
				dbMigrate()
			}

			// Setup middlewares
			headersMiddleware()

			// Setup controllers
			logger.info("Setting up controllers...")
			counterController()
			hitsController()
			apiNotFoundController()

			// Started
			logger.info("Started!")
		} catch(e: IOException) {
			logger.error("Failed to setup module:")
			e.printStackTrace()
		}
	}

	override fun shutdown() {
		// Disconnect database
		logger.info("Closing database connection...")
		dbClose()
	}

	// Configures the module
	private fun configure() {
		logger.info("Setting up config...")
		val conf = File("configs/hits.json")
		if(conf.exists()) {
			config = Json.decodeValue(BlockingReader.read(conf), HitsConfig::class.java)
		} else {
			BlockingWriter.write("configs/hits.json", Json.encodePrettily(config))
		}
	}
}