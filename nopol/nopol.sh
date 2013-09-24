#!/bin/sh

DIR="`dirname $0`"

CLASSPATH=$DIR/lib/com.gzoltar-0.0.3-jar-with-dependencies.jar
CLASSPATH=$CLASSPATH:$DIR/src/main/resources
CLASSPATH=$CLASSPATH:$DIR/target/nopol-0.0.1-SNAPSHOT-jar-with-dependencies.jar
MAINCLASS="fr.inria.lille.nopol.Main"
JAVA_FLAGS="-Xms2g -Xmx8g -XX:PermSize=128M"

COMMAND="java $JAVA_FLAGS -cp $CLASSPATH $MAINCLASS $*"

echo "Running: $COMMAND"

$COMMAND
