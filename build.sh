#!/bin/bash
./gradlew build
cp build/libs/twine-hits-all.jar ~/Applications/twine/2.0/modules/twine-hits.jar
clear
cd ~/Applications/twine/2.0/
java -Xmx1G -classpath ~/Applications/twine/2.0/twine-2.0.jar:dependencies/*:modules/* net.termer.twine.Twine --classpath-loaded -s $@