<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="renderer" content="webkit">
	<meta http-equiv="Pragma" CONTENT="no-cache">
	<meta http-equiv="Cache-Control" CONTENT="no-cache">
	<meta http-equiv="Expires" CONTENT="0">
  
	<title>EasyCode - <spring:theme code='project.name' /> - 登录</title>
	
	<link rel="shortcut icon" href="<spring:theme code='project.icon' />" type="image/x-icon" />
	<link type="text/css" rel="stylesheet" href="http://cdn.easycodebox.com/??bootstrap/3.3.6/css/bootstrap.min.css,font-awesome/4.6.3/css/font-awesome.min.css,ionicons/2.0.1/css/ionicons.min.css,admin-tmpls/AdminLTE/2.3.6/css/AdminLTE.min.css,icheck/1.0.2/skins/square/blue.min.css" />
	<style type="text/css">
		.input-error {
		  border-color: #d73925;
		}
		.errors {
			color: #dd4b39 !important;
		}
		.main-footer {
			margin-left: 0;
			text-align: center;
			bottom: 0;
			width: 100%;
			position: absolute;
		}
	</style>
</head>
<body class="hold-transition login-page">
<div class="login-box">
	<div class="login-logo">
    	<a href="http://www.easycodebox.com"><b>Easy</b>Code</a>
  	</div>
