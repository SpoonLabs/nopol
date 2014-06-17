#!/bin/sh

DIR="`dirname $0`"
LIB="$3"

CLASSPATH=$DIR/lib/com.gzoltar-0.0.5-jar-with-dependencies.jar
CLASSPATH=$CLASSPATH:$DIR/src/main/resources
CLASSPATH=$CLASSPATH:$DIR/target/nopol-0.0.1-SNAPSHOT-jar-with-dependencies.jar
CLASSPATH=$CLASSPATH:$LIB
MAINCLASS="fr.inria.lille.nopol.Main"
JAVA_FLAGS="-Xms512m -Xmx1024m -XX:PermSize=128M -XX:MaxPermSize=1G -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=heap_dump.hprof"

COMMAND="java $JAVA_FLAGS -cp $CLASSPATH $MAINCLASS $1 $2"
echo "Running: $COMMAND \n"
$COMMAND


