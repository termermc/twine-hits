package net.termer.twinehits.db.dataobject

import io.vertx.codegen.annotations.DataObject
import io.vertx.sqlclient.templates.RowMapper
import net.termer.twinehits.util.colorFromString
import java.awt.Color
import java.time.OffsetDateTime

/**
 * Data class for a counter
 * @param id The counter's internal ID
 * @param counterId The counter's alphanumeric ID
 * @param name The counter's name
 * @param password The counter's generated password
 * @param textColor The counter's text color
 * @param bgColor The counter's background color
 * @param createdOn The counter's creation time
 * @author termer
 * @since 2.0.0
 */
@DataObject
class Counter(
		/**
		 * The counter's internal ID
		 * @since 2.0.0
		 */
		val id: Int,
		/**
		 * The counter's alphanumeric ID
		 * @since 2.0.0
		 */
		val counterId: String,
		/**
		 * The counter's name
		 * @since 2.0.0
		 */
		val name: String,
		/**
		 * The counter's generated password
		 * @since 2.0.0
		 */
		val password: String,
		/**
		 * The counter's text color
		 * @since 2.0.0
		 */
		val textColor: Color,
		/**
		 * The counter's background color
		 * @since 2.0.0
		 */
		val bgColor: Color,
		/**
		 * The counter's creation time
		 * @since 2.0.0
		 */
		val createdOn: OffsetDateTime
) {
	companion object {
		/**
		 * The row mapper for this type of row
		 * @since 1.4.0
		 */
		val MAPPER = RowMapper<Counter> { row ->
			Counter(
					id = row.getInteger("id"),
					counterId = row.getString("counter_id"),
					name = row.getString("counter_name"),
					password = row.getString("counter_password"),
					textColor = colorFromString(row.getString("counter_text_color")),
					bgColor = colorFromString(row.getString("counter_bg_color")),
					createdOn = row.getOffsetDateTime("counter_created_on")
			)
		}
	}
}