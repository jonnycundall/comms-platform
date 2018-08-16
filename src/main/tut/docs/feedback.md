---
layout: docs
title: "Feedback"
---

# Feedback

The Comms Platform offers a unified topic for feedback on outcomes of issued communications, across all channels.

**Feedback** [(view on GitHub)](https://github.com/ovotech/comms-kafka-messages/blob/master/modules/core/src/main/scala/com/ovoenergy/comms/model/Feedback.scala)

### Topic name
```comms.feedback```

### Event Structure:
```scala
case class Feedback(
    commId: String,
    customer: Option[Customer],
    status: FeedbackStatus,
    description: Option[String],
    email: Option[EmailFeedback],
    channel: Option[Channel],
    metadata: EventMetadata
)
```

#### Status values
- **Scheduled** : Produced when your communication has been scheduled for future delivery.
- **Pending** : Produced when your communication  has been accepted by the relevant third party party message provider.
- **Delivered** : Produced when we have confirmation of message delivery from the upstream carrier, usually a third party message provider.
- **Failed** : Produced when the Comms Platform cannot process a triggered communication. This can be caused by a wide variety of problems, the details of which are specified by the human readable `description`.
- **Cancelled** : Produced when a scheduled communication is cancelled before being delivered.
- **FailedCancellation** : Produced when the Comms Platform is unable to cancel a communication.
- **Expired** : Produced when a scheduled message could not be sent before its expiry date.

#### Email feedback

The `email` field _may_ be be defined when `channel` is `Email`, but it _cannot_ be defined otherwise. It contains additional feedback you can get for emails, namely:
- **Opened** : the recipient has opened the email
- **Interacted** : the recipient has clicked on a link in the email.


### Note

The feedback topic contains both messages indicating _progress_ or _feedback_, and messages indicating _outcome_.
In particular a `Status` of `Failed`, `Delivered` or `Cancelled` indicates _outcome_, every other message indicates _progress_ or _feedback_.
The `EmailStatus` messages in particular represent _feedback_ in that you can receive them after the the communication has been `Delivered`, but they don't affect the state of the platform.
