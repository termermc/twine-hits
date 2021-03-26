package net.termer.twinehits.util

import java.awt.Color

/**
 * Returns a comma-separated RGB string of this color
 * @return A comma-separated RGB string of this color
 * @since 2.0.0
 */
fun Color.toCommaSeparatedRGB() = "$red,$green,$blue"

/**
 * Returns a color from a comma-separated RGB string
 * @param str The string to convert
 * @return A color from a comma-separated RGB string
 * @since 2.0.0
 */
fun colorFromString(str: String): Color {
	val parts = str.split(',')

	// Check amount of values
	if(parts.size < 3)
		throw IllegalArgumentException("String does not contain all 3 RGB values")

	// Parse values
	val red = parts[0].toInt().coerceIn(0, 255)
	val green = parts[1].toInt().coerceIn(0, 255)
	val blue = parts[2].toInt().coerceIn(0, 255)

	// Return color
	return Color(red, green, blue)
}