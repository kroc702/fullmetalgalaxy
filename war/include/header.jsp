<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<%@taglib prefix="fmg" uri="/WEB-INF/classes/fmg.tld"%>

	<div id="motifHeader">
	<div id="bordure">
	<div id="corps">
	<div id="content">
		<div id="header"><header>
			<div id="banniere">
				<div id="logo"></div>
			</div>
		<jsp:include page="/include/mytopmenu.jsp" />
  				<div id="chart"></div>
		<div id="sousMarin"></div>
		</header></div>
		<fmg:include page="/include/menu.jsp" />
