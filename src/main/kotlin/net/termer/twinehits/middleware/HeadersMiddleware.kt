package net.termer.twinehits.middleware

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext
import net.termer.twine.ServerManager.router
import net.termer.twinehits.Module.Companion.config
import net.termer.twinehits.util.appHostnames
import net.termer.twinehits.util.corsAllowHeader

/**
 * Middleware for setting response headers
 * @since 2.0.0
 */
fun headersMiddleware() {
	for(hostname in appHostnames()) {
		fun handler(r: RoutingContext) {
			val origin = if(config.frontend_host == "*") {
				if(r.request().headers().contains("Origin")) {
					r.request().getHeader("Origin")
				} else {
					"*"
				}
			} else {
				config.frontend_host
			}

			// Send headers
			r.response()
					.putHeader("Content-Type", "application/json")
					.putHeader("Access-Control-Allow-Origin",  origin)
					.putHeader("Access-Control-Allow-Credentials", "true")
					.putHeader("Cache-Control", "no-store")

			// Handle preflight headers
			if(r.request().method() == HttpMethod.OPTIONS)
				r.response()
						.corsAllowHeader("authorization")
						.corsAllowHeader("content-type")

			// Pass to next handler
			if(!r.response().ended())
				r.next()
		}

		router().route("/api/*").virtualHost(hostname).handler(::handler)
		router().route("/hit/:id").virtualHost(hostname).handler(::handler)
	}
}