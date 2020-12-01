#!/usr/bin/env bash
jre-lin/bin/java -Djava.library.path=lib -Dlogback.configurationFile=config/logback.xml -cp "lib/*" com.univocity.freecommerce.Main
