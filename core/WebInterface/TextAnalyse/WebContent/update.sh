#!/usr/bin/sh

dir=$1
filepath=$2
cd $dir
python update.py $filepath
