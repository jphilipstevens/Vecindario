name := "app"

version := "1.0-SNAPSHOT"


resolvers ++= Seq(
        "Oss repo" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "eu.trentorise.opendata" % "jackan" % "0.3.0-SNAPSHOT",
  "postgresql" % "postgresql" % "9.1-901.jdbc4"
)     

compile in Test <<= PostCompile(Test)

play.Project.playJavaSettings
