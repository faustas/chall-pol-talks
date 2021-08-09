addSbtPlugin("com.dwijnand"      % "sbt-dynver"          % "4.1.1")
addSbtPlugin("de.heikoseeberger" % "sbt-header"          % "5.6.0")
addSbtPlugin("com.typesafe.sbt"  % "sbt-native-packager" % "1.8.1")
addSbtPlugin("io.spray"          % "sbt-revolver"        % "0.9.1")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"        % "2.4.2")
addSbtPlugin("org.scoverage"     % "sbt-scoverage"       % "1.6.1")
addSbtPlugin("org.wartremover"   % "sbt-wartremover"     % "2.4.15")
// Needed to build debian packages via java (for sbt-native-packager).
libraryDependencies += "org.vafer" % "jdeb" % "1.8" artifacts (Artifact("jdeb", "jar", "jar"))
