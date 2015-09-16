----- Standalone Stanford and OpenNLP coreference resolution system -----
author: Sergej Jaschonkow


*** Stanford standalone coreference resolution system ***
- StanfordCorefResExample.java: Example class on how to annotate a document with Stanford coreference annotations, write them to file and calculate precision, recall and F-Measure.

- requires StanfordCoreNLP version 3.5.2 (2015-04-20) which is downloadable from: http://nlp.stanford.edu/software/stanford-corenlp-full-2015-04-20.zip



*** OpenNLP standalone coreference resolution system ***
- OpenNLPCorefResExample.java: Example class on how to annotate a document with OpenNLP coreference annotations, write them to file and calculate precision, recall and F-Measure. Requires the VM argument: -DWNSEARCHDIR="folderOfWordNetDirectory"

- requires WordNet version 2.1 which is downloadable from: http://wordnet.princeton.edu/wordnet/download/old-versions/
- requires OpenNLP coreference models version 1.4 which are downloadable from: http://opennlp.sourceforge.net/models-1.4/english/coref/