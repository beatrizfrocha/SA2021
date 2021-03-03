#!/usr/bin/env bash

mvn clean compile

sudo cp -r src/main/java/sa /root/robocode/robots
sudo cp -r target/classes/sa /root/robocode/robots

