---
layout: docs
title: "Events"
---

# Events

## TriggeredV3

In order to send a comm to a customer, you need to trigger the comm by sending a `TriggeredV3` event to the `comms.triggered.v3` Kafka topic.

### Event structure

The `TriggeredV3` event has the following fields: 

* `metadata` - This includes a unique ID for the comm and a DeliverTo (either the ID of the customer you wish to send the given comm to, or the contact details if the comm recipient does not have a Salesforce account). The fields are documented in the [source code](https://github.com/ovotech/comms-kafka-messages/blob/master/modules/core/src/main/scala/com/ovoenergy/comms/model/MetadataV2.scala) and in the Avro schema (available in the schema registry).
* `templateData` - This is the customer-specific data you want to use to fill in the placeholders in your comm template. **Note:** If you fail to fill in all the placeholders in the template, your comm will not be sent!
* `deliverAt` (optional) - If you want to schedule the comm to be sent in the future, use this field to specify the delivery time. See the [scheduling](scheduling.html) page for more details.
* `expireAt` (optional) - If your comm is time sensitive, you can use this field to specify the latest delivery time. See the [scheduling](scheduling.html) page for more details.
* `preferredChannels` (optional) - An ordered list of channels over which you would like to send the comm. See the [channels](channels.html) page for more details.

### DeliverTo - customer vs non-customer comms

As part of the metadata, you need to specify the `deliverTo` field. You can provide one of two things:

* A `Customer`, containing a global customer ID, if you want to send the comm to an OVO customer.
* A `ContactDetails`, if you want to send the comm to an arbitrary recipient. You must provide an email address, a mobile phone number (for SMS) or both.

### Building the event

As long as you send us a valid `TriggeredV3` event, you can build it however you like.

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

val event = TriggeredV3(
  metadata = metadata, 
  templateData = templateData, 
  deliverAt = None, 
  expireAt = None, 
  preferredChannels = None
)
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

If you are using IntelliJ 2017.1 or newer, there will be a little icon next to the annotation:

![IntelliJ: expand scala.meta macro](../img/intellij-expand-macro.png)

Clicking this will expand the annotation, replacing the annotated object with the generated code. This means you can see exactly what the generated code looks like, and will also allow IntelliJ to understand it.

However, we recommend that you un-expand the macro again afterwards (currently the only way to do this in IntelliJ is to "Undo"), and avoid committing the generated code to git.

### Sending the event

Events should be sent using the Kafka cluster on Aiven.

All events must be encoded as Avro binary, with the schema ID in a header. The Avro schemas are all registered in the schema registry on Aiven.

If you are using Scala, we recommend that you use our [`comms-kafka-serialisation`](https://github.com/ovotech/comms-kafka-serialisation) library to help you encode the event as Avro binary:

```tut:silent
import com.ovoenergy.comms.serialisation.Serialisation.avroBinarySchemaRegistrySerializer
import com.ovoenergy.comms.serialisation.Codecs._
import com.ovoenergy.kafka.serialization.avro.SchemaRegistryClientSettings

val schemaRegistryUrl = sys.env("SCHEMA_REGISTRY_URL")
val schemaRegistryUsername = sys.env("SCHEMA_REGISTRY_USERNAME")
val schemaRegistryPassword = sys.env("SCHEMA_REGISTRY_PASSWORD")
val schemaRegistryClientSettings = SchemaRegistryClientSettings(schemaRegistryUrl, schemaRegistryUsername, schemaRegistryPassword)

val serializer = avroBinarySchemaRegistrySerializer[TriggeredV3](schemaRegistryClientSettings, "comms.triggered.v3")

val bytes: Array[Byte] = serializer.serialize("comms.triggered.v3", event)
```

## Example project

We have a full working example project showing the recommended way to build and send a `TriggeredV3` event in Scala: [https://github.com/ovotech/comms-example-trigger](https://github.com/ovotech/comms-example-trigger)
