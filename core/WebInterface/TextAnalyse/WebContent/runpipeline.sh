#!/usr/bin/sh

dirPip=$1
language=$2
thisDir=$3
nquads=$4
cd $nquads
echo $nquads
rm *.nq
cd $thisDir
cd $dirPip
java -Xmx3g -jar pipelineFinal.jar example.properties $language
cd $thisDir
