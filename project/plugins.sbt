logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.4")

addSbtPlugin("name.de-vries" % "sbt-typescript" % "0.2.7")

// checks your typescript code for error prone constructions
addSbtPlugin("name.de-vries" % "sbt-tslint" % "0.9.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.10")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")
