package net.termer.twinehits.model

import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.templates.SqlTemplate
import net.termer.twinehits.db.Database.client

/**
 * Database model for hits
 * @author termer
 * @since 2.0.0
 */
class HitsModel {
	/**
	 * Creates a new HitsModel
	 * @since 2.0.0
	 */
	constructor()

	/**
	 * Creates a new hit entry
	 * @param counter The counter this hit is being created for
	 * @param ip The hash of the IP who created this hit
	 * @since 2.0.0
	 */
	suspend fun createHit(counter: Int, ip: String) {
		SqlTemplate
				.forUpdate(client, """
					INSERT INTO hits
					( hit_ip, hit_counter )
					VALUES
					( #{ip}, #{counter} )
				""".trimIndent())
				.execute(hashMapOf<String, Any>(
						"counter" to counter,
						"ip" to ip
				)).await()
	}

	/**
	 * Deletes all hits on the specified counter ID
	 * @param counter The counter's internal ID
	 * @since 2.0.0
	 */
	suspend fun deleteHitsByCounter(counter: Int) {
		SqlTemplate
				.forUpdate(client, """
					DELETE FROM hits WHERE hit_counter = #{counter}
				""".trimIndent())
				.execute(hashMapOf<String, Any>(
						"counter" to counter
				)).await()
	}
}