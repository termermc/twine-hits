package net.termer.twinehits.util

import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import net.termer.twine.utils.RequestUtils
import net.termer.vertx.kotlin.validation.RequestValidator

/**
 * Sends an error API response with the provided message
 * @param msg The error message
 * @since 2.0.0
 */
suspend fun RoutingContext.error(msg: String) {
	json(io.vertx.kotlin.core.json.json {
		obj("status" to "error", "error" to msg)
	}).await()
}
/**
 * Sends an error API response with the provided message and details
 * @param msg The error message
 * @param details The error details
 * @since 2.0.0
 */
suspend fun RoutingContext.error(msg: String, details: JsonObject) {
	json(io.vertx.kotlin.core.json.json {
		obj(
				"status" to "error",
				"error" to msg,
				"details" to details
		)
	}).await()
}
/**
 * Sends an error API response with the provided message and error type and text details
 * @param msg The error message
 * @param type The error type
 * @param text The error plaintext message
 * @since 2.0.0
 */
suspend fun RoutingContext.error(msg: String, type: String, text: String) {
	json(io.vertx.kotlin.core.json.json {
		obj(
				"status" to "error",
				"error" to msg,
				"details" to io.vertx.kotlin.core.json.json {
					obj(
							"error_type" to type,
							"error_text" to text
					)
				}
		)
	}).await()
}

/**
 * Returns an error based on a failed request validation
 * @param validator The RequestValidator that failed validation
 * @since 2.0.0
 */
suspend fun RoutingContext.error(validator: RequestValidator) {
	json(io.vertx.kotlin.core.json.json {
		obj(
				"status" to "error",
				"error" to validator.validationErrorText,
				"details" to io.vertx.kotlin.core.json.json {
					obj(
							"error_type" to validator.validationErrorType,
							"error_text" to validator.validationErrorText,
							"param_name" to validator.validationErrorParam
					)
				}
		)
	}).await()
}

/**
 * Sends a success API response
 * @since 2.0.0
 */
suspend fun RoutingContext.success() {
	json(io.vertx.kotlin.core.json.json {
		obj("status" to "success")
	}).await()
}

/**
 * Sends a success API response with the provided JSON data
 * @param json The JSON to send along with the success status
 * @since 2.0.0
 */
suspend fun RoutingContext.success(json: JsonObject) {
	json(json.put("status", "success")).await()
}

/**
 * Sends an authorized status and JSON message
 * @since 2.0.0
 */
suspend fun RoutingContext.unauthorized() {
	response().statusCode = 401

	if(get("invalidJWT") as Boolean? == true)
		error("Unauthorized", "INVALID_TOKEN", "The provided token is invalid")
	else
		error("Unauthorized")
}

/**
 * Returns the IP address that this request connected from (respects X-Forwarded-For header if Twine is under a reverse proxy)
 * @return The IP address that this request connected from
 * @since 2.0.0
 */
fun RoutingContext.ip(): String = RequestUtils.resolveIp(request())

/**
 * Allows a header via Access-Control-Allow-Headers
 * @param header The header to allow
 * @return This, to be used fluently
 * @since 2.0.0
 */
fun HttpServerResponse.corsAllowHeader(header: String): HttpServerResponse {
	val allowed = arrayListOf<String>()
	if(headers().contains("Access-Control-Allow-Headers")) {
		val strs = headers()["Access-Control-Allow-Headers"].split(',')

		// Add existing allowed headers
		for(str in strs) {
			val procStr = str.toLowerCase()
			if(!allowed.contains(procStr))
				allowed.add(procStr)
		}
	}

	// Add new allowed header
	if(!allowed.contains(header.toLowerCase()))
		allowed.add(header.toLowerCase())

	// Set Access-Control-Allow-Headers
	putHeader("Access-Control-Allow-Headers", allowed.joinToString(", "))

	return this
}