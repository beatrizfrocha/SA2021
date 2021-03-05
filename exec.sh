#!/usr/bin/env bash

# Como temos todos o robocode instalado de maneira diferente criem aqui um if para a vossa versão.
# Só precisam de mudar o vosso user e o que está dentro do then.
if [[ $(whoami) == "osodr" ]]; then
  ./build.sh
  cd /c/robocode/
  ./robocode.bat
  cd ~/Desktop/uni/SA/projeto
else
  ./build.sh
  ./run.sh
fi
