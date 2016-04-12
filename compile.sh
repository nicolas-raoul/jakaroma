#!/bin/sh

# Clean
rm -rf classes
mkdir classes
rm jakaroma.jar

# Compile
javac -encoding UTF-8 -cp lib/kuromoji-core-1.0-SNAPSHOT.jar:lib/kuromoji-ipadic-1.0-SNAPSHOT.jar -d classes/ src/fr/free/nrw/jakaroma/*

# Generate JAR
jar cfe jakaroma.jar fr.free.nrw.jakaroma.Jakaroma -C classes fr
