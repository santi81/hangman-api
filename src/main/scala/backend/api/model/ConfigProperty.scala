package backend.api.model

object ConfigProperty {

  case class ServerConfig(name: String,
                          host: String,
                          port: Int,
                          maxConcurrentRequests: Int,
                          maxWaiters: Int)
}
