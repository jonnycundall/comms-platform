class DoNotCommit {


  import com.ovoenergy.comms.model.{TriggeredV2, Metadata, TemplateData, Channel}
  import com.ovoenergy.comms.triggered.Template


  object MeterReadsComm {
    val commManifest: _root_.com.ovoenergy.comms.model.CommManifest = _root_.com.ovoenergy.comms.model.CommManifest(_root_.com.ovoenergy.comms.model.CommType.Service, "example-for-docs", "0.1")

    object Electricity {

      object Meter {}

      case class Meter(firstNumber: _root_.java.lang.String, secondNumber: _root_.java.lang.String) extends _root_.com.ovoenergy.comms.triggered.TemplateDataObj {
        def convertToTemplateData: _root_.scala.collection.immutable.Map[_root_.java.lang.String, _root_.com.ovoenergy.comms.model.TemplateData] = _root_.scala.Seq(convertField(this.firstNumber).map("firstNumber" -> _), convertField(this.secondNumber).map("secondNumber" -> _)).flatten.toMap
      }

    }

    object Gas {

      object Meter {}

      case class Meter(meterNumber: _root_.java.lang.String) extends _root_.com.ovoenergy.comms.triggered.TemplateDataObj {
        def convertToTemplateData: _root_.scala.collection.immutable.Map[_root_.java.lang.String, _root_.com.ovoenergy.comms.model.TemplateData] = _root_.scala.Seq(convertField(this.meterNumber).map("meterNumber" -> _)).flatten.toMap
      }

    }

    case class Electricity(meters: _root_.scala.Seq[Electricity.Meter]) extends _root_.com.ovoenergy.comms.triggered.TemplateDataObj {
      def convertToTemplateData: _root_.scala.collection.immutable.Map[_root_.java.lang.String, _root_.com.ovoenergy.comms.model.TemplateData] = _root_.scala.Seq(convertField(this.meters).map("meters" -> _)).flatten.toMap
    }

    case class Gas(meters: _root_.scala.Seq[Gas.Meter]) extends _root_.com.ovoenergy.comms.triggered.TemplateDataObj {
      def convertToTemplateData: _root_.scala.collection.immutable.Map[_root_.java.lang.String, _root_.com.ovoenergy.comms.model.TemplateData] = _root_.scala.Seq(convertField(this.meters).map("meters" -> _)).flatten.toMap
    }

  }

  case class MeterReadsComm(electricity: _root_.scala.Option[MeterReadsComm.Electricity], gas: _root_.scala.Option[MeterReadsComm.Gas]) extends _root_.com.ovoenergy.comms.triggered.TemplateDataObj {
    def convertToTemplateData: _root_.scala.collection.immutable.Map[_root_.java.lang.String, _root_.com.ovoenergy.comms.model.TemplateData] = _root_.scala.Seq(convertField(this.electricity).map("electricity" -> _), convertField(this.gas).map("gas" -> _)).flatten.toMap
  }

  val data = MeterReadsComm(
    gas = None, // not a gas customer
    electricity = Some(MeterReadsComm.Electricity(
      meters = List(
        // customer has 2 electricity meters
        MeterReadsComm.Electricity.Meter(firstNumber = "123", secondNumber = "456"),
        MeterReadsComm.Electricity.Meter(firstNumber = "789", secondNumber = "123")
      )
    ))
  )

  val metadata = Metadata(
    createdAt = java.time.OffsetDateTime.now().toString,
    eventId = java.util.UUID.randomUUID().toString,
    customerId = "my-customer",
    traceToken = java.util.UUID.randomUUID().toString,
    commManifest = MeterReadsComm.commManifest, // use the generated comm manifest in the companion object
    friendlyDescription = "awesome comm",
    source = "my amazing service",
    triggerSource = "my amazing service",
    canary = true,
    sourceMetadata = None
  )


  val templateData: Map[String, TemplateData] = data.convertToTemplateData

  val preferredChannels = Some(List(Channel.SMS, Channel.Email))

  val triggered = TriggeredV2(
    metadata = metadata,
    templateData = templateData,
    deliverAt = Some(java.time.OffsetDateTime.now().plusSeconds(180).toString),
    expireAt = None,
    preferredChannels = preferredChannels
  )


}
