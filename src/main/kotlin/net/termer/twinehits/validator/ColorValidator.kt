package net.termer.twinehits.validator

import net.termer.vertx.kotlin.validation.ParamValidator
import java.awt.Color

/**
 * Validator for comma-separated RGB colors
 * @since 2.0.0
 */
class ColorValidator: ParamValidator {
	override fun validate(param: ParamValidator.Param): ParamValidator.ValidatorResponse {
		val parts = param.value.split(',')

		// Check amount of values
		if(parts.size < 3)
			return ParamValidator.ValidatorResponse("INVALID_COLOR", "String does not contain all 3 RGB values")

		return try {
			// Parse values
			val red = parts[0].toInt().coerceIn(0, 255)
			val green = parts[1].toInt().coerceIn(0, 255)
			val blue = parts[2].toInt().coerceIn(0, 255)

			// Return color
			ParamValidator.ValidatorResponse(Color(red, green, blue))
		} catch(e: NumberFormatException) {
			ParamValidator.ValidatorResponse("INVALID_COLOR", "RGB colors must be numbers")
		}
	}
}