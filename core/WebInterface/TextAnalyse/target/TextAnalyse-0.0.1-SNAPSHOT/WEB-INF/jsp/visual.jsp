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


<spring:url value="/resources/js_v/d3.v3.min.js" var="d3.v3.min" />
<spring:url value="/resources/js_v/underscore-min.js" var="underscore-min" />
<spring:url value="/resources/js_v/color.min.js" var="color.min" />
<spring:url value="/resources/js_v/triplevis.js" var="triplevis" />

</head>
<body>
    <script src="${d3.v3.min}"></script>
    <script src="${underscore-min}"></script>
    <script src="${color.min}"></script>
    <script src="${triplevis}"></script>
    
 </body>

</html>
