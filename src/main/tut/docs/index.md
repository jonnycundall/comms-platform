---
layout: docs
title: "Getting Started"
---

# Getting Started

In order to start sending comms to customers, you will need two things:

1. A template. See the [templates](docs/templates.html) page for information on how to write and publish templates.
2. Some code to send an event to a Kafka topic in order to trigger delivery of your comm to a customer. See the [events](docs/events.html) page for information about what the event contains and how to build and send one.

## How the comms platform works

When we receive your Kafka event, we do roughly the following:

1. **Orchestration.** This includes looking up the customer's profile, scheduling the comm to be delivered later (and cancelling these scheduled comms at a later date if you decide!), and choosing the most suitable channel on which to deliver the comm (email, SMS, ...).
2. **Composition.** We combine your comm template with the customer-specific data you provide in your Kafka event to generate the content of your comm.
3. **Delivery.** We send the comm to the API of the appropriate delivery gateway.
4. **Tracking.** We listen to events from the delivery gateway in order to track the comm's delivery progress.
