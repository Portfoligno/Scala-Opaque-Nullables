sequenceOf(
    "2.12",
    "2.13.0-RC2"
)
    .forEach {
      include("nullables_$it")
      include("nullables-cats_$it")
    }
