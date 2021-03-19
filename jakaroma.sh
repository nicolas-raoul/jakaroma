#!/bin/sh

STRING=$1

java -Dfile.encoding=UTF-8 jakaroma-1.0-SNAPSHOT-jar-with-dependencies.jar -jar $1
