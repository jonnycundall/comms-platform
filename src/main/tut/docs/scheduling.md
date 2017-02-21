---
layout: docs
title: "Scheduling"
---

# Scheduling

Our platform supports the scheduling of comms. This enables you to optionally specify a date and time when you want a comm to be delivered and an expiry date for when a comm will no longer be relevant when sending a TriggeredV2 event (see the [events](docs/events.html) page for more info on triggered events).  
 
# How do I schedule a comm?

When generating a TriggeredV2 event ([as explained in the events page](docs/events.html)), simply include the date and time you want the comm to be delivered in the ISO-8601 format.
 
```tut:silent
import com.ovoenergy.comms.model.{TriggeredV2, Metadata, TemplateData}
import com.ovoenergy.comms.triggered.Template

@Template("service", "example-for-docs", "0.1")
object MeterReadsComm

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

val triggered = TriggeredV2(
  metadata = metadata,
  templateData = templateData, 
  deliverAt = Some(java.time.OffsetDateTime.now().plusSeconds(180).toString),
  expireAt = None
)
```

Then send the TriggeredV2 event as described in the [events page](docs/events.html) ([avro schema](), [scala case class]()) to `comms.triggered.v2` 

# Why should I specify when my comm expires?

If your comm is time sensitive (for example, will no longer be relevant after a certain date/time), we advise you also include an expireAt value in the event. This way if something goes wrong and the comm has not been sent within the time period you specify, we won't send it at all.
   
```tut:silent
import com.ovoenergy.comms.model.{TriggeredV2, Metadata}

val triggered = TriggeredV2(
  metadata = metadata,
  templateData = templateData, 
  deliverAt = Some(java.time.OffsetDateTime.now().plusSeconds(180).toString),
  expireAt = Some(java.time.OffsetDateTime.now().plusSeconds(360).toString)
)
```

# How do I cancel scheduled comms?

To cancel a scheduled comm, you need to send a Cancelled event ([avro schema](https://github.com/ovotech/comms-kafka-messages/blob/master/schemas/1.4/CancellationRequested.avsc), [scala case class](https://github.com/ovotech/comms-kafka-messages/blob/master/src/main/scala/com/ovoenergy/comms/model/CancellationRequested.scala)) to `comms.cancellation.requested`. We currently support cancelling scheduled comms by a combination of customer id and the comm name:

```tut:silent
import com.ovoenergy.comms.model.{CancellationRequested, GenericMetadata}

val genericMetadata = GenericMetadata(
  createdAt = java.time.OffsetDateTime.now().toString,
  eventId = java.util.UUID.randomUUID().toString,
  traceToken = java.util.UUID.randomUUID().toString,
  source = "my amazing service",
  canary = true
)

val cancellationRequested = CancellationRequested(
  metadata = genericMetadata,
  commName = MeterReadsComm.commManifest.name, // use the generated comm manifest in the companion object
  customerId = "my-customer"
)
```

# How can I get feedback on my scheduled comms?

*Firstly you can see if a comm has been scheduled and what time for in our Audit log. Within the comm details breakdown page we now have a "Scheduled for" field ([PRD](https://audit-log.ovo-comms.co.uk), [UAT](https://audit-log-uat.ovo-comms.co.uk))
  
*Additionally there are a number of events the comms platform produces which provide feedback on scheduled comms:

### Orchestration Started

Generated once the orchestration of a comm has begun, so one of these will be generated once we start to orchestrate a scheduled comm.

[Avro Schema](https://github.com/ovotech/comms-kafka-messages/blob/master/schemas/1.4/OrchestrationStarted.avsc) | [Scala Case Class](https://github.com/ovotech/comms-kafka-messages/blob/master/src/main/scala/com/ovoenergy/comms/model/OrchestrationStarted.scala) | Kafka topic : comms.orchestration.started |

### Cancelled
Generated to confirm the successful cancellation of a scheduled comm. Please note: for each CancellationRequested event, there may be multiple comms cancelled which will result in multiple Cancelled events being generated.

[Avro Schema](https://github.com/ovotech/comms-kafka-messages/blob/master/schemas/1.4/Cancelled.avsc) | [Scala Case Class](https://github.com/ovotech/comms-kafka-messages/blob/master/src/main/scala/com/ovoenergy/comms/model/Cancelled.scala) | Kafka topic : comms.cancelled | 

### Failed Cancellation 
Generated if a Cancellation Request fails and we were unable to cancel the requested comm(s). Just as with Cancelled, there may be multiple comms which failed to cancel so there could be multiple failed Cancellation events as a result of a CancellationRequested event

[Avro Schema](https://github.com/ovotech/comms-kafka-messages/blob/master/schemas/1.4/FailedCancellation.avsc) | [Scala Case Class](https://github.com/ovotech/comms-kafka-messages/blob/master/src/main/scala/com/ovoenergy/comms/model/FailedCancellation.scala) | Kafka topic: comms.failed.cancellation |
 
 
 