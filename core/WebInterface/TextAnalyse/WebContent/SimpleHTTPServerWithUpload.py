#!/usr/bin/env python

"""Simple HTTP Server With Upload.

This module builds on BaseHTTPServer by implementing the standard GET
and HEAD requests in a fairly straightforward manner.

"""


__version__ = "0.1"
__all__ = ["SimpleHTTPRequestHandler"]
__author__ = "bones7456"
__home_page__ = "http://li2z.cn/"

from pymantic import sparql
import subprocess
import sys
import re
import glob
import shutil
import os
import posixpath
import BaseHTTPServer
import urllib
import cgi
import shutil
import mimetypes
import re
from shutil import copyfile
try:
    from cStringIO import StringIO
except ImportError:
    from StringIO import StringIO


class SimpleHTTPRequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):

    """Simple HTTP request handler with GET/HEAD/POST commands.

    This serves files from the current directory and any of its
    subdirectories.  The MIME type for files is determined by
    calling the .guess_type() method. And can reveive file uploaded
    by client.

    The GET/HEAD/POST requests are identical except that the HEAD
    request omits the actual contents of the file.

    """

    server_version = "SimpleHTTPWithUpload/" + __version__


    def do_GET(self):
        """Serve a GET request."""
        f = self.send_head()
        if f:
            self.copyfile(f, self.wfile)
            f.close()

    def do_HEAD(self):
        """Serve a HEAD request."""
        f = self.send_head()
        if f:
            f.close()

    def do_POST(self):
        """Serve a POST request."""
        global localhost
        r, info = self.deal_post_data()
        print r, info, "by: ", self.client_address
        f = StringIO()
        f.write('<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">')
        if info[:4] == "File":
            f.write("<html>\n<title>Upload Result Page</title>\n")
            f.write("<body>\n<h2>Upload Result Page</h2>\n")
            f.write("<hr>\n")
            if r:
                f.write("<strong>Success:</strong>")
            else:
                f.write("<strong>Failed:</strong>")
            f.write(info)
            print("Localhost: ", localhost)
            

            f.write("<br><a href=\"%s\">back</a>" % localhost)
            f.write("</small></body>\n</html>\n")
            length = f.tell()
            f.seek(0)
            self.send_response(200)
            #self.send_header("Content-type", "text/html;  charset=UTF-8\n\n")
            self.send_header("Content-Length", str(length))
            self.end_headers()
            if f:
                self.copyfile(f, self.wfile)
                f.close()
        if info[:8] == "Pipeline":            
            language = info[9:11]
            f.write("<html>\n<title>Pipeline Result Page</title>\n")
            f.write("<body>\n<h2>Pipeline Result Page</h2>\n")
            f.write("<hr>\n")
            if r:
                f.write("<strong>Success:</strong>")
                f.write(info)   
                f.write("<br><a href=\"%s\">back</a>" % localhost)
                f.write("<br><a href=\"http://localhost:8082\">Show Nquad Files</a>")
            else:
                f.write("<strong>Failed:</strong>")
                f.write(info)
                f.write("<br><a href=\"%s\">back</a>" % localhost)
            
            f.write("</small></body>\n</html>\n")
            length = f.tell()
            f.seek(0)
            self.send_response(200)
            #self.send_header("Content-type", "text/html;  charset=UTF-8\n\n")
            self.send_header("Content-Length", str(length))
            self.end_headers()
            if f:
                self.copyfile(f, self.wfile)
                f.close()
        if info[:10] == "Blazegraph":
            f.write("<html>\n<title>Blazegraph Update Result Page</title>\n")
            f.write("<body>\n<h2>Blazegraph Update Result Page</h2>\n")
            f.write("<hr>\n")
            if r:
                f.write("<strong>Success:</strong>")
            else:
                f.write("<strong>Failed:</strong>")
            f.write(info)
            print("Localhost: ", localhost)
            

            f.write("<br><a href=\"%s\">back</a>" % localhost)
            f.write("</small></body>\n</html>\n")
            length = f.tell()
            f.seek(0)
            self.send_response(200)
            #self.send_header("Content-type", "text/html;  charset=UTF-8\n\n")
            self.send_header("Content-Length", str(length))
            self.end_headers()
            if f:
                self.copyfile(f, self.wfile)
                f.close()        

        
    def deal_post_data(self):
        global thisDir
        boundary = self.headers.plisttext.split("=")[1]
        remainbytes = int(self.headers['content-length'])
        line = self.rfile.readline()
        remainbytes -= len(line)
        if not boundary in line:
            return (False, "Content NOT begin with boundary")
	line = self.rfile.readline()
        print("Line0: ", line)
        remainbytes -= len(line)
        fn = re.findall(r'Content-Disposition.*name="choice-language-.*"', line)
        fnpipeline = re.findall(r'Content-Disposition.*name="pipeline-language-.*"', line)
        fnblazegraph = re.findall(r'Content-Disposition.*name="blazegraph.*"', line)
        global savepath
        if not fn:
            if not fnpipeline:
                    if not fnblazegraph:
                        return (False, "Can't upload to blazegraph...")
                    else:
                        print("Blazegraph upload...")
                        fn = re.findall(r'Content-Disposition.*name="blazegraph"; filename="(.*)"', line)

                        if not fn:
                            return (False, "Can't find out file name...")
                        path = self.translate_path(self.path)
                        fn = os.path.join(path, fn[0])
                        line = self.rfile.readline()
                        remainbytes -= len(line)
                        line = self.rfile.readline()
                        remainbytes -= len(line)
                        print("Line3: ",line)
                        newpath = os.getcwd()
                    #global savepath
                        newpath = thisDir+"/nquads"
                        print(newpath)
                    #copyfile(fn, newpath+fn.split(newpath)[1])
                        fn = newpath+fn.split(path)[1]
                        if fn.split(".")[::-1][0] not in ['ttl','n3','nq']:
                            return (False, "File should be .nq, .ttl, .n3 Format!")
                            
                        try:
                            out = open(fn, 'wb')
                        except IOError:
                            return (False, "Can't create file to write, do you have permission to write?")
                            
                        preline = self.rfile.readline()
                        remainbytes -= len(preline)
                        while remainbytes > 0:
                            line = self.rfile.readline()
                            remainbytes -= len(line)
                            if boundary in line:
                                preline = preline[0:-1]
                                if preline.endswith('\r'):
                                    preline = preline[0:-1]
                                out.write(preline)
                                out.close()

	                       # try:
       	        	       #     subprocess.check_call(['sh', 'update.sh', thisDir, fn])
                               #     return (True, "Blazegraph successfully updated with data of '%s' !" % fn)
                               # except subprocess.CalledProcessError:
	                       #     return(False, "Blazegraph could not be updated")
                                os.chdir(thisDir)
				global blazegraph
                                server = sparql.SPARQLServer(urlBlazegraph)
                                # Loading data to Blazegraph
                                server.update('load <file://%s>' %fn)
                                return (True, "Blazegraph successfully updated with data of '%s' !" % fn)
                            else:
                                out.write(preline)
                                preline = line
                        return (False, "Unexpect Ends of data.")

                        
            else:
                language = line.split("pipeline-language-")[1][:2]

        else:
            language = line.split("choice-language-")[1][:2]
            print("Language chosen:",language)
            savepath = savepath+language+"/filePool/"
            print("savepath: ", savepath)
	line = self.rfile.readline()
        print("Line1: ", line)
        remainbytes -= len(line)

        line = self.rfile.readline()
        print("Line2: ", line)
        remainbytes -= len(line)

        line = self.rfile.readline()
        print("Line3: ", line)
        remainbytes -= len(line)

        if fn:
            line = self.rfile.readline()
            print("Line4: ", line)
            remainbytes -= len(line)
            fn = re.findall(r'Content-Disposition.*name="file"; filename="(.*)"', line)
            if not fn:
                return (False, "Can't find out file name...")
            path = self.translate_path(self.path)
            fn = os.path.join(path, fn[0])
            line = self.rfile.readline()
            remainbytes -= len(line)
            line = self.rfile.readline()
            remainbytes -= len(line)
            print("Line3: ",line)
	    newpath = os.getcwd()
        #global savepath
            newpath = savepath
            savepath = savepath.split("en")[0].split("de")[0]#remove language (to use global variable savepath "from the beginning")
            print("Savepath restored: ",savepath)
            print(newpath)
        #copyfile(fn, newpath+fn.split(newpath)[1])
            fn = newpath+fn.split(path)[1]
            if fn.split(".")[::-1][0] not in ['html','pdf']:
                return (False, "File should be .html or .pdf Format!")
            try:
                out = open(fn, 'wb')
            except IOError:
                return (False, "Can't create file to write, do you have permission to write?")
                
            preline = self.rfile.readline()
            remainbytes -= len(preline)
            while remainbytes > 0:
                line = self.rfile.readline()
                remainbytes -= len(line)
                if boundary in line:
                    preline = preline[0:-1]
                    if preline.endswith('\r'):
                        preline = preline[0:-1]
                    out.write(preline)
                    out.close()
                    return (True, "File '%s' upload success!" % fn)
                else:
                    out.write(preline)
                    preline = line
            return (False, "Unexpect Ends of data.")
        if fnpipeline:
            print("Language pipeline: ", language)
            global locationPip
            print("Location Pipeline: ", locationPip)
            try:
       	        subprocess.check_call(['sh', 'runpipeline.sh', locationPip, language, thisDir, thisDir+"nquads"])
                
                for filename in glob.glob(os.path.join(locationPip+"/ResourcesPipeline/localFiles/outputPipeline/"+language+"/nquads", '*.nq')):
                    shutil.copy(filename, thisDir+"/nquads/")
                    print "Copied %s" %filename


                return(True, "Pipeline %s successfully executed" %language)
            except subprocess.CalledProcessError:
	        return(False, "Pipeline could not be executed")

	
            
                                
            


    def send_head(self):
        """Common code for GET and HEAD commands.

        This sends the response code and MIME headers.

        Return value is either a file object (which has to be copied
        to the outputfile by the caller unless the command was HEAD,
        and must be closed by the caller under all circumstances), or
        None, in which case the caller has nothing further to do.

        """
        path = self.translate_path(self.path)
        f = None
        if os.path.isdir(path):
            if not self.path.endswith('/'):
                # redirect browser - doing basically what apache does
                self.send_response(301)
                self.send_header("Location", self.path + "/")
                self.end_headers()
                return None
            for index in "indexUploadFile.html", "indexUploadFile.htm":
                index = os.path.join(path, index)
                if os.path.exists(index):
                    path = index
                    break
            else:
                return self.list_directory(path)
        ctype = self.guess_type(path)
        try:
            # Always read in binary mode. Opening files in text mode may cause
            # newline translations, making the actual size of the content
            # transmitted *less* than the content-length!
            f = open(path, 'rb')
        except IOError:
            self.send_error(404, "File not found")
            return None
        self.send_response(200)
        self.send_header("Content-type", ctype)
        fs = os.fstat(f.fileno())
        self.send_header("Content-Length", str(fs[6]))
        self.send_header("Last-Modified", self.date_time_string(fs.st_mtime))
        self.end_headers()
        return f

    def list_directory(self, path):
        """Helper to produce a directory listing (absent index.html).

        Return value is either a file object, or None (indicating an
        error).  In either case, the headers are sent, making the
        interface the same as for send_head().

        """
        try:
            list = os.listdir(path)
        except os.error:
            self.send_error(404, "No permission to list directory")
            return None
        list.sort(key=lambda a: a.lower())
        f = StringIO()
        displaypath = cgi.escape(urllib.unquote(self.path))
        #f.write('<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">')
        #f.write("<html>\n<title>Directory listing for %s</title>\n" % displaypath)

        
	f.write("<hr>\n")
        f.write("<form ENCTYPE=\"multipart/form-data\" method=\"post\">")

	f.write("<input type=\"radio\" name=\"choice-language-de\" id=\"choice-language-de\">")
	f.write("<label for=\"choice-language-de\">German</label>")
	f.write("<input type=\"radio\" name=\"choice-language-en\" id=\"choice-language-en\">")
	f.write("<label for=\"choice-language-en\">English</label>")

        f.write("<input class=\"reveal-if-active\"  name=\"file\" type=\"file\"/><br>")
        f.write("<input class=\"reveal-if-active\"  type=\"submit\" value=\"upload\"/></form>\n")
        #f.write("<hr>\n<ul>\n")
        f.write("<hr>\n")

        f.write("<hr>\n")
        f.write("<form ENCTYPE=\"multipart/form-data\" method=\"post\">")

	f.write("<input type=\"radio\" name=\"pipeline-language-de\" id=\"pipeline-language-de\">")
	f.write("<label for=\"pipeline-language-de\">German</label>")
	f.write("<input type=\"radio\" name=\"pipeline-language-en\" id=\"pipeline-language-en\">")
	f.write("<label for=\"pipeline-language-en\">English</label>")

        f.write("<input class=\"reveal-if-active\"  type=\"submit\" value=\"Start Pipeline\"/></form>\n")
        #f.write("<hr>\n<ul>\n")
        f.write("<hr>\n")


        f.write("<hr>\n")
        f.write("<form ENCTYPE=\"multipart/form-data\" method=\"post\">")
        f.write("<input  name=\"blazegraph\" type=\"file\"/><br>")
        f.write("<input  type=\"submit\" value=\"Upload data to Blazegraph\"/></form>\n")
        #f.write("<hr>\n<ul>\n")
        f.write("<hr>\n")
        



 #       for name in list:
 #           fullname = os.path.join(path, name)
 #           displayname = linkname = name
 #           # Append / for directories or @ for symbolic links
 #           if os.path.isdir(fullname):
 #               displayname = name + "/"
 #               linkname = name + "/"
 #           if os.path.islink(fullname):
 #               displayname = name + "@"
 #               # Note: a link to a directory displays with @ and links with /
 #           f.write('<li><a href="%s">%s</a>\n'
 #                   % (urllib.quote(linkname), cgi.escape(displayname)))
 #       f.write("</ul>\n<hr>\n")
    
       # footerfile = open("footer.txt", "r")
#	for line in footerfile:
#            f.write(line)

        f.write("</body>\n</html>\n")
        length = f.tell()
        f.seek(0)
        self.send_response(200)
        self.send_header("Content-type", "text/html")
    

        self.send_header("Content-Length", str(length))
        self.end_headers()
        return f

    def translate_path(self, path):
        """Translate a /-separated PATH to the local filename syntax.

        Components that mean special things to the local file system
        (e.g. drive or directory names) are ignored.  (XXX They should
        probably be diagnosed.)

        """
        # abandon query parameters
        path = path.split('?',1)[0]
        path = path.split('#',1)[0]
        path = posixpath.normpath(urllib.unquote(path))
        words = path.split('/')
        words = filter(None, words)
        path = os.getcwd()
        for word in words:
            drive, word = os.path.splitdrive(word)
            head, word = os.path.split(word)
            if word in (os.curdir, os.pardir): continue
            path = os.path.join(path, word)
        return path

    def copyfile(self, source, outputfile):
        """Copy all data between two file objects.

        The SOURCE argument is a file object open for reading
        (or anything with a read() method) and the DESTINATION
        argument is a file object open for writing (or
        anything with a write() method).

        The only reason for overriding this would be to change
        the block size or perhaps to replace newlines by CRLF
        -- note however that this the default server uses this
        to copy binary data as well.

        """
        shutil.copyfileobj(source, outputfile)

    def guess_type(self, path):
        """Guess the type of a file.

        Argument is a PATH (a filename).

        Return value is a string of the form type/subtype,
        usable for a MIME Content-type header.

        The default implementation looks the file's extension
        up in the table self.extensions_map, using application/octet-stream
        as a default; however it would be permissible (if
        slow) to look inside the data to make a better guess.

        """

        base, ext = posixpath.splitext(path)
        if ext in self.extensions_map:
            return self.extensions_map[ext]
        ext = ext.lower()
        if ext in self.extensions_map:
            return self.extensions_map[ext]
        else:
            return self.extensions_map['']

    if not mimetypes.inited:
        mimetypes.init() # try to read system mime.types
    extensions_map = mimetypes.types_map.copy()
    extensions_map.update({
        '': 'application/octet-stream', # Default
        '.py': 'text/plain',
        '.c': 'text/plain',
        '.h': 'text/plain',
        })


def test(HandlerClass = SimpleHTTPRequestHandler,
         ServerClass = BaseHTTPServer.HTTPServer):
    BaseHTTPServer.test(HandlerClass, ServerClass)
    global savepath
    global localhost
    print savepath


    
if __name__ == '__main__':
    global savepath
    global localhost
    global thisDir
    global locationPip
    global urlBlazegraph
    localhost = "http://localhost:"+sys.argv[1] 
    savepath = sys.argv[2]
    thisDir = sys.argv[3]
    locationPip = sys.argv[4]
    urlBlazegraph = sys.argv[5]#'http://localhost:9999/blazegraph/sparql'
    test()
