#!/bin/bash
ABSPATH=$(cd "$(dirname "$0")"; pwd)
SCRIPTPATH=$ABSPATH
echo $SCRIPTPATH
SCRIPT="$SCRIPTPATH/jre-mac/Contents/Home/bin/java -Dlogback.configurationFile=$SCRIPTPATH/config/logback.xml -cp "\'"$SCRIPTPATH/lib/*"\'" com.univocity.freecommerce.Main"
echo $SCRIPT
osascript -e "do shell script \"$SCRIPT\" "
