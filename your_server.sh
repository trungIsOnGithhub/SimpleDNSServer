#!/bin/sh
#
set -e
mvn -B --quiet package -Ddir=/tmp/codecrafters-dns-target
exec java -jar /tmp/codecrafters-dns-target/java_dns.jar "$@"