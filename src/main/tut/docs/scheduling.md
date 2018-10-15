---
layout: docs
title: "Scheduling"
---

# Scheduling

Our platform supports the scheduling of comms. This enables you to optionally specify a date and time when you want a comm to be delivered and an expiry date for when a comm will no longer be relevant when sending a TriggeredV3 event (see the [events](events.md) page for more info on triggered events).
 
# How do I schedule a comm?

When generating a TriggeredV3 event ([as explained in the events page](events.md)), simply include the date and time you want the comm to be delivered. The timestamp is represented as a `java.time.Instant` in the Scala model, and as timestamp-millis in Avro.
 
```tut:silent
import com.ovoenergy.comms.model._
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

val metadata = MetadataV2(
	createdAt = java.time.Instant.now(),
	eventId = java.util.UUID.randomUUID().toString,
	deliverTo = Customer("my-customer"),
	traceToken = java.util.UUID.randomUUID().toString,
	commManifest = MeterReadsComm.commManifest, // use the generated comm manifest in the companion object
	friendlyDescription = "awesome comm",
	source = "my amazing service",
	triggerSource = "my amazing service",
	canary = true,
	sourceMetadata = None
)

    
val templateData: Map[String, TemplateData] = data.convertToTemplateData

val triggered = TriggeredV3(
  metadata = metadata,
  templateData = templateData, 
  deliverAt = Some(java.time.Instant.now().plusSeconds(180)),
  expireAt = None,
  preferredChannels = None
)
```

Then send the TriggeredV3 event as described in the [events page](events.html) to `comms.triggered.v3` 

# Why should I specify when my comm expires?

If your comm is time sensitive (for example, will no longer be relevant after a certain date/time), we advise you also include an `expireAt` value in the event. This way if something goes wrong and the comm has not been sent within the time period you specify, we won't send it at all.

```tut:silent
import com.ovoenergy.comms.model.{TriggeredV3, MetadataV2}

val triggered = TriggeredV3(
  metadata = metadata,
  templateData = templateData, 
  deliverAt = Some(java.time.Instant.now().plusSeconds(180)),
  expireAt = Some(java.time.Instant.now().plusSeconds(360)),
  preferredChannels = None
)
```

# How do I cancel scheduled comms?

To cancel a scheduled comm, you need to send a [CancellationRequestedV2](https://github.com/ovotech/comms-kafka-messages/blob/master/modules/core/src/main/scala/com/ovoenergy/comms/model/CancellationRequestedV2.scala) event to `comms.cancellation.requested.v2`. We currently support cancelling scheduled comms by a combination of customer ID and the comm name:

```tut:silent
import com.ovoenergy.comms.model.{CancellationRequestedV2, GenericMetadataV2}

val genericMetadata = GenericMetadataV2(
  createdAt = java.time.Instant.now(),
  eventId = java.util.UUID.randomUUID().toString,
  traceToken = java.util.UUID.randomUUID().toString,
  source = "my amazing service",
  canary = true
)

val cancellationRequested = CancellationRequestedV2(
  metadata = genericMetadata,
  commName = MeterReadsComm.commManifest.name, // use the generated comm manifest in the companion object
  customerId = "my-customer"
)
```

Note: we do not currently support cancellation of non-customer comms. We can implement this if there is a need for it.

# How can I get feedback on my scheduled comms?

* Firstly you can see if a comm has been scheduled and what time for in our Audit log. Within the comm details breakdown page we now have a "Scheduled for" field ([PRD](https://audit-log.ovo-comms.co.uk), [UAT](https://audit-log.ovo-comms-uat.co.uk))

* Additionally there are a number of events the comms platform produces which provide feedback on scheduled comms:

### Orchestration Started

Generated once the orchestration of a comm has begun, so one of these will be generated once we start to orchestrate a scheduled comm.

[Scala Case Class](https://github.com/ovotech/comms-kafka-messages/blob/master/src/main/scala/com/ovoenergy/comms/model/OrchestrationStartedV2.scala) | Kafka topic : comms.orchestration.started.v2 |

### Cancelled

Generated to confirm the successful cancellation of a scheduled comm. Please note: for each CancellationRequested event, there may be multiple comms cancelled which will result in multiple Cancelled events being generated.

[Scala Case Class](https://github.com/ovotech/comms-kafka-messages/blob/master/src/main/scala/com/ovoenergy/comms/model/CancelledV2.scala) | Kafka topic : comms.cancelled.v2 | 

### Failed Cancellation 

Generated if a Cancellation Request fails and we were unable to cancel the requested comm(s). Just as with Cancelled, there may be multiple comms which failed to cancel so there could be multiple failed Cancellation events as a result of a CancellationRequested event

[Scala Case Class](https://github.com/ovotech/comms-kafka-messages/blob/master/src/main/scala/com/ovoenergy/comms/model/FailedCancellationV2.scala) | Kafka topic: comms.failed.cancellation.v2 |
 
 
