/* Samuel Ronnqvist - sronnqvi@abo.fi */


var width = window.innerWidth-4,
    height = window.innerHeight-4;

function color(h){return Color.hsl(h,0.7,0.4)}

function node_focus(cnode){
    // Highlight node links
    var retval;
    var do_focus = function(obj){
        obj.transition().delay(50)
        .style('opacity', function(l) {
            if(cnode == l.source){
                retval = 1.0;
            } else if(cnode == l.target){
                retval = 1.0;
            } else {
                retval = 0.2;
            }
            return retval;
        })
        .style('stroke-width', function(l) {
            if(cnode == l.source){
                retval = 2;
            } else if(cnode == l.target){
                retval = 2;
            } else {
                retval = 1.2;
            }
            return retval;
        })
        .duration(200);
    }
    do_focus(link);
    do_focus(linktext);
}

function unfocus(){
    if(mouseDown) return;

    var do_unfocus = function(obj){
        obj.transition()
            .style('opacity', function(l) {
                return 0.2;
            })
            .style('stroke-width', function(l) {
                return 1.2;
            })
            .duration(200);
    }
    do_unfocus(link);
    do_unfocus(linktext);
}

var force = d3.layout.force()
    .charge(-1250)
    .linkDistance(function(d,i) {
        if(d.target.name[0] == ':')
            return 130;
        else
            return 500;
    })
    .size([width, height])
    .gravity(0.01)
    .friction(0.9)
    .linkStrength(function(d,i) {
        if(d.target.name[0] == ':')
            return 0.9;
        else
            return 0.8;
    });

var mouseDown = 0;
document.body.onmousedown = function() {
    mouseDown = 1;
}

document.body.onmouseup = function() {
    mouseDown = 0;
}

var nodes, links, node, link, glinks, gnodes, linktext;

var zoomer = d3.behavior.zoom().scaleExtent([-8, 8]).on("zoom", zoom);

var svg = d3.select("body")
    .append("svg")
    .attr("id", "svgRoot")
    .attr("width", width)
    .attr("height", height)
    .attr("xmlns", "http://www.w3.org/2000/svg")
    .attr("version", 1.1)
    .append("g")
    .call(zoomer)
    .append("g")
    .attr("id", "allContent");

// Def. arrow end marker
svg.append("svg:defs").selectAll("marker")
    .data(["end"])
    .enter().append("svg:marker")
    .attr("id", String)
    .attr("viewBox", "0 -5 10 10")
    .attr("refX", 18)
    .attr("refY", 0)
    .attr("markerWidth", 6)
    .attr("markerHeight", 6)
    .attr("orient", "auto")
    .attr("fill", "white")
    .attr("opacity", 0.6)
    .append("svg:path")
    .attr("d", "M0,-5L10,0L0,5");

// Zoom overlay
svg.append("rect")
    .attr("class", "overlay")
    .attr("width", width*8)
    .attr("height", height*8)
    .attr("x",-4*width)
    .attr("y",-4*height)
    .attr("stroke","#aaa")
    .attr("fill", "none");

function zoom() {
    svg.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
}

nodes = [];
links = [];

var filename = location.search.split('file=')[1];
if(filename == undefined){
    filename = "triples.json"
} else {
    filename = filename.split('/')[0]
}

var graph = d3.json(filename, function(error, graph) {
    // Initialize network visualization
    nodes = graph.nodes;
    links = graph.links;

    force
        .nodes(graph.nodes)
        .links(graph.links)
        .start();

    glinks = svg.selectAll(".glink")
        .data(graph.links)
        .enter().append("g")
        .classed("glink", true);

    link = glinks.append("path")
        .attr("class", "link")
        .attr("id", function(d,i){
            return "linkId_" + i;
        })
        .attr("marker-end", "url(#end)")
        .style("stroke", "indigo")
        .style("fill", "none")
        .style("opacity", 0.2);

    linktext = glinks.append("text")
        .classed("linktext", true)
        .attr("font-size", "7pt")
        .attr("text-anchor", "start")
        .attr("dx", 100)
        .attr("dy", -3)
        .append("textPath")
        .attr("xlink:href", function(d,i){
            return "#linkId_" + i;
        })
        .text(function(d,i){ return d.rel; });

    gnodes = svg.selectAll(".gnode")
        .data(graph.nodes)
        .enter()
        .append("g")
        .classed("gnode", true)
        .attr("id", function(d){ return ("node_"+d.name) })
        .on("mouseenter", node_focus)
        .on("mouseleave", unfocus)
        .call(force.drag);

    gnodes.append("circle")
        .attr("class", "node")
        .attr("r", 9)
        .style("opacity", 0.5)
        .style("stroke", "white")
	// Fill the nodes as indicated by the "group" attribute in json.
	// substring removes the first colon ':'
	.style("fill", function(d){
	    return "#" +d.group.substring(1);
	  
	// Access name of the node directly.
	//    if(d.name == ':year')
	//      return "#4444dd";
        
	// Disable the complete node with an invalid color code.
	    //else if(d.group == '6')
	    //    return "00FF33";
	//    else
        //        return "#D80000";
	})
        .style("stroke-width", "2px");

    gnodes.append("text")
        .attr("text-anchor", "middle")
        .attr("dy",".35em")
        .text(function(d) { return d.name; })
        .style("fill", function(d,i){
            if(d.name[0] == ':')
                return "#4444aa";
            else
                return "#6666ff";
        });

    var drag = force.drag()
        .on("dragstart", dragstarted)
        .on("drag", dragged)
        .on("dragend", dragended);

    function dragstarted(d) {
        d3.event.sourceEvent.stopPropagation();
        //d.fixed = true;
        d3.select(this).classed("dragging", true);
    }

    function dragged(d) {
        d3.select(this).attr("cx", d.x = d3.event.x).attr("cy", d.y = d3.event.y);
    }
    function dragended(d) {
        d3.select(this).classed("dragging", false);
    }

    force.on("tick", function() {
        link.attr("d", function(d) {
            var dx = d.target.x - d.source.x,
                dy = d.target.y - d.source.y,
                dr = Math.sqrt(dx * dx + dy * dy)*3;//*(0.7+Math.random()*0.6);
            return "M" + d.source.x + "," + d.source.y + "A" + dr + ","
                + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
        });
        // link label
        linktext.attr("transform", function(d) {
            return "translate(" + (d.source.x + d.target.x) / 2 + ","
            + (d.source.y + d.target.y) / 2 + ")"; });

        gnodes.attr("transform", function(d) {
            return 'translate(' + [d.x, d.y] + ')';
        });
    });
});

/*
setTimeout(function(){
    var header = d3.selectAll(".header");
    header.transition().duration(2000).style("opacity", 0);
    header.transition().style("display", "none").delay(2000);
}, 6000);
*/
