package net.termer.twinehits.controller

import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.termer.twine.ServerManager.router
import net.termer.twine.ServerManager.vertx
import net.termer.twine.utils.StringFilter
import net.termer.twinehits.Module.Companion.logger
import net.termer.twinehits.model.CountersModel
import net.termer.twinehits.model.HitsModel
import net.termer.twinehits.util.appHostnames
import net.termer.twinehits.util.error
import net.termer.twinehits.util.success
import net.termer.twinehits.validator.ColorValidator
import net.termer.vertx.kotlin.validation.RequestValidator
import net.termer.vertx.kotlin.validation.validator.StringValidator
import java.awt.Color

/**
 * Controller handling all registration and deletion of counters
 * @since 2.0.0
 */
fun counterController() {
	for(hostname in appHostnames()) {
		// Counter creation route
		router().post("/api/v1/counter/create").virtualHost(hostname).handler { r ->
			GlobalScope.launch(vertx().dispatcher()) {
				val countersModel = CountersModel()

				// Request validation
				val v = RequestValidator()
						.param("name", StringValidator()
								.trim()
								.notBlank()
								.maxLength(32))
						.param("text_color", ColorValidator())
						.param("bg_color", ColorValidator())

				if(v.validate(r)) {
					val name = v.parsedParam("name") as String
					val textColor = v.parsedParam("text_color") as Color
					val bgColor = v.parsedParam("bg_color") as Color

					// Generate ID and password
					val id = StringFilter.generateString(16)
					val passwd = StringFilter.generateString(10)

					try {
						// Create entry
						countersModel.createCounter(name, id, textColor, bgColor, passwd)

						// Success
						r.success(json {
							obj(
									"id" to id,
									"password" to passwd
							)
						})
					} catch(e: Exception) {
						logger.error("Failed to create counter:")
						e.printStackTrace()
						r.error("Database error")
					}
				} else {
					r.error(v)
				}
			}
		}

		// Counter edit route
		router().post("/api/v1/counter/:id/edit").virtualHost(hostname).handler { r ->
			GlobalScope.launch(vertx().dispatcher()) {
				val countersModel = CountersModel()

				// Request validation
				val v = RequestValidator()
						.param("name", StringValidator()
								.trim()
								.notBlank()
								.maxLength(32))
						.param("text_color", ColorValidator())
						.param("bg_color", ColorValidator())
						.param("password", StringValidator()
								.maxLength(10))
						.routeParam("id", StringValidator().maxLength(16))

				if(v.validate(r)) {
					val name = v.parsedParam("name") as String
					val textColor = v.parsedParam("text_color") as Color
					val bgColor = v.parsedParam("bg_color") as Color
					val password = v.parsedParam("password")
					val id = v.parsedRouteParam("id") as String

					try {
						val counterRes = countersModel.fetchCounter(id)

						if(counterRes.rowCount() > 0) {
							val counter = counterRes.first()

							val realPass = counter.password

							// Check password
							if(password == realPass) {
								try {
									// Update counter
									countersModel.updateCounter(id, name, textColor, bgColor)

									// Send success
									r.success()
								} catch(e: Exception) {
									logger.error("Failed to update counter ID $id:")
									e.printStackTrace()
									r.error("Database error")
								}
							} else {
								r.error("Password does not match")
							}
						} else {
							r.error("Invalid ID")
						}
					} catch(e: Exception) {
						logger.error("Failed to fetch counter:")
						e.printStackTrace()
						r.error("Database error")
					}
				} else {
					r.error(v)
				}
			}
		}

		// Counter delete route
		router().post("/api/v1/counter/:id/delete").virtualHost(hostname).handler { r ->
			GlobalScope.launch(vertx().dispatcher()) {
				val countersModel = CountersModel()
				val hitsModel = HitsModel()

				// Request validation
				val v = RequestValidator()
						.param("password", StringValidator()
								.maxLength(10))
						.routeParam("id", StringValidator()
								.maxLength(16))

				if(v.validate(r)) {
					val password = v.parsedParam("password") as String
					val id = v.parsedRouteParam("id") as String

					try {
						// Check counter to make sure it exists
						val counterRes = countersModel.fetchCounter(id)

						if(counterRes.rowCount() > 0) {
							val counter = counterRes.first()
							val realPass = counter.password

							// Check password
							if(password == realPass) {
								try {
									// Delete counter
									countersModel.deleteCounter(id)

									try {
										// Delete counter's hits
										hitsModel.deleteHitsByCounter(counter.id)

										// Send success
										r.success()
									} catch(e: Exception) {
										logger.error("Failed to delete hits for counter ID $id:")
										e.printStackTrace()
										r.error("Database error")
									}
								} catch(e: Exception) {
									logger.error("Failed to delete counter ID $id:")
									e.printStackTrace()
									r.error("Database error")
								}
							} else {
								r.error("Password does not match")
							}
						}
					} catch(e: Exception) {
						logger.error("Failed to fetch counter ID $id:")
						e.printStackTrace()
						r.error("Database error")
					}
				} else {
					r.error(v)
				}
			}
		}
	}
}