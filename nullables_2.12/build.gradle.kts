plugins {
  maven
  scala
  `java-library`
}
val baseName = "nullables"

apply {
  from("../$baseName/shared.gradle.kts")
}
