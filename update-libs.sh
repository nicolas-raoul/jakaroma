#!/bin/sh
sudo apt-get install git maven
git clone git@github.com:atilika/kuromoji.git
cd kuromoji
mvn clean package -DskipTests=true
cp kuromoji-core/target/kuromoji-core-1.0-SNAPSHOT.jar ../libs
cp kuromoji-ipadic/target/kuromoji-ipadic-1.0-SNAPSHOT.jar ../libs
