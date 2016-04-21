<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ taglib prefix="decorator"
	uri="http://www.opensymphony.com/sitemesh/decorator"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"  
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Text Analytics | Contact</title>
<spring:url value="/resources/style.css" var="style" />
<link href="${style}" rel="stylesheet" />
<spring:url value="/resources/js/cufon-yui.js" var="cufon-yui" />
<script src="${cufon-yui}"></script>
<spring:url value="/resources/js/georgia.js" var="georgia" />
<script src="${georgia}"></script>
<spring:url value="/resources/js/cuf_run.js" var="cuf_run" />
<script src="${cuf_run}"></script>

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
						<li><a href="textAnalyse.html">Text Analyse</a></li>
						<li><a href="login.html">Verwaltung</a></li>
						<li><a href="about.html">About Us</a></li>
						<li class="active"><a href="contact.html">Contact Us</a></li>
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
							<span>Kontakt</span>
						</h2>
						<p>Wir bedanken uns für Ihren Besuch. Wenn Sie noch Frage
							haben, kontaktieren Sie uns bitte über das Kontaktformular.</p>
					</div>
					<div class="article">
						<h2>
							<span>Senden Sie uns eine </span> Nachricht
						</h2>
						<form action="#" method="post" id="sendemail">
							<ol>
								<li><label for="name">Name (required)</label> <input
									id="name" name="name" class="text" /></li>
								<li><label for="email">Email Address (required)</label> <input
									id="email" name="email" class="text" /></li>
								<li><label for="message">Your Message</label> <textarea
										id="message" name="message" rows="8" cols="50"></textarea></li>
								<li><input type="image" name="imageField" id="imageField"
									src="resources/images/submit.gif" class="send" />
									<div class="clr"></div></li>
							</ol>
						</form>
					</div>
				</div>
				<div class="sidebar">
					<div class="gadget">
						<h2 class="star">
							<span>Nützliche Links</span>
						</h2>
						<ul class="sb_menu">

								<li><a href="https://www.informatik.uni-frankfurt.de/">Goethe-Universität
								</a><br /> Institut für Informatik</li>
								<li><a href="http://acoli.cs.uni-frankfurt.de/">Lehrstuhl
										Prof. Christian Chiarcos</a> <br />Angewandte Computerlinguistik
								</li>
								<li><a href="http://www.w3schools.com/html/">w3schools.com</a><br />
									Sehr gut Tutorium-Webseite</li>
								<li><a
									href="https://wiki.blazegraph.com/wiki/index.php/Main_Page">blazegraph</a><br />
									Website Builder Software</li>

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
						Analysepipeline für Nachrichtentexte<br /> ï· -Englisch ï·
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
