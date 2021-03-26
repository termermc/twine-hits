package net.termer.twinehits.db.dataobject

import io.vertx.codegen.annotations.DataObject
import io.vertx.sqlclient.templates.RowMapper
import net.termer.twinehits.util.colorFromString
import java.awt.Color
import java.time.OffsetDateTime

/**
 * Data class for a counter's info
 * @param internalId The counter's internal ID
 * @param id The counter's alphanumeric ID
 * @param name The counter's name
 * @param textColor The counter's text color
 * @param bgColor The counter's background color
 * @param createdOn The counter's creation time
 * @param hits The total amount of hits this counter has had
 * @param unique The total amount of unique hits this counter has had
 * @author termer
 * @since 2.0.0
 */
@DataObject
class CounterInfo(
		/**
		 * The counter's internal ID
		 * @since 2.0.0
		 */
		val internalId: Int,
		/**
		 * The counter's alphanumeric ID
		 * @since 2.0.0
		 */
		val id: String,
		/**
		 * The counter's name
		 * @since 2.0.0
		 */
		val name: String,
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
		val createdOn: OffsetDateTime,
		/**
		 * The total amount of hits this counter has had
		 * @since 2.0.0
		 */
		val hits: Long,
		/**
		 * The total amount of unique hits this counter has had
		 * @since 2.0.0
		 */
		val unique: Long
) {
	companion object {
		/**
		 * The row mapper for this type of row
		 * @since 1.4.0
		 */
		val MAPPER = RowMapper<CounterInfo> { row ->
			CounterInfo(
					internalId = row.getInteger("internal_id"),
					id = row.getString("id"),
					name = row.getString("name"),
					textColor = colorFromString(row.getString("text_color")),
					bgColor = colorFromString(row.getString("bg_color")),
					createdOn = row.getOffsetDateTime("created_on"),
					hits = row.getLong("hits"),
					unique = row.getLong("unique")
			)
		}
	}
}