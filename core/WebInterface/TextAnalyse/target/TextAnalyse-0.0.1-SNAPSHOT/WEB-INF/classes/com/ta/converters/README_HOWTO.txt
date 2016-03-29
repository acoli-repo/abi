# 1. Analyze the PDF file (http://corpora.acoli.informatik.uni-frankfurt.de/tmws/)

# 2. Query your .ttl file (http://corpora.acoli.informatik.uni-frankfurt.de:3030/control-panel.tpl) 
#    and
# 3. Export the query result in JSON (!) format and store it on your local machine.
# 4. Place your Fuseki JSON query result file into *this* directory.

# 5. Convert Fuseki JSON query result to .ttl-like file:
python fuseki_to_ttl.py query.json > query.ttl

# 6. Convert query result in .ttl-like format to JSON appropriate for the D3 visualization:
python parse_triples.py query.ttl

# 7. Open triplevis/interface/index.html with Firefox and be patient. 
    (The visualization is slow for large number of nodes/edges ;-)) / other Browsers require SimpleHTTPServer.
    
    
    
    
    
# Note:    
# ... You can skip step 5. if your query result to be visualized is already in .ttl format.