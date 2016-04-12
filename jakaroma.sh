#!/bin/sh

STRING=$1

java -Dfile.encoding=UTF-8 -cp lib/kuromoji-core-1.0-SNAPSHOT.jar:lib/kuromoji-ipadic-1.0-SNAPSHOT.jar:classes fr.free.nrw.jakaroma.Jakaroma $1
