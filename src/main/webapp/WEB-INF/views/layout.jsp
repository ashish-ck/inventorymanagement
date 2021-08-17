<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<title>Drivojoy | Inventory</title>

<link rel="stylesheet" type="text/css"
	href="/resources/bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css"
	href="/resources/bootstrap/css/font-awesome.min.css" />
<link rel="stylesheet" type="text/css"
	href="/resources/bootstrap/css/ionicons.min.css" />
<link rel="stylesheet" type="text/css"
	href="/resources/css/AdminLTE.min.css" />
<link rel="stylesheet" type="text/css"
	href="/resources/css/skins/_all-skins.min.css" />
<link rel="stylesheet" type="text/css"
	href="/resources/plugins/iCheck/flat/blue.css" />
<link rel="stylesheet" type="text/css"
	href="/resources/plugins/morris/morris.css" />
<link rel="stylesheet" type="text/css"
	href="/resources/plugins/jvectormap/jquery-jvectormap-1.2.2.css" />
<link rel="stylesheet" type="text/css"
	href="/resources/plugins/datepicker/datepicker3.css" />
<link rel="stylesheet" type="text/css"
	href="/resources/plugins/daterangepicker/daterangepicker-bs3.css" />
<link rel="stylesheet" type="text/css"
	href="/resources/plugins/datepicker/datepicker3.css" />
<link rel="stylesheet" type="text/css"
	href="/resources/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css" />




</head>
<body class="hold-transition skin-blue sidebar-mini">

	<div class="wrapper" data-ng-app="inventoryApp"
		data-ng-controller="inventoryAppController">

		<jsp:include page="shared/userHeader.jsp"></jsp:include>
		<jsp:include page="shared/sidebar.jsp"></jsp:include>

		<div data-ng-view></div>



		<footer class="main-footer">
			<div class="pull-right hidden-xs">
				<b>Beta Version</b>
			</div>
			<strong>Copyright &copy; 2015-2017 <a
				href="http://drivojoy.com">DrivoJoy</a>.
			</strong> All rights reserved.
		</footer>


		<jsp:include page="shared/rightSidebar.jsp"></jsp:include>

		<!-- Add the sidebar's background. This div must be placed
           immediately after the control sidebar -->
		<div class="control-sidebar-bg"></div>

	</div>




	<script src="/resources/plugins/jQuery/jQuery-2.1.4.min.js"></script>
	<script src="/resources/js/jquery-ui.js"></script>
	<script src="/resources/bootstrap/js/bootstrap.min.js"></script>
	<!-- <script src="/resources/js/plugins/morris/raphael.min.js" ></script> -->
	<!-- <script src="/resources/js/plugins/morris/morris.min.js" ></script> -->
	<!-- <script src="/resources/plugins/sparkline/jquery.sparkline.min.js" ></script> -->
	<!-- <script src="/resources/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js" ></script> -->
	<!-- <script src="/resources/plugins/jvectormap/jquery-jvectormap-world-mill-en.js" ></script> -->
	<!-- <script src="/resources/plugins/knob/jquery.knob.js" ></script> -->
	<!-- <script src="/resources/js/moment.min.js" ></script> -->
	<!-- <script src="/resources/plugins/daterangepicker/daterangepicker.js" ></script> -->
	<!-- <script src="/resources/plugins/datepicker/bootstrap-datepicker.js" ></script> -->
	<!-- <script src="/resources/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js" ></script> -->
	<!-- <script src="/resources/plugins/slimScroll/jquery.slimscroll.min.js" ></script> -->
	<!-- <script src="/resources/plugins/fastclick/fastclick.min.js" ></script> -->
	<script src="/resources/js/inventoryAppUI.js"></script>
	<!-- <script src="/resources/js/dashboard.js" ></script> -->
	<script src="/resources/js/demo.js"></script>
	<script src="/resources/js/angular.min.js"></script>
	<script src="/resources/js/angular-route.min.js"></script>
	<script src="/resources/app/inventory/inventoryApp.js"></script>
	<script src="/resources/app/inventory/controllers/inventoryAppController.js"></script>

	<script>

	</script>

</body>
</html>
