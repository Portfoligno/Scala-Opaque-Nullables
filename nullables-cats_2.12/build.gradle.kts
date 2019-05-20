plugins {
  maven
  scala
  `java-library`
}
val baseName = "nullables-cats"

apply {
  from("../$baseName/shared.gradle.kts")
}
