#!/bin/sh

extSrc=$1
filepath=$2
filepathsplit=$3
outputpath=$4
langSpecCommand="-tagger $extSrc/mate/models/CoNLL2009-ST-English-ALL.anna-3.3.postagger.model -parser $extSrc/mate/models/CoNLL2009-ST-English-ALL.anna-3.3.parser.model -srl $extSrc/mate/models/CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model -lemma $extSrc/mate/models/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model"


java "-cp" "$extSrc/mate/srl.jar:$extSrc/mate/lib/anna-3.3.jar:$extSrc/mate/lib/liblinear-1.51-with-deps.jar:$extSrc/mate/lib/opennlp-tools-1.4.3.jar:$extSrc/mate/lib/maxent-2.5.2.jar:$extSrc/mate/lib/trove.jar:$extSrc/mate/lib/seg.jar" "-Xmx3g" "se/lth/cs/srl/CompletePipeline" "eng" $langSpecCommand "-tokenize" "-test" "$filepath" "-out" "$outputpath$filepathsplit"

