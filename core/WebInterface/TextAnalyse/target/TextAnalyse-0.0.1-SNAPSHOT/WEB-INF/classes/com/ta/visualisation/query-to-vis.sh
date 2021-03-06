#!/bin/bash
# feed the argument files into InteractiveQuery 

CLASSPATH=./java/bin':'`find java/lib | egrep 'jar$' | perl -e 'while(<>) { s/\n/:/gs; print;}'`.;
if echo $OSTYPE | grep -i cygwin >&/dev/null; then
	CLASSPATH=`echo $CLASSPATH | sed s/':'/';'/g`;
fi;

TRIPLEVIS=./;

TMPjson=$TRIPLEVIS/converters/`echo $0 | sed -e s/'.*\/'//g -e s/'\([^\.]\)\..*'/'\1'/`.tmp;
while [ -e TMPjson ]; do
	TMPjson=$TRIPLEVIS/converters/`echo $0 | sed -e s/'.*\/'//g -e s/'\..*'//`.`date +%N`.tmp;
done;
echo > $TMPjson;

# context of "trench"
# PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>
# PREFIX tbox: <http://purl.org/acoli/open-ie/>
# PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
# PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
# CONSTRUCT { ?aC ?r ?bC. ?bC ?s ?aC. ?cC ?t ?bC. ?bC ?u ?cC }
# WHERE { 
# ?a ?ign ?c. ?c a ?cC. ?a a ?aC.
# { ?a a tbox:trench } UNION { ?c a tbox:trench}.
# {{ ?a ?r ?b } UNION { ?b ?s ?a}. ?b a ?bC.} UNION 
# {{ ?c ?t ?b } UNION { ?b ?u ?c}. ?b a ?bC.}
# }

# "trench" context plus labels
# PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>
# PREFIX tbox: <http://purl.org/acoli/open-ie/>
# PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
# PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
# CONSTRUCT { ?aC ?r ?bC. ?bC ?s ?aC. ?cC ?t ?bC. ?bC ?u ?cC. ?bC rdfs:label ?bL} 
# WHERE { 
# ?a ?ign ?c. ?c a ?cC. ?a a ?aC.
# { ?a a tbox:trench } UNION { ?c a tbox:trench}.
# {{ ?a ?r ?b } UNION { ?b ?s ?a}. ?b a ?bC. ?b rdfs:label ?bL} UNION 
# {{ ?c ?t ?b } UNION { ?b ?u ?c}. ?b a ?bC. ?b rdfs:label ?bL}
# }

# what has been found
# PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>
# PREFIX tbox: <http://purl.org/acoli/open-ie/>
# PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
# PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
# CONSTRUCT { ?aC ?r ?bC. ?bC ?s ?aC. ?cC ?t ?bC. ?bC ?u ?cC }
# WHERE { 
# [] tbox:find ?c. ?c a ?cC.
# { ?a ?r ?c. ?a a ?aC. {{ ?a ?r ?b } UNION { ?b ?s ?a}. ?b a ?bC.}} UNION 
# {{ ?c ?t ?b } UNION { ?b ?u ?c}. ?b a ?bC.}
# }

# what has been found II
# PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>
# PREFIX terms: <http://purl.org/acoli/open-ie/>
# PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
# PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
# CONSTRUCT { 
	# terms:FINDER ?r ?cC.
	# ?aC rdfs:subClassOf terms:FINDER. ?aC ?r ?cC.
	# ?aC ?p ?bC. ?bC ?q ?aC.
	# ?cC ?r ?dC. ?dC ?s ?cC.
	# }
# WHERE { 
# # ?r (rdfs:subPropertyOf*)/(^rdfs:subPropertyOf*) terms:find.
# ?r (rdfs:subPropertyOf)/(^rdfs:subPropertyOf) terms:find.
# ?a ?r ?c.
# ?c a ?cC. 
# OPTIONAL { ?a a ?aC. { ?a ?p ?b } UNION { ?b ?q ?b }. ?b a ?bC} 
# { ?c ?r ?d } UNION { ?d ?s ?c }. ?c a ?dC 
# } 

# what has been found III: too large graph
# PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>
# PREFIX terms: <http://purl.org/acoli/open-ie/>
# PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
# PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
# CONSTRUCT { 
	# ?what rdfs:label ?whatl.
	# ?what a ?whatC.
	# ?what rdfs:predicate ?r.
	# ?whoC ?r ?what.
	# ?whoC rdfs:label ?whoL.
	# ?what terms:during ?whenL.
	# ?what terms:at ?whereL.
	# ?whenC rdfs:label ?whenL.
	# ?whereC rdfs:label ?whereL.
# }
# WHERE { 
	# # ?r rdfs:subPropertyOf*/(^rdfs:subPropertyOf)* terms:find.
	# ?r rdfs:subPropertyOf/(^rdfs:subPropertyOf) terms:find.
	# [] ?r ?what.
	# ?what rdfs:label ?whatl.
	# OPTIONAL {
		# ?who ?r ?what.
		# ?who rdfs:label ?whol.
		# ?who a ?whoC.
	# }
	# OPTIONAL { 
		# { ?who ?r ?what. ?who terms:at ?where } UNION 
			# { ?what terms:at ?where }. 	
		# ?where a ?whereC.
		# ?where rdfs:label ?wherel.
	# }
	# OPTIONAL { 
		# { ?who ?r ?what. ?who terms:during ?when } UNION 
			# { ?what terms:during ?when }. 	
		# ?when a ?whenC.
		# ?when rdfs:label ?whenl.
	# }
# } 

# subclasses of "Person" and their labels
# PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>
# PREFIX tbox: <http://purl.org/acoli/open-ie/>
# PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
# PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
# CONSTRUCT { ?aC rdfs:label ?aL. ?aC rdfs:subClassOf ?bC } 
# WHERE { 
# {?a a tbox:person} UNION 
# {?a a/rdfs:subClassOf tbox:person} UNION
# {?a a/rdfs:subClassOf/rdfs:subClassOf tbox:person}.
# ?a a ?aC.
# ?aC rdfs:subClassOf ?bC.
# ?a rdfs:label ?aL.
# }

# everything roman (relativ brauchbares ergebnis auf 11*-linked.ttl, aber etwas gemogelt, da string-match)
# PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>
# PREFIX tbox: <http://purl.org/acoli/open-ie/>
# PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
# PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
# PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
# CONSTRUCT { ?aC rdfs:label ?aL. ?aC rdfs:subClassOf ?bC . ?bC rdfs:subClass ?cC. ?cC rdfs:subClass ?dC. ?dC rdfs:subClass ?eC. ?eC rdfs:subClass ?fC. ?fC rdfs:subClass ?gC. ?aC ?r ?sC. ?sC ?r3 ?tC. tbox:roman tbox:includes ?aL} 
# WHERE { 
# {?aC skos:broader/rdfs:subClass ?bC FILTER (regex(str(?aC),".*(roman|imperial).*","i")) } 
# UNION {
	# {?a a ?aC FILTER (regex(str(?a),".*(roman|imperial).*","i")). ?a rdfs:label ?aL } UNION 
	# {?a a ?aC FILTER (regex(str(?aC),".*(roman|imperial).*","i")). ?a rdfs:label ?aL } UNION
	# {?a a ?aC. ?a rdfs:label ?aL FILTER (regex(str(?aL),".*(roman|imperial).*","i")) }
	# ?a ?r ?s.
	# ?s a ?sC.
	# OPTIONAL {
		# ?a ?r2 ?t1.
		# ?t1 a ?tC.
		# ?s ?r3 ?t.
		# ?t a ?tC.
		# }
	# OPTIONAL { ?aC rdfs:subClassOf/skos:broader ?bC. 
		# OPTIONAL { ?bC rdfs:subClass/skos:broader ?cC.
			# OPTIONAL { ?cC rdfs:subClass/skos:broader ?dC.
				# OPTIONAL { ?dC rdfs:subClass/skos:broader ?eC.
					# OPTIONAL { ?eC rdfs:subClass/skos:broader ?fC.
						# OPTIONAL { ?fC rdfs:subClass/skos:broader ?gC}
						# }
					# }
				# }
			# }
		# }
	# }
# }

# experiment zu arch-features, SCHEITERT (pr�fe linking)
# PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>
# PREFIX tbox: <http://purl.org/acoli/open-ie/>
# PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
# PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
# PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
# PREFIX arch: <http://thesaurus.archeoinf.de/concept#>
# CONSTRUCT { 
	# ?f a ?fC. 
	# ?fC rdfs:subClassOf ?gC. 
	# ?gC rdfs:subClassOf ?hC. 
	# ?hC rdfs:subClassOf ?iC.
# } WHERE { 
	# ?f a ?fC. ?fC rdfs:subClassOf*/skos:broader*/skos:broaderTransitive* arch:feature.
	# OPTIONAL { ?fC rdfs:subClassOf*/skos:broader*/skos:broaderTransitive* ?gC.
		# OPTIONAL { ?gC rdfs:subClassOf*/skos:broader*/skos:broaderTransitive* ?hC.
			# OPTIONAL { ?hC rdfs:subClassOf*/skos:broader*/skos:broaderTransitive* ?iC.
				# }
			# }
		# }
# }

# temporal statements
# PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>
# PREFIX terms: <http://purl.org/acoli/open-ie/>
# PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
# PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
# CONSTRUCT { 
	# ?whenC rdfs:label ?whenL.
	# ?whenC rdfs:subClassOf ?whenC1.
# #	?whenC1 rdfs:subClassOf ?whenC2.
# #	?whenC2 rdfs:subClassOf ?whenC3.
# #	?whenC3 rdfs:subClassOf ?whenC4.
# }
# WHERE { 
	# [] terms:during ?when.
	# ?when rdfs:label ?whenL.
	# ?when a ?whenC.
	# OPTIONAL { ?whenC rdfs:subClassOf ?whenC1.
# #		OPTIONAL { ?whenC1 rdfs:subClassOf ?whenC2. 
# #			OPTIONAL { ?whenC2 rdfs:subClassOf ?whenC3. 
# #				OPTIONAL { ?whenC3 rdfs:subClassOf ?whenC4. 
# #					OPTIONAL { ?whenC4 rdfs:subClassOf ?whenC5. 
# #					}
# #				}
# #			}
# #		}
	# }
# }

# "summary" (works just fine, but don't visualize all hypernyms)
# PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>
# PREFIX terms: <http://purl.org/acoli/open-ie/>
# PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
# PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
# PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
# CONSTRUCT { 
# ?ac ?b ?cc.
# # ?ac rdfs:label ?aL.
# # ?cc rdfs:label ?cL.
# ?ac rdfs:subClassOf ?ac1.
# # ?ac1 rdfs:subClassOf ?ac2.
# ?cc rdfs:subClassOf ?cc1.
# #?cc1 rdfs:subClassOf ?cc2.
# } 
# WHERE { 
	# {
		# SELECT ?b (COUNT(?b) AS ?bweight)
		# WHERE {
			# [] ?b []
		# } GROUP BY ?b
	# }
	# FILTER (?bweight > 5)								# use this to control sparsity: minimal relation frequency
	# ?a ?b ?c.
	# ?a a ?ac.
	# ?c a ?cc.
	# ?a rdfs:label ?aL.
	# ?c rdfs:label ?cL.	
	# {
		# SELECT ?ac (COUNT(*) AS ?aweight)
		# WHERE {
			# [] a ?ac
		# } GROUP BY ?ac
	# }
	# FILTER (?aweight > 5)								# use this to control sparsity: minimal class frequency for subject
	# {
		# SELECT ?cc (COUNT(*) AS ?cweight)
		# WHERE {
			# [] a ?cc
		# } GROUP BY ?cc ?x
	# }
	# FILTER (?cweight > 5)								# use this to control sparsity: minimal class frequency for object
	# OPTIONAL { ?ac rdfs:subClass|skos:broader ?ac1.
		# OPTIONAL { ?ac1 rdfs:subClass|skos:broader ?ac2.
		# }
	# }
	# OPTIONAL { ?cc rdfs:subClass|skos:broader ?cc1.
		# OPTIONAL { ?cc1 rdfs:subClass|skos:broader ?cc2.
		# }
	# }	
# }

# "normal" summary (no color)
# PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>
# PREFIX terms: <http://purl.org/acoli/open-ie/>
# PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
# PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
# PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
# CONSTRUCT { 
# ?ac ?b ?cc.
# #?ac rdfs:label ?aL.
# #?cc rdfs:label ?cL.
# #?ac rdfs:subClassOf ?ac1.
# #?ac1 rdfs:subClassOf ?ac2.
# #?cc rdfs:subClassOf ?cc1.
# #?cc1 rdfs:subClassOf ?cc2.
# } 
# WHERE { 
	# {
		# SELECT ?b (COUNT(?b) AS ?bweight)
		# WHERE {
			# [] ?b []
		# } GROUP BY ?b
	# }
	# FILTER (?bweight > 5)								# use this to control sparsity: minimal relation frequency
	# ?a ?b ?c.
	# ?a a ?ac.
	# ?c a ?cc.
	# {
		# SELECT ?ac (COUNT(*) AS ?aweight)
		# WHERE {
			# [] a ?ac
		# } GROUP BY ?ac
	# }
	# FILTER (?aweight > 5)								# use this to control sparsity: minimal class frequency for subject
	# {
		# SELECT ?cc (COUNT(*) AS ?cweight)
		# WHERE {
			# [] a ?cc
		# } GROUP BY ?cc ?x
	# }
	# FILTER (?cweight > 5)								# use this to control sparsity: minimal class frequency for object
# #	OPTIONAL { 
# #		# { ?a a terms:person } UNION {?a a terms:place} UNION 
# #	{?a a terms:organization}. ?a rdfs:label ?aL }	# labels for properly classified named entities
# #	OPTIONAL { 
# #		#{ ?c a terms:person } UNION {?c a terms:place} UNION 
# #		{?c a terms:organization}. ?c rdfs:label ?cL }
# #	OPTIONAL {
# #		?ac skos:broader ?ac1. 							# we disambiguate wordnet hypernyms using the text itself ;)
# #		[] a ?ac1.
# #		OPTIONAL { ?ac1 skos:broader ?ac2. [] a ?ac2 }	# faster than UNION, but lossy, and still terribly slow, optimization through a domain-specifically pruned WN
# #		}
# #	OPTIONAL { 
# #		?cc skos:broader ?cc1. [] a ?cc1. 
# #		[] a ?cc1.
# #		OPTIONAL { ?cc1 skos:broader ?cc2. [] a ?cc2 }
# #		}
# }



echo 'PREFIX text: <http://purl.org/acoli/open-ie/acoli.in2rdf.Sentence2Triple/16787226#>
PREFIX terms: <http://purl.org/acoli/open-ie/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
CONSTRUCT { 
?ac ?b ?cc.
?ac terms:json_color ?acolor.
?cc terms:json_color ?ccolor.
} 
WHERE { 
	{
		SELECT ?b (COUNT(?b) AS ?bweight)
		WHERE {
			[] ?b [] FILTER (regex(str(?b), "http://purl.org/acoli/open-ie.*"))
		} GROUP BY ?b
	}
	FILTER (?bweight > 5)								# use this to control sparsity: minimal relation frequency
	?a ?b ?c.
	?a a ?ac.
	?c a ?cc.
	FILTER (?ac != ?cc)									# not properly visualized
	{
		SELECT ?ac (COUNT(*) AS ?aweight)
		WHERE {
			[] a ?ac
		} GROUP BY ?ac
	}
	FILTER (?aweight > 5)								# use this to control sparsity: minimal class frequency for subject
	{
		SELECT ?cc (COUNT(*) AS ?cweight)
		WHERE {
			[] a ?cc
		} GROUP BY ?cc ?x
	}
	FILTER (?cweight > 5)								# use this to control sparsity: minimal class frequency for object
	#
	# coloring (with manual extensions)
	OPTIONAL { 
		{ ?a a*/(rdfs:subClassOf|skos:broader|skos:broaderTransitive)* <http://thesaurus.archeoinf.de/concept#feature>.  } UNION
		{ ?a a terms:structure } UNION 
		{ ?a a terms:building } UNION
		{ ?a a terms:trench } UNION 
		{ ?a a terms:pavement } UNION
		{ ?a a terms:room } UNION
		{ ?a a terms:wall } UNION
		{ ?a a terms:tomb }
		BIND (("FF0000"^^xsd:string) AS ?acolor)		# red
	}
	OPTIONAL { 
		?c a*/(rdfs:subClassOf|skos:broader|skos:broaderTransitive)* <http://thesaurus.archeoinf.de/concept#feature>. 
		{ ?c a terms:structure } UNION 
		{ ?c a terms:building } UNION
		{ ?c a terms:trench } UNION 
		{ ?c a terms:pavement } UNION
		{ ?c a terms:room } UNION
		{ ?c a terms:wall } UNION
		{ ?c a terms:tomb }
		BIND (("FF0000"^^xsd:string) AS ?ccolor)		# red
	}
	# manual extensions: orientation terms
	OPTIONAL { 
		{ ?a a terms:site } UNION 
		{ ?a a terms:place } UNION
		{ ?a a terms:portion } UNION 
		{ ?a a terms:area } UNION 
		{ ?a a terms:west } 
		BIND (("33CC33"^^xsd:string) AS ?acolor)		# green
	}
	OPTIONAL { 
		{ ?c a terms:site } UNION 
		{ ?c a terms:place } UNION
		{ ?c a terms:portion } UNION 
		{ ?c a terms:area } UNION 
		{ ?c a terms:west } 
		BIND (("33CC33"^^xsd:string) AS ?ccolor)		# green
	}
	# chronologically relevant terms
	OPTIONAL { 
		{ ?a a terms:vicus } UNION 
		{ ?a a terms:church }
		BIND (("3366FF"^^xsd:string) AS ?acolor)		# blue
	}
	OPTIONAL { 
		{ ?c a terms:vicus } UNION 
		{ ?c a terms:church }
		BIND (("3366FF"^^xsd:string) AS ?ccolor)		# blue
	}
}

' | \
java -classpath $CLASSPATH acoli.query.InteractiveQuery $* | \
rdf2rdf -.ttl -.nt | 																	# turtle to n-3 		# TOFIX: using rdf2rdf is not portable
sed -e s/'"[^ \t"]*[#:]string[>]*'/'"'/g -e s/'[^ "]*[:#\/]'/':'/g -e s/'[<>]'//g  -e s/' '/'\t'/ -e s/' '/'\t'/ -e s/'[\t ]*\.$'/'.'/g | 		# preformat for parse_triples.py
sed -e s/'\("[^ ]* [^ ]* [^ ]* [^ ]* [^ ]*\)[^"]*"'/'\1 ..."'/g |					# shorten long labels
egrep -v '[ \t]:do[ \t]' | \
sed -e s/'\([ \t]:[^ \t]*\)_do[ \t]'/'\1\t'/g \
	-e s/':do_'/':'/g | \
perl -e 'while(<>) {
	while(m/.*"[^"\t]\t.*/) {
		s/("[^"\t]*)\t/\1 /g;
	};
	print;
}' |\
sed -e s/'\(:json_color[^"]*\)"\([^"]*\)"\([^"]*\)$'/'\1:\2\3'/g | 					# ad hoc fix for parse_triples.py
sort -u > $TMPjson;				# eliminate non-argument predicate edges
cd $TRIPLEVIS/converters;
python parse_triples.py `echo $TMPjson | sed s/'.*\/'//g`;								# TOFIX: set paths relative to installation directory in parse_triples.py
cp triples.json ../interface/;
cd -
#rm $TMPjson
