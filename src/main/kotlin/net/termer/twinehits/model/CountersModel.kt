package net.termer.twinehits.model

import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.templates.SqlTemplate
import net.termer.twinehits.db.Database.client
import net.termer.twinehits.db.dataobject.Counter
import net.termer.twinehits.db.dataobject.CounterInfo
import net.termer.twinehits.util.toCommaSeparatedRGB
import java.awt.Color

/**
 * Database model for counters
 * @author termer
 * @since 2.0.0
 */
class CountersModel {
	/**
	 * Creates a new CounterModel
	 * @since 2.0.0
	 */
	constructor()

	/**
	 * SELECT statement for getting info
	 * @param extra Extra rows to select (can be null for none)
	 * @return The SELECT statement
	 * @since 2.0.0
	 */
	private fun infoSelect(extra: String?): String {
		return """
			SELECT
				counters.id AS internal_id,
				counter_id AS id,
				counter_name AS name,
				counter_text_color AS text_color,
				counter_bg_color AS bg_color,
				counter_created_on AS created_on,
				CAST((
					SELECT COUNT(hit_ip)
					FROM hits
					WHERE hit_counter = counters.id
				) AS BIGINT) as hits,
				CAST((
					SELECT COUNT(DISTINCT hit_ip)
					FROM hits
					WHERE hit_counter = counters.id
				) AS BIGINT) as unique
			FROM counters
		""".trimIndent()
	}
	/**
	 * SELECT statement for getting info
	 * @return The SELECT statement
	 * @since 2.0.0
	 */
	private fun infoSelect() = infoSelect(null)

	/**
	 * Creates a new counter
	 * @param name The name of the counter
	 * @param id The generated ID of this counter
	 * @param textColor The text color of the image for this counter
	 * @param bgColor The background color of the image for this counter
	 * @param password The password used to edit or delete this counter
	 * @since 2.0.0
	 */
	suspend fun createCounter(name: String, id: String, textColor: Color, bgColor: Color, password: String) {
		SqlTemplate
				.forUpdate(client, """
					INSERT INTO counters ( counter_name, counter_id, counter_text_color, counter_bg_color, counter_password )
					VALUES
					( #{name}, #{id}, #{textColor}, #{bgColor}, #{password} )
				""".trimIndent())
				.execute(hashMapOf<String, Any>(
						"name" to name,
						"id" to id,
						"textColor" to textColor.toCommaSeparatedRGB(),
						"bgColor" to bgColor.toCommaSeparatedRGB(),
						"password" to password
				)).await()
	}

	/**
	 * Fetches a counter based on its alphanumeric ID
	 * @param id The counter's generated ID
	 * @return A row containing the counter, or no rows if the counter doesn't exist
	 * @since 2.0.0
	 */
	suspend fun fetchCounter(id: String): RowSet<Counter> {
		return SqlTemplate
				.forQuery(client, """
					SELECT * FROM counters
					WHERE counter_id = #{id}
				""".trimIndent())
				.mapTo(Counter.MAPPER)
				.execute(hashMapOf<String, Any>(
						"id" to id
				)).await()
	}

	/**
	 * Fetches a counter's info
	 * @param id The counter's alphanumeric ID
	 * @return A row containing the counter's info, or no rows if the counter does not exist
	 * @since 2.0.0
	 */
	suspend fun fetchCounterInfo(id: String): RowSet<CounterInfo> {
		return SqlTemplate
				.forQuery(client, """
					${infoSelect()}
					WHERE counter_id = #{id}
				""".trimIndent())
				.mapTo(CounterInfo.MAPPER)
				.execute(hashMapOf<String, Any>(
						"id" to id
				)).await()
	}

	/**
	 * Updates a counter's info
	 * @param id The alphanumeric ID of the counter to edit
	 * @param name The counter's new name
	 * @param textColor The counter's new text color
	 * @param bgColor The counter's new background color
	 * @since 2.0.0
	 */
	suspend fun updateCounter(id: String, name: String, textColor: Color, bgColor: Color) {
		SqlTemplate
				.forUpdate(client, """
					UPDATE counters
					SET
						counter_name = #{name},
						counter_text_color = #{textColor},
						counter_bg_color = #{bgColor}
					WHERE counter_id = #{id}
				""".trimIndent())
				.execute(hashMapOf<String, Any>(
						"id" to id,
						"name" to name,
						"textColor" to textColor.toCommaSeparatedRGB(),
						"bgColor" to bgColor.toCommaSeparatedRGB()
				)).await()
	}

	/**
	 * Deletes a counter
	 * @param id The counter's alphanumeric ID
	 * @since 2.0.0
	 */
	suspend fun deleteCounter(id: String) {
		SqlTemplate
				.forUpdate(client, """
					DELETE FROM counters WHERE counter_id = #{id}
				""".trimIndent())
				.execute(hashMapOf<String, Any>(
						"id" to id
				)).await()
	}
}