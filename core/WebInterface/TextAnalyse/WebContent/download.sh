#!/usr/bin/sh

dir=$1
nquads="nquads"
dirNq=$dir$nquads
echo $dirNq
cd $dirNq
python -m SimpleHTTPServer 8082
cd $dir
