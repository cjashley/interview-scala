package forex.services.rates

object errors {

  sealed trait Error
  object Error {
    final case class ErrorInProvisionOfService(msg: String) extends Error
    final case class ErrorInUsageOfService(msg: String) extends Error
  }

  // TODO work around interpreters did not like returning their home made Error
  def mkErrorInProvisionOfService(msg: String):Error = Error.ErrorInProvisionOfService(msg)
  def mkErrorInUsageOfService(msg:String):Error = Error.ErrorInUsageOfService(msg)

}
