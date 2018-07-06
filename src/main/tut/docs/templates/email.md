---
layout: docs
title: "Email templates"
---

# Email

An email template consists of up to four template files:

* `subject.txt` is a Handlebars template for the email subject
* `body.html` is a Handlebars template for the HTML body
* `body.txt` is a Handlebars template for the text body. This file is optional. If it is present, your comm will be sent as a multipart email with both HTML and text bodies.
* `sender.txt` is a custom sender in the format `Name <custom.address@ovoenergy.com>`. This file is optional. If it is not present, a default sender will be used.

As well as optional assets.

## Assets

Any assets you want to publish with your email should be placed in the `email/assets` directory and any references to the assets in your email should be relational from the email folder to the assets directory.

For example if you add the following asset `email/assets/images/image1.png` then you would refer to the image with something like:
* `<img src="assets/images/image1.png" width="70" height="70" alt="My image" style="display:block; border:none; font-family:Arial, Helvetica, sans-serif; font-size:10px" border="0" />`

When your template is published the assets are placed in a public S3 repo and the links in the template are rewritten accordingly.
