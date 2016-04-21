#!/usr/bin/sh

dir=$1
input=$2
locPip=$3
urlBlazegraph=$4
cd $dir
python SimpleHTTPServerWithUpload.py 8081 $input $dir $locPip $urlBlazegraph
cd $thisDir

