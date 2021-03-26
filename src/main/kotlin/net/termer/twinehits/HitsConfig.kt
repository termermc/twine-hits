package net.termer.twinehits

/**
 * Configuration class for twine-hits
 * @author termer
 * @since 2.0.0
 */
class HitsConfig {
    var db_address = "localhost"
    var db_port = 3333
    var db_name = "hits"
    var db_user = "me"
    var db_pass = "drowssap"
    var db_max_pool_size = 5
    var db_auto_migrate = true

    var domain = "default"
    var frontend_host = "*"

    var ip_hash_algorithm = "SHA-256"
    var ip_hash_salt = "123ChangeMe"
}