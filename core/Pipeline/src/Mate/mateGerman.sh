#!/bin/sh

extSrc=$1
filepath=$2
filepathsplit=$3
outputpath=$4
langSpecCommand="-tagger $extSrc/mate/models/german/tag-ger-3.6.model -parser $extSrc/mate/models/german/parser-ger-3.6.model -morph  $extSrc/mate/models/german/morphology-ger-3.6.model -srl $extSrc/mate/models/german/tiger-complete-predsonly-srl-4.11.srl.model -lemma $extSrc/mate/models/german/lemma-ger-3.6.model"

java "-cp" "$extSrc/mate/srl.jar:$extSrc/mate/lib/anna-3.3.jar:$extSrc/mate/lib/liblinear-1.51-with-deps.jar:$extSrc/mate/lib/opennlp-tools-1.4.3.jar:$extSrc/mate/lib/maxent-2.5.2.jar:$extSrc/mate/lib/trove.jar:$extSrc/mate/lib/seg.jar" "-Xmx3g" "se/lth/cs/srl/CompletePipeline" "ger" $langSpecCommand "-tokenize" "-test" "$filepath" "-out" "$outputpath$filepathsplit"
