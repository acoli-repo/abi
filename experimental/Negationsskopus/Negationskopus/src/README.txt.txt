############################################################# 
README für die Implementierung zur Erfassung des Negationsskopus

Entwickelt mit 
APACHE JENA 2.13, neueste Version unter: https://jena.apache.org/
Jython 2.7, neueste Version unter: http://www.jython.org/
############################################################# 

Das Programm wird in der Shell/Kommandozeile gestartet.
Für den Aufruf können bzw. müssen bestimmte Parameter angegeben werden:

-conll  	: 	Name der Datei im CoNLL Format (muss im Hauptverzeichnis liegen)
-rdf		:  	Name der Datei im RDF Format (muss im Hauptverzeichnis liegen)
-out		:	Pfad zur Ausgabe der erweiterten RDF-Datei und des Outputs in Human Readable Form (ggf. mit Prefix der Datei)
-format		: 	Format der RDF Datei, muss von APACHE JENA verarbeitbar sein (Default: TURTLE)
-neg		: 	Position der Negation für unterschiedliche Formate (Default: 10)
-wd		: 	Setzt das Arbeitsverzeichnis



Ein beispielhafter Aufruf kann so aussehen:

-conll wsj_1105.txt.csv -rdf wsj_1105.txt.ttl -wd C:\Users\No\Desktop -neg 10 -format TURTLE -out C:\Users\No\Desktop\

#############################################################
Copyright (C) 2015  Norman Seeliger
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.