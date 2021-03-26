package net.termer.twinehits.util

import io.vertx.core.Promise
import io.vertx.core.buffer.Buffer
import io.vertx.kotlin.coroutines.await
import net.termer.twine.ServerManager.vertx
import net.termer.twine.Twine
import net.termer.twinehits.Module
import net.termer.twinehits.Module.Companion.config
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.MessageDigest
import javax.imageio.ImageIO

/**
 * Returns the hostnames this application should bind its routes to
 * @return The hostnames this application should bind its routes to
 * @since 2.0.0
 */
fun appHostnames(): Array<String> = if(config.domain != "*")
	Twine.domains().byNameOrDefault(config.domain).hostnames()
else
	arrayOf("*")

/**
 * Hashes a string with the configured algorithm and returns the result
 * @param str The string to hash
 * @return The hashed string
 * @since 2.0.0
 */
suspend fun hashString(str: String): String {
	return vertx().executeBlocking<String> {
		// Hash IP
		val bytes: ByteArray = (str+config.ip_hash_salt).toByteArray(Charsets.UTF_8)
		val md = MessageDigest.getInstance(config.ip_hash_algorithm)
		val hashed = md.digest(bytes)

		it.complete(String(hashed, Charsets.UTF_8))
	}.await()
}

/**
 * Renders a hit counter image with the specified text, and returns it as a ByteArrayOutputStream
 * @param format The format to create this image as (PNG, JPEG, GIF, BMP)
 * @param content The counter's text content
 * @param txtColor The color of the text to render
 * @param bgColor The color of the image background
 * @return A Buffer containing the image data
 * @since 2.0.0
 */
suspend fun renderCounter(format: String, content: String, txtColor: Color, bgColor: Color): Buffer {
	return Buffer.buffer(vertx().executeBlocking { promise: Promise<ByteArrayOutputStream> ->
		// Init image
		val img = BufferedImage(185, 50, BufferedImage.TYPE_INT_RGB)
		val gfx = img.createGraphics()

		// Draw graphics
		gfx.background = bgColor
		gfx.clearRect(0, 0, 185, 50)
		gfx.font = Font("Monospace", Font.PLAIN, 20)
		gfx.paint = txtColor

		var y = -4
		for(line in content.split("\n").toTypedArray())
			gfx.drawString(line, 3, gfx.fontMetrics.height.let { y += it; y })

		val baos = ByteArrayOutputStream()
		try {
			// Write image
			ImageIO.write(img, format, baos)
			promise.complete(baos)
		} catch(e: IOException) {
			Module.logger.error("Failed to render hit image:")
			e.printStackTrace()
			promise.fail(e)
		}
	}.await().toByteArray())
}