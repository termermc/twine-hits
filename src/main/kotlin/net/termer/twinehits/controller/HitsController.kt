package net.termer.twinehits.controller

import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.termer.twine.ServerManager.router
import net.termer.twine.ServerManager.vertx
import net.termer.twinehits.Module.Companion.logger
import net.termer.twinehits.model.CountersModel
import net.termer.twinehits.model.HitsModel
import net.termer.twinehits.util.appHostnames
import net.termer.twinehits.util.hashString
import net.termer.twinehits.util.ip
import net.termer.twinehits.util.renderCounter
import java.awt.Color

/**
 * Controller for rendering page hit images and incrementing amounts
 * @since 2.0.0
 */
fun hitsController() {
	val countersModel = CountersModel()
	val hitsModel = HitsModel()

	for(hostname in appHostnames()) {
		router().get("/hit/:id").virtualHost(hostname).handler { r ->
			GlobalScope.launch(vertx().dispatcher()) {
				val id = r.pathParam("id")

				// Determine format
				val format = when(r.request().getParam("format")?.toLowerCase()) {
					"jpg", "jpeg" -> "JPEG"
					"gif" -> "GIF"
					"bmp" -> "BMP"
					else -> "PNG"
				}

				// Set format
				r.response().putHeader("content-type", "image/$format")

				try {
					// Fetch counter
					val counterRes = countersModel.fetchCounterInfo(id)

					if(counterRes.rowCount() > 0) {
						val counter = counterRes.first()

						// Collect values
						val hits = counter.hits
						val unique = counter.unique

						try {
							// Render image
							r.response().end(renderCounter(format, "Hits: $hits\nUnique: $unique", counter.textColor, counter.bgColor))

							try {
								// Create hit entry
								hitsModel.createHit(counter.internalId, hashString(r.ip()))
							} catch(e: Exception) {
								logger.error("Failed to create hit entry for counter ID $id:")
								e.printStackTrace()
							}
						} catch(e: Exception) {
							logger.error("Failed to render image for counter ID $id:")
							e.printStackTrace()
							r.response().end()
						}
					} else {
						// Send failure image
						r.response().end(renderCounter(format, "Invalid ID", Color(255, 255, 255), Color(0, 0, 0)))
					}
				} catch(e: Exception) {
					logger.error("Failed to fetch counter ID $id:")
					e.printStackTrace()
					r.response().end(renderCounter(format, "Database error", Color(255, 255, 255), Color(0, 0, 0)))
				}
			}
		}
	}
}