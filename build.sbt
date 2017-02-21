enablePlugins(MicrositesPlugin)

micrositeName := "OVO Comms Platform"
micrositeBaseUrl := "/comms-platform"
micrositeDocumentationUrl := "/comms-platform/docs"
micrositeAuthor := "Comms team"
micrositeHomepage := "https://ovoenergy.slack.com/messages/hello_comms/"
micrositeGithubOwner := "ovotech"
micrositeGithubRepo := "comms-platform"
micrositePalette := Map(
  "brand-primary" -> "#0a9928",
  "brand-secondary" -> "#24911d",
  "brand-tertiary" -> "#2d232f",
  "gray-dark" -> "#7a7f8a",
  "gray" -> "#a2a5ad",
  "gray-light" -> "#d1d2d6",
  "gray-lighter" -> "#f6f8fc",
  "white-color" -> "#ffffff"
)

scalaVersion := "2.11.8"
resolvers += Resolver.bintrayRepo("ovotech", "maven")
libraryDependencies ++= Seq(
  "com.ovoenergy" %% "comms-kafka-messages" % "1.4",
  "com.ovoenergy" %% "comms-triggered-event-builder" % "1.0",
  "com.ovoenergy" %% "comms-kafka-serialisation" % "2.0"
)
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-beta4" cross CrossVersion.full)
