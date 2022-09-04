package forex.services.rates

import java.lang.System.Logger
import java.lang.System.Logger.Level

object errors {

  sealed trait Error
  object Error {
    final case class ErrorInProvisionOfService(msg: String) extends Error
    final case class ErrorInUsageOfService(msg: String) extends Error
  }

  private final val log: Logger = System.getLogger(this.getClass.getName)

  // TODO work around interpreters did not like returning their home made Error
  def mkErrorInProvisionOfService(msg: String):Error = { val e = Error.ErrorInProvisionOfService(msg); log.log(Level.ERROR,e); e}
  def mkErrorInProvisionOfService(throwable: Throwable):Error = { val e = Error.ErrorInProvisionOfService(throwable.getClass.getSimpleName+" "+throwable.getMessage); log.log(Level.ERROR,e); e}
  def mkErrorInUsageOfService(msg:String):Error = { val e = Error.ErrorInUsageOfService(msg); log.log(Level.ERROR,e); e}
  def mkErrorInUsageOfService(throwable: Throwable):Error = { val e = Error.ErrorInUsageOfService(throwable.getClass.getSimpleName+" "+throwable.getMessage); log.log(Level.ERROR,e); e}

}
