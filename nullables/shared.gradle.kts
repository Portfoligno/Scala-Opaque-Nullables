
val baseName = "nullables"

// Substitutes for generated extension properties
val sourceSets = project.the<SourceSetContainer>()
val SourceSetContainer.main get() = getByName("main")
val SourceSetContainer.test get() = getByName("test")
val SourceSet.scala get() = withConvention(ScalaSourceSet::class) { scala }

// Add shared sources
sourceSets.apply {
  main.scala.srcDir("../$baseName/src/main/scala")
  test.scala.srcDir("../$baseName/src/test/scala")
}

// Load gradle.properties
val scalaVersion: String by lazy {
  val scalaVersion: String? by project
  scalaVersion ?: scalaMinorVersion
}
val scalaMinorVersion = project.name.substringAfterLast('_')

val dependencyScalaVersion: String by lazy {
  val dependencyScalaVersion: String? by project
  dependencyScalaVersion ?: scalaMinorVersion
}

val useMacroParadise: Boolean by lazy {
  val useMacroParadise: String? by project
  useMacroParadise?.toBoolean() == true
}

val usePartialUnification: Boolean by lazy {
  val usePartialUnification: String? by project
  usePartialUnification?.toBoolean() == true
}

val scalaTestVersion: String by lazy {
  val scalaTestVersion: String? by project
  scalaTestVersion ?: "3.0.7"
}

// Dependencies
val scalaCompilerPlugin: Configuration = configurations.create("scalaCompilerPlugin")

repositories {
  jcenter()
}
dependencies {
  if (useMacroParadise) {
    scalaCompilerPlugin("org.scalamacros", "paradise_$scalaVersion", "2.1.1")
  }
  "api"("org.scala-lang", "scala-library", scalaVersion)

  "testImplementation"("org.scalatest", "scalatest_$dependencyScalaVersion", scalaTestVersion)
}

// Compiler options
var BaseScalaCompileOptions.parameters: List<String>
  get() = additionalParameters ?: listOf()
  set(x) { additionalParameters = x }

tasks.withType<ScalaCompile> {
  if (!useMacroParadise) {
    scalaCompileOptions.parameters += "-Ymacro-annotations"
  }
  if (usePartialUnification) {
    scalaCompileOptions.parameters += "-Ypartial-unification"
  }
  scalaCompileOptions.parameters += listOf(
      "-Xplugin:" + scalaCompilerPlugin.asPath,
      "-Xfatal-warnings",
      "-language:higherKinds",
      "-language:implicitConversions")
}

// Scalatest
val scalaTest by tasks.registering(JavaExec::class) {
  main = "org.scalatest.tools.Runner"
  args = listOf("-R", "${sourceSets.test.scala.outputDir}", "-o")
  classpath = sourceSets.test.runtimeClasspath
}
tasks["test"].dependsOn(scalaTest)
