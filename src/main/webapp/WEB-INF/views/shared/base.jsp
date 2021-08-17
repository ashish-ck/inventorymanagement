<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<tiles:importAttribute name="javascripts" />
<tiles:importAttribute name="stylesheets" />

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<title>Drivojoy | <tiles:insertAttribute name="title" /></title>

<c:forEach var="css" items="${stylesheets}">
	<link rel="stylesheet" type="text/css" href="<c:url value="${css}"/>">
</c:forEach>

<link rel="apple-touch-icon" href="/resources/img/favicon.ico" />
<c:forEach var="script" items="${javascripts}">
	<script src="<c:url value="${script}"/>"></script>
</c:forEach>

<style type="text/css">
textarea {
	resize: none;
}

.select2>.select2-choice.ui-select-match {
	/* Because of the inclusion of Bootstrap */
	height: 29px;
}

.selectize-control>.selectize-dropdown {
	top: 36px;
}

/* .tree { */
/*     min-height:20px; */
/*     padding:19px; */
/*     margin-bottom:20px; */
/*     background-color:#fbfbfb; */
/*     border:1px solid #999; */
/*     -webkit-border-radius:4px; */
/*     -moz-border-radius:4px; */
/*     border-radius:4px; */
/*     -webkit-box-shadow:inset 0 1px 1px rgba(0, 0, 0, 0.05); */
/*     -moz-box-shadow:inset 0 1px 1px rgba(0, 0, 0, 0.05); */
/*     box-shadow:inset 0 1px 1px rgba(0, 0, 0, 0.05) */
/* } */
.tree li {
	list-style-type: none;
	margin: 0;
	padding: 10px 5px 0 5px;
	position: relative
}

.tree li::before, .tree li::after {
	content: '';
	left: -20px;
	position: absolute;
	right: auto
}

.tree li::before {
	border-left: 1px solid #999;
	bottom: 50px;
	height: 100%;
	top: 0;
	width: 1px
}

.tree li::after {
	border-top: 1px solid #999;
	height: 20px;
	top: 25px;
	width: 25px
}

.tree li span {
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
	border: 1px solid #999;
	border-radius: 5px;
	display: inline-block;
	padding: 3px 8px;
	text-decoration: none
}

.tree li>span {
	cursor: pointer
}

.tree>ul>li::before, .tree>ul>li::after {
	border: 0
}

.tree li:last-child::before {
	height: 30px
}

.tree li>span:hover, .tree li>span:hover+ul li span {
	background: #eee;
	border: 1px solid #94a0b4;
	color: #000
}
</style>

</head>
<body class="hold-transition skin-blue sidebar-mini">

	<div class="wrapper" data-ng-app="inventoryApp"
		data-ng-controller="inventoryAppController" data-ng-init="fetchInitData()">
		<tiles:insertAttribute name="header" />
		<tiles:insertAttribute name="sidebar" />
		<div class="content-wrapper">
			<tiles:insertAttribute name="body" />
		</div>

		<footer class="main-footer">
			<div class="pull-right hidden-xs">
				<b>Beta Version</b>
			</div>
			<strong>Copyright &copy; 2015-2017 <a
				href="http://drivojoy.com">DrivoJoy</a>.
			</strong> All rights reserved.
		</footer>

		<tiles:insertAttribute name="rightSidebar" />
		<!-- Add the sidebar's background. This div must be placed
           immediately after the control sidebar -->
		<div class="control-sidebar-bg"></div>

	</div>


	<script type="text/javascript">
		
	</script>
</body>
</html>