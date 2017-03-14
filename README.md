# comms-platform

This is the documentation microsite for the OVO comms platform.

## To run the site locally

You must have jekyll installed: `gem install jekyll`.

```
sbt makeMicrosite
cd target/site
jekyll serve
```

The site should now be running at [http://localhost:4000/comms-platform/](http://localhost:4000/comms-platform/).

## To publish the site

The site is published automatically by CircleCI whenever a PR is merged.

But if you want to publish from your local machine, you can do so with `sbt publishMicrosite`.
