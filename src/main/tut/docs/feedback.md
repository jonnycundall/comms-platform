---
layout: docs
title: "Feedback"
---

# Feedback

The Comms Platform offers the following topics for feedback on outcomes of issued communications.

<br>

## Email
Produced when the Comms Platform had successfully sent the communication to its third party email delivery service (Mailgun) and recevied a feedback from it.

#### Topics to consume

**1. Email Progressed** [(view on GitHub)](https://github.com/ovotech/comms-kafka-messages/blob/master/modules/core/src/main/scala/com/ovoenergy/comms/model/email/EmailProgressedV2.scala)

topic name: ```comms.progressed.email.v2```

event structure: 

```
EmailProgressedV2(
    metadata: MetadataV2,
    internalMetadata: InternalMetadata,
    status: EmailStatus,
    gateway: Gateway,
    gatewayMessageId: Option[String] = None,
    reason: Option[String] = None
)
```


##### status values:
- **Queued:** your comm has been accepted by our third party message provider and the message has been placed in queue.
- **Dropped:** your comm was not delivered to the recipient email server.
- **Bounced:** your comm has been rejected by the recipient SMTP server.
- **Delivered:** your comm was sent and it was accepted by the recipient email server.
- **Opened:** the email recipient opened the email and enabled image viewing.

<br>

**2. Link Clicked** [(view on GitHub)](https://github.com/ovotech/comms-kafka-messages/blob/master/modules/core/src/main/scala/com/ovoenergy/comms/model/LinkClickedV2.scala)

topic name: ```comms.link.clicked.v2```

event structure:
```
LinkClickedV2(
    metadata: MetadataV2,
    internalMetadata: InternalMetadata,
    gateway: Gateway,
    url: String
)
```

<br><br>
## SMS
Produced when the Comms Platform had successfully sent the communication to its third party sms delivery service (Twilio) and received a feedback from it.

#### Topics to consume

**1. SMS Progressed** [(view on GitHub)](https://github.com/ovotech/comms-kafka-messages/blob/master/modules/core/src/main/scala/com/ovoenergy/comms/model/sms/SMSProgressedV2.scala)

topic name: ```comms.progressed.sms.v2```

event structure: 

```
SMSProgressedV2(
    metadata: MetadataV2,
    internalMetadata: InternalMetadata,
    status: SMSStatus,
    gateway: Gateway,
    gatewayMessageId: String,
    reason: Option[String] = None
)
```

##### status values: 
- **Accepted:** your comm has been accepted by our third party party message provider.
- **Queued:** your comms is queued for delivery.
- **Sent:** your comms was successfully accepted by the nearest upstream carrier.
- **Delivered:** we received confirmation of message delivery from the upstream carrier.
- **Undelivered:** we received a delivery receipt indicating that the message was not delivered. This can happen for a number of reasons including carrier content filtering, availability of the destination handset, etc.
- **Failed:** The message could not be sent. This can happen for various reasons including queue overflows, account suspension. Charges for failed messages do not apply.

<br><br>
## Print
Produced when the Comms Platform had successfully sent the communication to its third party printer service (Stannp).

#### Topics to consume

**1. Issued For Delivery** [(view on GitHub)](https://github.com/ovotech/comms-kafka-messages/blob/master/modules/core/src/main/scala/com/ovoenergy/comms/model/IssuedForDeliveryV2.scala)

topic name: ```comms.issued.for.delivery.v2```

event structure: 

```
IssuedForDeliveryV2(
    metadata: MetadataV2,
    internalMetadata: InternalMetadata,
    channel: Channel,
    gateway: Gateway,
    gatewayMessageId: String
) 
```
<br><br>
## Internally failed comms

Produced when the Comms Platform cannot process a triggered communication. This can be caused by a wide variety of problems, the details of which are specified by the human readable reason and errorCode.

#### Topics to consume

**1. Failed** [(view on GitHub)](https://github.com/ovotech/comms-kafka-messages/blob/master/modules/core/src/main/scala/com/ovoenergy/comms/model/FailedV2.scala)

topic name: ```comms.failed.v2``` 

event structure: 
   
```
FailedV2(
    metadata: MetadataV2,
    internalMetadata: InternalMetadata,
    reason: String,
    errorCode: ErrorCode
)
```
<br><br>
## Cancelled comms

Produced when a scheduled comm is cancelled before being delivered.

#### Topics to consume

**1. Cancelled** [(view on GitHub)](https://github.com/ovotech/comms-kafka-messages/blob/master/modules/core/src/main/scala/com/ovoenergy/comms/model/CancelledV2.scala)

topic name: ```comms.cancelled.v2```

event structure:

```
CancelledV2(
    metadata: MetadataV2,
    cancellationRequested: CancellationRequestedV2
)
```
<br>

**2. Cancellation Failed** [(view on GitHub)](https://github.com/ovotech/comms-kafka-messages/blob/master/modules/core/src/main/scala/com/ovoenergy/comms/model/FailedCancellationV2.scala)

topic name: ```comms.failed.cancellation.v2```

event structure:

```
FailedCancellationV2(
    metadata: GenericMetadataV2,
    cancellationRequested: CancellationRequestedV2,
    reason: String
)
```