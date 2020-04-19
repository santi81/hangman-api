package backend.api.domain

import com.typesafe.config.ConfigFactory
import backend.api.model.ConfigProperty.ServerConfig

trait Config {

  private[this] lazy val loadConf = ConfigFactory.load().getConfig("backend.api.server")

  lazy val serverConf = ServerConfig(loadConf.getString("name"),
    loadConf.getString("host"),
    loadConf.getInt("port"),
    loadConf.getInt("maxConcurrentRequests"),
    loadConf.getInt("maxWaiters"))
}
