package backend.api.interceptors

trait GameError {

  final case class NonExistentGameException(msg: String, cause: Option[Throwable] = None)
    extends Exception(msg, cause.orNull)

}
