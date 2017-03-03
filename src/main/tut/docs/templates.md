---
layout: docs
title: "Templates"
---

# Templates

In order to send comms, you will need a template.

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
```

Let's go through some of that terminology.

### comm-type

Comms are broadly categorised by their type. The main reason for doing this is so that different comms of the same type can share things like a common header and footer, in order to provide a consistent look and feel.

The currently supported categories are `Service`, `Regulatory` and `Marketing`.

### comm-name

This is the unique name for your comm. It's an arbitrary string that you get to decide.

### version

Templates are versioned, and every published version of a comm is immutable. So if you want to make any changes to a comm template, you will need to publish a new version.

### Channels

Currently email is our only supported channel, so you must provide an email template. In future, when we add support for SMS and other channels, you will be able to include different templates for each channel that you want to use.

#### Email

An email template consists of up to four template files:

* `subject.txt` is a Handlebars template for the email subject
* `body.html` is a Handlebars template for the HTML body
* `body.txt` is a Handlebars template for the text body. This file is optional. If it is present, your comm will be sent as a multipart email with both HTML and text bodies.
* `sender.txt` is a custom sender in the format `Name <custom.address@ovoenergy.com>`. This file is optional. If it is not present, a default sender will be used.

As well as optional assets.

##### Assets

Any assets you want to publish with your email should be placed in the `email/assets` directory and any references to the assets in your email should be relational from the email folder to the assets directory.

For example if you add the following asset `email/assets/images/image1.png` then you would refer to the image with something like:
* `<img src="assets/images/image1.png" width="70" height="70" alt="My image" style="display:block; border:none; font-family:Arial, Helvetica, sans-serif; font-size:10px" border="0" />`

When your template is published the assets are placed in a public S3 repo and the links in the template are rewritten accordingly.

## Handlebars syntax

We support a subset of [Handlebars](http://handlebarsjs.com/) syntax for templates.

{% raw %}
As well as plain old Mustache-style placeholders (`{{foo}}`), you can also write conditionals using `{{#if foo}}`, and you can loop through lists of things using `{{#each things}}`.
{% endraw %}

### Built-in data

The following fields are provided automatically for you to refer to in your templates:

* `profile.firstName`
* `profile.lastName`
* `recipient.emailAddress` (only available in email templates, not in other channels)
* `system.year`
* `system.month`
* `system.dayOfMonth`

We can add more of these fields if necessary. If you would like a new field added, please get in touch.

### Simple example template

Here's a hypothetical HTML email body template:

{% raw %}
```
<p>Hello {{profile.firstName}} {{profile.lastName}}!</p>

<p>Thanks for doing that thing you just did. The result was: {{result}}.</p>

<p>Here's another piece of data you might find useful: {{thing}}.<p>
```
{% endraw %}

In this case, `profile.firstName` and `profile.lastName` are provided automatically, so you would need to provide the values of `result` and `thing` in your Kafka event when you trigger a comm. See the [Events](events.html) page for more details of how this is done.

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

We provide a common HTML email header and footer for use in comms of type `Service`.

{% raw %}
You can include these in your Handlebars templates using the standard Handlebars partial syntax: `{{> header }}` and `{{> footer }}`.
{% endraw %}

## Templates app

The app for publishing and viewing templates is [here](https://templates.ovo-comms.co.uk)

### Publishing a template

Whether you are publishing a new version of an existing template or a completely new template, via the app, you will be required to upload your template as a zip file.

The structure of the zip file should be something like:
 * `/email/subject.txt`
 * `/email/body.html`
 * `/email/body.txt`
 * `/email/sender.txt`
 * `/email/assets/images/someImage.png`
 * `/email/assets/icon/someIcon.png`
 
## Environments 

Please note that both UAT and PRD use the same published templates, so you can test the same templates against UAT before using in PRD.