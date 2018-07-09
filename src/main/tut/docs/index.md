---
layout: docs
title: "Getting Started"
---

# Getting Started

In order to start sending comms using the platform, you will need two things:

1. A template. See the [templates](templates.html) page for information on how to write and publish templates.
2. Some code to send an HTTP request to our REST API in order to trigger delivery of your comm. See the [REST API](rest-api.html) page for information about what the request contains. You can check out the [comms-example-trigger](https://github.com/ovotech/comms-example-trigger) project to see how to send a request.

## How the comms platform works

When we receive your trigger request, we do roughly the following:

1. **Orchestration.** This includes looking up a customer profile, scheduling the comm to be delivered later (and cancelling these scheduled comms at a later date if you decide!), and choosing the most suitable channel on which to deliver the comm (email, SMS, ...).
2. **Composition.** We combine your comm template with the user-specific data you provide in your Kafka event to generate the content of your comm.
3. **Delivery.** We send the comm to the API of the appropriate delivery gateway.
4. **Tracking.** We listen to events from the delivery gateway in order to track the comm's delivery progress.
