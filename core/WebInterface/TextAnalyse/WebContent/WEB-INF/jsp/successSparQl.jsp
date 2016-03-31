<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="decorator"
	uri="http://www.opensymphony.com/sitemesh/decorator"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"  
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Text Analytics | Text Analyse</title>

<spring:url value="/resources/style.css" var="style" />
<link href="${style}" rel="stylesheet" />
<spring:url value="/resources/js/cufon-yui.js" var="cufon-yui" />
<script src="${cufon-yui}"></script>
<spring:url value="/resources/js/georgia.js" var="georgia" />
<script src="${georgia}"></script>
<spring:url value="/resources/js/cuf_run.js" var="cuf_run" />
<script src="${cuf_run}"></script>

<style>
.error {
	color: red;
	font-weight: bolder;
}

.commonerrorblock {
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
}

form{
    width: 100%;
}

table {
    width: 100%;
}

tr {
    width: 100%;
}

td {
    width: 100%;
}
textarea {
    width: 600px;
    height: 300px;
    background-color : #C2CFCB;
    overflow-y: scroll;
}
</style>


</head>
<body>
	<div class="main">
		<div class="header">
			<div class="header_resize">
				<div class="logo">
					<h1>
						<a href="welcome.html">Text Analytics<br /> <small>
								on News Messages</small></a>
					</h1>
				</div>
				<div class="menu_nav">
					<ul>
						<li><a href="welcome.html">Home</a></li>
						<li class="active"><a href="textAnalyse.html">Text
								Analyse</a></li>
						<li><a href="login.html">Verwaltung</a></li>
						<li><a href="about.html">About Us</a></li>
						<li><a href="contact.html">Contact Us</a></li>
					</ul>
					<div class="clr"></div>
				</div>
				<div class="clr"></div>
			</div>
		</div>
		<div class="content">
			<div class="content_resize">
				<div class="mainbar">

					<div class="article">
						<h2>
							<span>SparQL Result</span>
						</h2>
					</div>
					<div id="wrapperanalyse">
						<div id="welcome-analyse" class="container">
							<h4>
								<span>SparQL Turtle</span>
							</h4>
							RDF Turtle  file  has been created: <br />
							${ta.rdftriplefile}
				
							<!--<a href="${ta.visualpath}" class="button">Start Visualization</a>-->
							<a href="http://127.0.0.1:8000" class="button">Start Visualization</a>
						</div>
					</div>
				</div>

				<div class="sidebar">
					<div class="gadget">
						<h2 class="star">
							<span>Sidebar</span> Menu
						</h2>
						<ul class="sb_menu">
							<li><a href="#">Home</a></li>
							<li><a href="#">TemplateInfo</a></li>
							<li><a href="#">Style Demo</a></li>
							<li><a href="#">Blog</a></li>
							<li><a href="#">Archives</a></li>
							<li><a href="#">Website Templates</a></li>
						</ul>
					</div>
					<div class="gadget">
						<h2 class="star">
							<span>Notwendige Webseite</span>
						</h2>
						<ul class="ex_menu">
							<li><a href="https://www.informatik.uni-frankfurt.de/">Institut
									fÃ¼r Informatik - Goethe-UniversitÃ¤t </a><br /> Institut Web in
								Frankfurt Am Mein</li>
							<li><a href="#">Fachabteilung </a><br /> .....</li>
							<li><a href="http://www.w3schools.com/html/">w3schools.com</a><br />
								Sehr gut Tutorium-Webseite</li>
							<li><a href="https://www.wikipedia.org/">wikipedia</a><br />
								Linking Knowledge Base</li>
							<li><a href="#">test</a><br /> Website Builder Software</li>

						</ul>
					</div>
				</div>
				<div class="clr"></div>
			</div>
		</div>
		<div class="fbg">
			<div class="fbg_resize">
				<div class="col c1">
					<h2>Image Analyse</h2>
					<a href="#"><img src="resources/images/pix1.jpg" width="56"
						height="56" alt="" /></a> <a href="#"><img
						src="resources/images/pix2.jpg" width="56" height="56" alt="" /></a>
					<a href="#"><img src="resources/images/pix3.jpg" width="56"
						height="56" alt="" /></a> <a href="#"><img
						src="resources/images/pix4.jpg" width="56" height="56" alt="" /></a>
					<a href="#"><img src="resources/images/pix5.jpg" width="56"
						height="56" alt="" /></a> <a href="#"><img
						src="resources/images/pix6.jpg" width="56" height="56" alt="" /></a>
				</div>
				<div class="col c2">
					<h2>Analyse Tool</h2>
					<p>
						Analysepipeline fÃ¼r Nachrichtentexte<br /> ï· -Englisch ï·
						-Skalierbar, unter Verwendung bestehender Softwarekomponenten ï·
						-Einheitliche, JAVA-basierte Architektur ï· -Entsprechend
						vorgegebenen Schnittstellenformaten ï· -Einbindung eines
						Datenbank-Backends ï· -Entwicklung einer Web-GUI
					</p>
				</div>
				<div class="col c3">
					<h2>About</h2>
					<img src="resources/images/white.jpg" width="56" height="56" alt="" />
					<p>
						Text Analyse mit JSF. <a href="#">Learn more...</a>
					</p>
				</div>
				<div class="clr"></div>
			</div>
		</div>
		<div class="footer">
			<div class="footer_resize">
				<p class="lf">&copy; Copyright MyWebSite. Designed by Andy and
					Kathrin</p>
				<div class="clr"></div>
			</div>
		</div>
	</div>
</body>
</html>
