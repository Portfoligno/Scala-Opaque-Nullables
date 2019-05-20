
val baseName = "nullables-cats"

project.the<SourceSetContainer>().apply {
  getByName("main") {
    withConvention(ScalaSourceSet::class) {
      scala.srcDir("../$baseName/src/main/scala")
    }
  }
  getByName("test") {
    withConvention(ScalaSourceSet::class) {
      scala.srcDir("../$baseName/src/test/scala")
    }
  }
}
val scalaCompilerPlugin: Configuration = configurations.create("scalaCompilerPlugin")

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

val scalaTestVersion: String by lazy {
  val scalaTestVersion: String? by project
  scalaTestVersion ?: "3.0.7"
}

repositories {
  jcenter()
}
dependencies {
  if (useMacroParadise) {
    scalaCompilerPlugin("org.scalamacros", "paradise_$scalaVersion", "2.1.1")
  }
  "api"("org.scala-lang", "scala-library", scalaVersion)
  "api"("org.typelevel", "cats-core_$dependencyScalaVersion", "2.0.0-M1")
  "api"(project(":nullables_$scalaMinorVersion", "default"))

  "testImplementation"("junit", "junit", "4.12")
  "testImplementation"("org.scalatest", "scalatest_$dependencyScalaVersion", scalaTestVersion)
}

var BaseScalaCompileOptions.parameters: List<String>
  get() = additionalParameters ?: listOf()
  set(x) { additionalParameters = x }

tasks.withType<ScalaCompile> {
  if (!useMacroParadise) {
    scalaCompileOptions.parameters += "-Ymacro-annotations"
  }
  scalaCompileOptions.parameters += listOf(
      "-Xplugin:" + scalaCompilerPlugin.asPath,
      "-Xfatal-warnings",
      "-language:higherKinds",
      "-language:implicitConversions")
}
