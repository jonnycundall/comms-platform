---
layout: docs
title: "Channels"
---

<div class="alert alert-info">
This documentation has been updated in preperation for release of SMS support, this is expected to be available from 12/4/17.
</div>

# Channels

Comms can be issued over the following channels:

* Email
* SMS

Each time a comm is [triggered](events.html) the channel the comm is sent over is determined by the following factors:

## Channels in the Template

When a comm [template](templates.html) is published, the channels the comm is available over are specified. 

A template obviously must have at least one channel, which can be either SMS or email. 

## Customer Communication Preferences

Within My OVO customers set which channels they wish to receive comms over, they are able to do this for Service and Marketing comms separately.
 
For Service comms the customer is forced to choose either Post or Email, with SMS available as an additional option.

Unless specified by the customer in their communication preferences, comms will not be issued via said channel.

<div class="alert alert-info">
If a customer has no Service Communication Preferences this indicates that they have not actually set any, as either email or post must be selected, and therefore the customer's Comminication Preferences are not taked into account.
</div>

  
## Customer Profile
  
The customer-service provides us with contact details for a customer. 

In order to send an email or SMS, an email address or mobile number are required respectively, if these are not present then the comm can not be issued over that channel.


## Preferred Channels

When a comm is [triggered](events.html) the `preferredChannels` field is an ordered list of channels indicating the preferred channels to send the comm over.

This is a list of preferred channels and the comm may be issued over a channel not specified in this list.

### Example Triggered Event

The following example is triggering a MeterReads comm, specifying that we would like to send the comm over SMS if possible, followed by email and then any other channel.

```tut:silent
import com.ovoenergy.comms.model.{Metadata, TriggeredV2, Channel, TemplateData}
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

val preferredChannels = Some(List(Channel.SMS, Channel.Email))
val templateData: Map[String, TemplateData] = data.convertToTemplateData

val event = TriggeredV2(metadata, templateData, None, None, preferredChannels)
```

## Cost

All things being equal the comm will be issued using the cheapest channel.

## The decision flow

The following diagram shows how the above are used to determine the channel to send the comm over.

![Channel selection logic flow](../img/comms-channels-logic.png)