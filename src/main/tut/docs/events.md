---
layout: docs
title: "Events"
---

# Events

## TriggeredV2

In order to send a comm to a customer, you need to trigger the comm by sending a `TriggeredV3` event to the `comms.triggered.v3` Kafka topic.

### Event structure

The `TriggeredV3` event has the following fields: 

* `metadataV2` - this includes a unique ID for the comm and a DeliverTo (either the id of the customer you wish to send the given comm to, or the contact details if the comm recipient does not have a Salesforce account). The fields are documented in the [Avro schema](https://github.com/ovotech/comms-kafka-messages/tree/master/schemas).
* `templateData` - this is the customer-specific data you want to use to fill in the placeholders in your comm template. **Note:** If you fail to fill in all the placeholders in the template, your comm will not be sent!
* `deliverTo` - this 

### Building the event

As long as you send us a valid `TriggeredV2` event, you can build it however you like.

However, if you are using Scala, we recommend you use our `comms-triggered-event-builder` library. This will generate Scala classes corresponding to the structure of your comm template, giving you a compile-time guarantee that you have filled in the template correctly.

For example, say you have the following comm template:

{% raw %}
```
Please give us your meter reads.

{{#if gas}}
  We need reads for the following gas meters:

  {{#each gas.meters}}
   - {{this.meterNumber}}  
  {{/each}}
{{/if}}

{{#if electricity}}
  We need reads for the following electricity meters:

  {{#each electricity.meters}}
  - {{this.firstNumber}} {{this.secondNumber}}  
  {{/each}}
{{/if}}
```
{% endraw %}

You can generate a case class corresponding to this template using a macro annotation:

```tut:silent
import com.ovoenergy.comms.triggered.Template

@Template("service", "example-for-docs", "0.1")
object MeterReadsComm
```

This will generate a case class with the same name as the annotated object.

You can then build an instance of the generated case class. Note how it matches the structure of the template:

```tut:silent
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
```

You can then convert it into the appropriate format for inclusion in a `TriggeredV3` event:

```tut:silent
import com.ovoenergy.comms.model.TemplateData

val templateData: Map[String, TemplateData] = data.convertToTemplateData
```

Now you're ready to build an event:

```tut:silent
import com.ovoenergy.comms.model.{MetadataV2, TriggeredV3, Customer}
val metadata = MetadataV2(
	createdAt = java.time.Instant.now(),
	eventId = java.util.UUID.randomUUID().toString,
	traceToken = java.util.UUID.randomUUID().toString,
	deliverTo = Customer("my-customer"), 
	commManifest = MeterReadsComm.commManifest, // use the generated comm manifest in the companion object
	friendlyDescription = "awesome comm",
	source = "my amazing service",
	triggerSource = "my amazing service",
	canary = true,
	sourceMetadata = None
)

val event = TriggeredV3(metadata, templateData, None, None, None)
```

#### Viewing the generated code

The macro will print a slightly simplified version of the generated code to standard out when you compile, so you can see the structure of the generated model classes.

The output should look something like this:

```
Based on your comm template, the following code has been generated:

object CanaryTemplateData {
  val commManifest: CommManifest = CommManifest(CommType.Service, "canary", "2.0")
  case class Thing(word: String) { ... }
}
case class CanaryTemplateData(traceToken: String, optionalString: Option[String], things: Seq[CanaryTemplateData.Thing]) { ... }

Use these classes to construct an instance of CanaryTemplateData, then call its `convertToTemplateData`
to create a `Map[String, TemplateData]` suitable for use in a `TriggeredV3` event.
```

#### A note about IntelliJ support

Because our annotation uses a scala.meta macro to generate code at compile time, IntelliJ gets quite confused.

Autocomplete will not work, and your code will look like it doesn't compile:

![IntelliJ is a sea of red](../img/intellij-sea-of-red.png)

Rest assured that your code will compile just fine with sbt.

If you are using a IntelliJ 2017.1 (currently available as an EAP, due to be officially released in spring 2017), there will be a little icon next to the annotation:

![IntelliJ: expand scala.meta macro](../img/intellij-expand-macro.png)

Clicking this will expand the annotation, replacing the annotated object with the generated code. This means you can see exactly what the generated code looks like, and will also allow IntelliJ to understand it.

However, we recommend that you un-expand the macro again afterwards (currently the only way to do this in IntelliJ is to "Undo"), and avoid committing the generated code to git.

### Sending the event

All events must be encoded as Avro JSON. The Avro schemas are available [here](https://github.com/ovotech/comms-kafka-messages/tree/master/schemas).

If you are using Scala, we recommend that you use our `comms-kafka-serialisation` library to help you encode the event as Avro JSON:

```tut:silent
import com.ovoenergy.comms.serialisation.Serialisation.avroSerializer

val serializer = avroSerializer[TriggeredV2]

val json: Array[Byte] = serializer.serialize("comms.triggered.v2", event)
```

## Example project

We have a full working example project showing the recommended way to build and send a `TriggeredV2` event in Scala: [https://github.com/ovotech/comms-example-trigger](https://github.com/ovotech/comms-example-trigger)
