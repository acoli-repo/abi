#!/bin/sh

locationProject=$1
location_parse="visualisation/converters/parse_triples.py"
location_input="visualisation/converters/rdftriples.ttl"
location_output="visualisation/interface/triples.json"

echo $locationProject$location_parse $locationProject$location_input $locationProject$location_output

python $locationProject$location_parse $locationProject$location_input $locationProject$location_output
