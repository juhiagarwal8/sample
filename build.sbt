lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := "play-java-rest-api-example",
    version := "2.8.x",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      javaJpa,
       "com.h2database" % "h2" % "1.4.199",
      "io.dropwizard.metrics" % "metrics-core" % "4.1.1",
      "com.palominolabs.http" % "url-builder" % "1.1.0",
      "net.jodah" % "failsafe" % "2.3.1",
      "org.apache.commons" % "commons-collections4" % "4.4",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.1",
      "com.datastax.cassandra" % "cassandra-driver-core" % "3.7.0"
    ),
    PlayKeys.externalizeResources := false,
    testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v")),
    javacOptions ++= Seq(
      "-Xlint:unchecked",
      "-Xlint:deprecation",
      "-Werror"
    )
  )

EclipseKeys.preTasks := Seq(compile in Compile, compile in Test)
EclipseKeys.projectFlavor := EclipseProjectFlavor.Java

EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)

val gatlingVersion = "3.3.1"
lazy val gatling = (project in file("gatling"))
  .enablePlugins(GatlingPlugin)
  .settings(
    scalaVersion := "2.12.10",
    libraryDependencies ++= Seq(
      "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % Test,
      "io.gatling" % "gatling-test-framework" % gatlingVersion % Test
    )
  )
