from pymantic import sparql
import sys


def update(filepath):
    server = sparql.SPARQLServer('http://localhost:9999/blazegraph/sparql')
    server.update('load <file://%s>' %filepath)

    
if __name__ == '__main__':
    update(sys.argv[1])

