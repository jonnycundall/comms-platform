---
layout: docs
title: "News & Updates"
---

# 'Brand' new API Endpoint (19/07/2018)

As part of our work to introduce the concept of a brand to the platform, the team have created a new api endpoint and introduced the concept of a TemplateId to the platform. This has a few implications for teams who use the platform, or are planning on doing so, of which will be covered in this platform. Feel free to skip to the TLDR at the end of the update if you are short of time!


## What has changed?
* The template used to render a comm is no longer determined in trigger request by a CommManifest, it is now determined by a TemplateManifest, which is comprised of a TemplateId and the template version
* The TemplateId is generated when a template is uploaded, for templates which existed prior to these changes we have generated an ID for you and these can be found within the template manager (more info about templates [here](templates.html)) 
* All new templates must use the /v3 endpoint of our API (information about our api can be found [here](rest-api.html)), requests for a newly uploaded template !via the V2 endpoint of our API will be rejected with a 403 http response, or via Kafka will result in failed events being raised
* Comms being triggered via legacy endpoints or Kafka will still for the time being, however we have plans to deprecate these in the near future - hold tight for further announcements on this matter

## Why have these changes been made?

This enables the platform to support sending SMS messages from a variety of 'Brands' (Ovo, Corgi, VNet, Boost, Lumo), if there are brands you'd like us to support not in this list please reach out to the team on #hello_comms and we'll be happy to help you out with this. This will also pave the way for the team to support the sending of regulatory letters, and also the capability of cost analysis on a brand (or even product) level. 


TLDR: Comms for any new templates uploaded need to be triggered via our /v3 api endpoint, the changes are backwards compatible so existing comms can stay as they are for now, but we'll be deprecating use of the /v2 api and the triggering of comms via Kakfa in the near future!
   