<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<section class="content-header"></section>
<div class="login-box">
	<div class="login-logo">
		<a href="/"><b>Drivojoy</b><i>Inventory</i></a>
	</div>
	<!-- /.login-logo -->
	<div class="login-box-body">
		<p class="login-box-msg">Sign in to start your session</p>

		<form:form id="loginForm" method="post" action="login/do"
			modelAttribute="loginBean">
			<div class="form-group has-feedback">
				<form:input id="username" class="form-control"
					placeholder="username" name="username" path="username" />
				<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
			</div>
			<div class="form-group has-feedback">
				<form:password id="password" class="form-control"
					placeholder="Password" name="password" path="password" />
				<span class="glyphicon glyphicon-lock form-control-feedback"></span>
			</div>
			<input type="submit" class="btn btn-primary btn-block btn-flat"
				value="Sign In" />
		</form:form>
	</div>
	<!-- /.login-box-body -->
</div>




<script>
	$(function() {
		$('input').iCheck({
			checkboxClass : 'icheckbox_square-blue',
			radioClass : 'iradio_square-blue',
			increaseArea : '20%' // optional
		});
	});
</script>
