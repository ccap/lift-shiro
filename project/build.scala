import sbt._, Keys._
import LiftModuleBuild._


object BuildSettings {
  val buildOrganization = "eu.getintheloop"
  val buildVersion      = "0.0.9-ccap2"
  val buildScalaVersion = "2.12.2"

  val buildSettings = Seq(
    liftVersion <<= liftVersion ?? "3.0.1",
    liftEdition <<= liftVersion apply { _.substring(0,3) },
    name <<= (name, liftEdition) { (n, e) =>  n + "_" + e },
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    scalacOptions <<= scalaVersion map { v: String =>
      val opts = "-deprecation" :: "-unchecked" :: Nil
      if (v.startsWith("2.9.")) opts else opts ++ ( "-language:implicitConversions" :: "-language:postfixOps" :: Nil)},
    crossScalaVersions := Seq("2.12.2"),
    resolvers ++= Seq(
      "CB Central Mirror" at "http://repo.cloudbees.com/content/groups/public",
      "Shiro Releases" at "https://repository.apache.org/content/repositories/releases/",
      "Shiro Snapshots" at "https://repository.apache.org/content/repositories/snapshots/",
      "sonatype.repo" at "https://oss.sonatype.org/content/repositories/public/"
    ),
    publishTo <<= version { (v: String) => 
      if (v.trim.endsWith("SNAPSHOT"))
        Some("CCAP Snapshots" at "http://repoman.wicourts.gov/artifactory/libs-snapshot-local")
      else
        Some("CCAP Releases" at "http://repoman.wicourts.gov/artifactory/libs-release-local")
    },
    credentials ++= Seq(
      Credentials(Path.userHome / ".ivy2" / ".credentials")
    ),
    publishArtifact in Test := false,
    pomIncludeRepository := { repo => false },
    pomExtra := (
      <url>https://github.com/timperrett/lift-shiro</url>
      <licenses>
        <license>
          <name>Apache 2.0 License</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:timperrett/lift-shiro.git</url>
        <connection>scm:git:git@github.com:timperrett/lift-shiro.git</connection>
      </scm>
      <developers>
        <developer>
          <id>timperrett</id>
          <name>Timothy Perrett</name>
          <url>http://timperrett.com</url>
        </developer>
        <developer>
          <id>fmpwizard</id>
          <name>Diego Medina</name>
          <url>http://www.fmpwizard.com</url>
        </developer>
      </developers>)
  )
}

object LiftShiroBuild extends Build {

  liftVersion ?? "3.0.1"

  lazy val root = Project("lift-shiro-root", file("."),
    settings = BuildSettings.buildSettings ++ Seq(
      // the root is just an aggregator so dont publish a JAR
      publishArtifact in (Compile, packageBin) := false,
      publishArtifact in (Test, packageBin) := false,
      publishArtifact in (Compile, packageDoc) := false,
      publishArtifact in (Compile, packageSrc) := false
    )) aggregate(library)


  lazy val library: Project = Project("lift-shiro", file("library"), 
    settings = BuildSettings.buildSettings ++ (
      libraryDependencies ++= Seq(
        "org.apache.shiro" % "shiro-core" % "1.4.2",
        "org.apache.shiro" % "shiro-web" % "1.4.2"
      )
    ) ++ Seq(
      libraryDependencies <+= liftVersion("net.liftweb" %% "lift-webkit" % _ % "provided")
    )
  )
  
/*
  lazy val example = Project("lift-shiro-example", file("example"),
    settings = BuildSettings.buildSettings ++ (
      libraryDependencies ++= Seq(
        "net.liftmodules"   %% "fobo-jquery_2.5"  % "1.0"              % "compile",
        "org.eclipse.jetty" % "jetty-webapp"      % "7.3.0.v20110203"  % "container",
        "ch.qos.logback"    % "logback-classic"   % "0.9.26"
      )
    ) ++ Seq(
      libraryDependencies <+= liftVersion("net.liftweb" %% "lift-webkit" % _ % "compile")
    ) ++
      com.github.siasia.WebPlugin.webSettings
  ) dependsOn library
*/
}
