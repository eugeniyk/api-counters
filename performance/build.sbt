name := "performance"

enablePlugins(JmhPlugin)

sourceDirectory in Jmh := (sourceDirectory in Compile).value
classDirectory in Jmh := (classDirectory in Compile).value
dependencyClasspath in Jmh := (dependencyClasspath in Compile).value
resourceDirectory in Jmh := (resourceDirectory in Compile).value