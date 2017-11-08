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
      sms/
        body.txt
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

Currently email and SMS channels are supported and you must provide either an email template, an SMS template or both. 

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

#### SMS

An SMS template consists of a single template file:

* `body.txt` is a Handlebars template for the text of the message

The sender of the SMS is currently set by the platform. 

More on channels [here](channels.html).

#### Print
A Print template consists of a single template file:
* `body.html` is a Handlebars template for the content of the letter

##### Address
Print templates must include a div element with the id set to letterAddress. 
This element has the following mandatory and optional fields:
* Mandatory	
    - line 1
    - postcode
    - town
    - county
* Optional
    - line 2
    - country
			

##### Footers

To add footer for every page of a letter, the content of the footer has to be encapsulated by the <footer> tag. It is important that the <footer> element has to be the first element in the body. 

The bottom margin of the pages have to be offset by the height of the footer, by adding the `@page {margin-bottom: 30mm;}` css rule to the `<style>` element in the head of the template. For a 30mm high footer this will look the following: 

```
<head>
    <meta charset="utf-8" />

    <style>
        @page {
            margin-bottom: 30mm;
        }
    </style>
</head>

```

##### Page Breaks
As the content of the letter is broke into pages when rendering, in case the last element on a page exceeds the remaining space at the bottom of the page, the element is split and overflows to the next page. 

By adding the `break-inside: avoid` parameter to the style of the element we can avoid the element being split over two pages and enforce the entire element to be placed onto the next page. 

##### Colours
Letters are printed on industrial printers which work with CMYK colour encoding. 

To ensure that the printed document stylistically consistent, colours in print templates have to be CMYK encoded. For example: 
```
h1 {
    color: cmyk(83%, 10%, 100%, 1%);
    font-weight:normal;
    font-size: 18px;
}

.infoBox {
    border-radius: 10px;
    border: solid cmyk(30%, 0%, 40%, 0%) 2px;
    padding: 10px;
    padding-top: 0px;
}
```

##### Assets
Similarly to email, a print template can also reference optional assets, however images have to meet the following criterias:  
 - encoded in one of the following CMYK colourspace supporting formats: tif, tiff, jpg, jpeg. 
 - its size is set to ensure that it fits onto a page, if an image is wider or higher than the page, the template will be rejected.

Templates cannot contain scripts and cannot reference third party stylesheets, however stylesheets can be uploaded as assets.


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
 
#### Email and SMS

Could be as simple as:
 
 * `/email/subject.txt`
 * `/email/body.html`
 * `/sms/body.txt`
 
## Environments 

Please note that both UAT and PRD use the same published templates, so you can test the same templates against UAT before using in PRD.
