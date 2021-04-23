
lazy val presentation = (project in file("."))
  .enablePlugins(ParadoxRevealPlugin)
  .enablePlugins(ParadoxSitePlugin)
  .settings(
    name := "trifunctors_presentation",
    publish / skip := true,
    // add dependencies if needed
    // libraryDependencies += "com.example" %% "example-lib-scala" % "1.0.0",
    Compile / paradoxRevealTheme ~= {
      _.withTheme(ParadoxRevealTheme.ThemeNight)  // choose theme
        .withDefaultTransition(ParadoxRevealTheme.TransitionSlide) // choose transition
        // .withMathPlugin // add plugin if needed
    },
    // exclude includes folder
    (Compile / paradoxMarkdownToHtml / excludeFilter) :=
      (Compile / paradoxMarkdownToHtml / excludeFilter).value ||
        ParadoxPlugin.InDirectoryFilter((Compile / paradox / sourceDirectory).value / "includes"),
    (Compile / paradoxPdfSite / excludeFilter) :=
      (Compile / paradoxMarkdownToHtml / excludeFilter).value ||
        ParadoxPlugin.InDirectoryFilter((Compile / paradox / sourceDirectory).value / "includes"),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.2.0" withSources(),
      "dev.zio" %% "zio-prelude"  % "1.0.0-RC1" withSources(),
      "io.7mind.izumi" %% "fundamentals-bio" % "0.10.19" withSources(),
      "org.scalatest" %% "scalatest" % "3.2.2" % Test withSources(),
    )
  )