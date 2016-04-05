#!/usr/bin/python

# Samuel Ronnqvist - sronnqvi@abo.fi 

import sys
import json
import re
import collections


def prefix_replace(i):
    '''replace the prefixes; Kathrin und Andy'''
    pattern = re.compile("<http://purl.org/acoli/open-ie/in2rdf.Sentence2Triple/"+"[0-9]")
    if pattern.match(i) != None:   
    	i = "data:"+i.split("#")[1]
    i = i.replace("<http://purl.org/acoli/open-ie/in2rdf.Sentence2Triple/1070020619#", "data:")
    i = i.replace("<http://purl.org/acoli/open-ie/in2rdf.Sentence2Triple/1363910379#", "data:")
    i = i.replace("<http://purl.org/acoli/open-ie/", ":")
    i = i.replace("<http://www.w3.org/2002/07/owl#", "owl")
    i = i.replace("<http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf")
    i = i.replace("<http://www.w3.org/2000/01/rdf-schema#", "rdfs")
    i = i.replace("<http://www.w3.org/2004/02/skos/core#", "skos")
    i = i.replace("<http://www.w3.org/2001/XMLSchema#", "xsd")
    i = i.replace(">>.", "")
    i = i.replace(">","")
    i = i.replace("<", "")
    return i


file = open(sys.argv[1])
#file = open("zeit.ttl")



# Default color of a node.
DEFAULT_COLOR = '792DC5'



# niko. collect all color names.
# color names are nodes that are specified by a 
# XYZ :json_color FFEEDD. 
# relation.
with open(sys.argv[1]) as f:
      lines = f.readlines()

node_to_color = {}

for l in lines:
    if ':json_color' in l:
        # Remove new line and final period.
        l = l.strip()[:-1]
        # Collect the nodes and their associated colors.
        node = l.split('\t')[0]
        color = l.split('\t')[2]
        #print node
        #print color
        # Add to dictionary.
        node_to_color.update({node:color})
        
#print 'Node to color: ' 
#print node_to_color



# Literals
entities = collections.defaultdict(lambda: 0)
# Non-literals
concepts = collections.defaultdict(lambda: 0)

links = collections.defaultdict(lambda: [])

i = 0
while True:
    line = file.readline()
    # Check line
    if line == '':
        break
    if line[0] in ['\n', '#', '@']:
        continue

    # Parse line & check relation
    line = line.strip()
    src, rel, trg = line.split('\t')
    src = prefix_replace(src)
    rel = prefix_replace(rel)
    trg = prefix_replace(trg)

    # src = src.split('_')[-1]
    # CC: out
	#if rel == 'rdfs:label':
    #    continue
    #if "\"" in trg:
    #    trg = trg.split("\"")[1]
    #if "data:" in trg:
    #    trg = trg.split("_")[-1]
    trg = trg.strip('.')

    #if src == trg.strip('.').strip(':') or len(src) < 2 or len(trg) < 2:
    #    continue

	# CC: NOPE, we control this using SPARQL
    #if re.match("_?:n\d+", src) or re.match("_?:n\d+", trg):
    #    continue

    # CC: OUT
    #filter_words = ['we', 'it', 'us', 'cm']
	## Leave out non-literals for now
    #if ':' in trg:# or src in filter_words or trg in filter_words:
    #    continue

    #print i, src, rel, trg

    entities[src] += 1

	# CC: out
    #if trg[0] == ':':
    #    # Non-literals are specific to the their related literals, not general as it would create many connections across the network
    #    concepts[(src, trg)] += 1
    #    links[(src, (src, trg))].append(rel)
    #else:
    entities[trg] += 1
    links[(src, trg)].append(rel)

    i += 1

## Filter out infrequent words		# CC: nope, we'll do that with SPARQL
#for e in entities.items():
#    if e[1] <= 1:
#        del entities[e[0]]

nodes = list(entities) + list(concepts)

noncolor_nodes = []

# niko.
# remove color nodes.
for a_node in nodes:
  #print "-> " + a_node
  if a_node not in node_to_color.values():
    noncolor_nodes.append(a_node)
   
nodes = noncolor_nodes
node_ids = dict(zip(nodes, range(len(nodes))))
  
#print 'NODES:'
#print nodes
#print 'IDs:'
#print node_ids


# Compile JSON
json_links = [{
  'source': node_ids[link[0][0]], 
  'target': node_ids[link[0][1]], 
  'rel': reduce(lambda a,b: a+', '+b, link[1])} 
  for link in links.items() if link[0][1] in nodes and
link[0][0] in nodes] # if entity_cnt[link[0][0]] > 1 and entity_cnt[link[0][1]] > 1]

json_nodes = [{'name': node[0] if type(node[0]) is not tuple else node[0][1]} for node in sorted(node_ids.items(), key=lambda x: x[1])]


# Add color attribute to nodes.
for node in json_nodes:
    node_name = node['name']
    #print node_name
    # get color for that node name.
    if node_name in node_to_color:
        color_for_that_node = node_to_color[node_name]
        node['group'] = color_for_that_node
        #print color_for_that_node
    else:
        print 'No color defined for node: ' + node_name
        node['group'] = DEFAULT_COLOR # default color


#open("triplesJson/triples.json", "w").write(json.dumps({'nodes': json_nodes, 'links': json_links}))
open(sys.argv[2], "w").write(json.dumps({'nodes': json_nodes, 'links': json_links}))


print 'JSON color output successfully generated.'


# Print word frequencies
#for e, c in sorted(entities.items(), key=lambda x:x[1]):
#    print e, c


