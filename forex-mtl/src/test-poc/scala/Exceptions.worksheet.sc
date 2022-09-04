
import java.lang
// https://stackoverflow.com/questions/10925268/define-your-own-exceptions-with-overloaded-constructors-in-scala
import java.lang.Exception

trait ProvisionOfService
trait UsageOfService

object provisionOfService
{
  case class ErrorInProvisionOfService(message: String = "", cause: Option[Throwable] = None) extends Exception(message)  with ProvisionOfService { cause.foreach(initCause) }
  case class ErrorWithConnection(message: String = "", cause: Option[Throwable] = None) extends Exception(message) with ProvisionOfService { cause.foreach(initCause) }
  case class ErrorRateStale(message: String = "", cause: Option[Throwable] = None) extends Exception(message) with ProvisionOfService { cause.foreach(initCause) }
  case class TooManyRequests(message: String = "", cause: Option[Throwable] = None) extends Exception(message) with ProvisionOfService  { cause.foreach(initCause) }
}

object usageOfService
{
  case class ErrorInUsageOfService(message: String = "", cause: Option[Throwable] = None) extends Exception(message) with UsageOfService { cause.foreach(initCause) }
  case class ErrorWithCurrencyGiven(message: String = "", cause: Option[Throwable] = None) extends Exception(message) with UsageOfService  { cause.foreach(initCause) }
}

try {
  throw provisionOfService.TooManyRequests()
  new provisionOfService.ErrorWithConnection("oh dear")
}
catch
{

  case e: provisionOfService.TooManyRequests => {
     var pp: ProvisionOfService = e
//      var uu: UsageOfService = e
    println("exception "+e.getClass.getSimpleName +" t" )
//    errors.mkErrorInProvisionOfService()
  }
  case ee: java.lang.Throwable => println(ee)
}