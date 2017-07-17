---
layout: home
title: "Home"
---

# Comms Platform

A platform for sending and tracking communications to OVO customers and other users.

## How to send a comm

So you want to send a comm using our platform? Excellent. There are two steps to the process:

1. [Create and publish a comm template](docs/templates.html)
2. [Send a Kafka event](docs/events.html) to trigger a comm. This event contains a reference to the template, plus the user-specific data you want to inject into it.

We'll take care of the rest.

For more details, please take a look at the [documentation](docs/) or talk to the comms team.

## How to stay informed about your comms

### Audit log

You can check the status of your comms using our [audit log service](https://audit-log.ovo-comms.co.uk/).

### Failed events

If you want to be informed about failures in real-time (e.g. for alerting), you can listen for [Failed](https://github.com/ovotech/comms-kafka-messages/blob/master/src/main/scala/com/ovoenergy/comms/model/FailedV2.scala) events on the `comms.failed.v2` Kafka topic. 

Whenever a failure occurs anywhere in our system that means we can't deliver a comm, we publish an event to this topic. 

The event includes both a machine-readable error code and a human-readable message containing more details.
