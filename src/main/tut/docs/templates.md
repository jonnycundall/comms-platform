---
layout: docs
title: "Templates"
---

# Templates

In order to send comms, you will need a template. Don't know anything about html? If you are doing a fairly standard template then there is no need to outsource your email building, click [here](htmlexamples.md) for a kickstart guide

## Directory structure

A comm template is a directory on S3 that adheres to the following structure.

```
<comm-type>/
  <comm-name>/
    <version>/
      email/
        subject.txt
        body.html
        body.txt (optional)
        sender.txt (optional)
        assets/ (optional)
      sms/
        body.txt
```

Let's go through some of that terminology.

### template id
A unique id for the template, generated at the time of publishing a new template. This id is required in the HTTP request to trigger a comm, find out more about the REST API [here](rest-api.md). 

The template id is displayed under the template name in the [Template Manager App](https://templates.ovo-comms.co.uk). 
![Template ID in Template Manager](../img/templateId.png)

### brand
Every template is assigned to a brand. The following brands are currently configured:
* OVO
* Boost
* Lumo
* Corgi
* VNet
 
This paves the way for future work to enable us to send SMS messages from the brand associated with the given comm, using an alphanumeric sender relevant to the specific brand. For more information on this please see the [SMS section](sms.html).

### comm-type

Comms are broadly categorised by their type. The main reason for doing this is so that different comms of the same type can share things like a common header and footer, in order to provide a consistent look and feel.

The currently supported categories are `Service`, `Regulatory` and `Marketing`.

### comm-name

It's an arbitrary string that you get to decide.

### version

Templates are versioned, and every published version of a comm is immutable. So if you want to make any changes to a comm template, you will need to publish a new version.

### Channels

Currently email, SMS and print channels are supported and you must provide either an email template, an SMS template, a print template or a combination of these three. 


## Handlebars syntax

We support a subset of [Handlebars](http://handlebarsjs.com/) syntax for templates.

{% raw %}
As well as plain old Mustache-style placeholders (`{{foo}}`), you can also write conditionals using `{{#if foo}}`, and you can loop through lists of things using `{{#each things}}`.
{% endraw %}

### Built-in data

The following fields are provided automatically for you to refer to in your templates:

* `profile.firstName` (only for customer comms)
* `profile.lastName` (only for customer comms)
* `recipient.emailAddress` (only available in email templates, not in other channels)
* `recipient.phoneNumber` (only available in SMS templates, not in other channels)
* `system.year`
* `system.month`
* `system.dayOfMonth`

We can add more of these fields if necessary. If you would like a new field added, please get in touch.

#### Customer and non-customer comms

If you specify an OVO customer ID for the `deliverTo` field when you trigger your comm, we look up the customer's profile and provide it for use in your template as the `profile.*` fields.

When you send a non-customer comm, by specifying contact details instead of a customer ID, we do not provide any `profile.*` fields.

### Simple example template

Here's a hypothetical HTML email body template:

{% raw %}
```
<p>Hello {{profile.firstName}} {{profile.lastName}}!</p>

<p>Thanks for doing that thing you just did. The result was: {{result}}.</p>

<p>Here's another piece of data you might find useful: {{thing}}.<p>
```
{% endraw %}

In this case, `profile.firstName` and `profile.lastName` are provided automatically (assuming this is a customer comm), so you would need to provide the values of `result` and `thing` in your Kafka event when you trigger a comm. See the [Events](events.html) page for more details of how this is done.

### More complex example

Here's another hypothetical example that includes conditionals and loops:

{% raw %}
```
<p>Hello. Please give us your meter reads.</p>

{{#if gas}}
  <div>
  We need reads for the following gas meters:

  <ul>
  {{#each gas.meters}}
    <li>{{this.meterNumber}}</li>
  {{/each}}
  </ul>

  </div>
{{/if}}

{{#if electricity}}
  <div>
  We need reads for the following electricity meters:

  <ul>
  {{#each electricity.meters}}
    <li>{{this.firstNumber}} {{this.secondNumber}}</li> 
  {{/each}}
  </ul>

  </div>
{{/if}}
```
{% endraw %}

### Partials

For emails we provide a common HTML email header and footer for use in comms of type `Service`.

{% raw %}
You can include these in your Handlebars templates using the standard Handlebars partial syntax: `{{> header }}` and `{{> footer }}`.
{% endraw %}

## Templates app

The app for publishing and viewing templates is [here](https://templates.ovo-comms.co.uk)

### Publishing a template

Whether you are publishing a new version of an existing template or a completely new template, via the app, you will be required to upload your template as a zip file.

Some example file structures for the zip file could be:

#### Email only

 * `/email/subject.txt`
 * `/email/body.html`
 * `/email/body.txt`
 * `/email/sender.txt`
 * `/email/assets/images/someImage.png`
 * `/email/assets/icon/someIcon.png`

#### SMS only
 
 * `/sms/body.txt`
 
#### Print only
 * `/print/body.html`
 * `/print/assets/images/someImage.png`
 
#### Email and SMS

Could be as simple as:
 
 * `/email/subject.txt`
 * `/email/body.html`
 * `/sms/body.txt`
 
#### Email, SMS and Print
 
 * `/email/subject.txt`
 * `/email/body.html`
 * `/sms/body.txt`
 * `/print/body.html`
 
## Environments 

Please note that both UAT and PRD use the same published templates, so you can test the same templates against UAT before using in PRD.
