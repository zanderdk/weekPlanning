name := "weekplanning"

version := "1.0"

lazy val `weekplanning` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  javaWs,
  specs2 % Test,
  "com.h2database" % "h2" % "1.3.175",
  "com.typesafe.slick" %% "slick" % "3.1.0",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",

  //webjars npm
  "org.webjars.npm" % "systemjs" % "0.19.26",
  "org.webjars.npm" % "todomvc-common" % "1.0.2",
  "org.webjars.npm" % "rxjs" % "5.0.0-beta.7",
  "org.webjars.npm" % "es6-promise" % "3.1.2",
  "org.webjars.npm" % "es6-shim" % "0.35.0",
  "org.webjars.npm" % "reflect-metadata" % "0.1.3",
  "org.webjars.npm" % "zone.js" % "0.6.12",
  "org.webjars.npm" % "typescript" % "1.9.0-dev.20160516",

  //angular
  "org.webjars.npm" % "angular__core" % "2.0.0-rc.3",
  "org.webjars.npm" % "angular__http" % "2.0.0-rc.3",
  "org.webjars.npm" % "angular__common" % "2.0.0-rc.3",
  "org.webjars.npm" % "angular__compiler" % "2.0.0-rc.3",
  "org.webjars.npm" % "angular__platform-browser" % "2.0.0-rc.3",
  "org.webjars.npm" % "angular__platform-browser-dynamic" % "2.0.0-rc.3",
  "org.webjars.npm" % "angular__router-deprecated" % "2.0.0-rc.2",
  "org.webjars.npm" % "angular__upgrade" % "2.0.0-rc.3",
  "org.webjars.npm" % "angular__router" % "3.0.0-alpha.7",

  //tslint dependency
  "org.webjars.npm" % "tslint-eslint-rules" % "1.2.0",
  "org.webjars.npm" % "codelyzer" % "0.0.19"
)

dependencyOverrides ++= Set(
  "org.webjars.npm" % "minimatch" % "3.0.0",
  "org.webjars.npm" % "glob" % "7.0.3"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

routesGenerator := InjectedRoutesGenerator

typingsFile := Some(baseDirectory.value / "typings" / "index.d.ts")

// use the webjars npm directory (target/web/node_modules ) for resolution of module imports of angular2/core etc
resolveFromWebjarsNodeModulesDir := true

// use the combined tslint and eslint rules plus ng2 lint rules
(rulesDirectories in tslint) := Some(List(tslintEslintRulesDir.value,ng2LintRulesDir.value))

fork in run := true
