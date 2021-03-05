#!/usr/bin/env bash

# Como temos todos o robocode instalado de maneira diferente criem aqui um if para a vossa versão.
# Só precisam de mudar o vosso user e o que está dentro do then.
if [[ $(whoami) == "osodr" ]]; then
  ../../../apache-maven-3.6.3/bin/mvn clean compile
  cp -r src/main/java/sa /c/robocode/robots
  cp -r target/classes/sa /c/robocode/robots
else
  mvn clean compile
  sudo cp -r src/main/java/sa /root/robocode/robots
  sudo cp -r target/classes/sa /root/robocode/robots
fi


echo "Build completed"

