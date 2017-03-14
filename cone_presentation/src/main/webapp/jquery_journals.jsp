<?xml version="1.0" encoding="UTF-8" ?>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<script src="/cone/js/jquery-1.11.1.min.js" type="text/javascript"></script>
		<script src="./js/jquery.suggest.js" type="text/javascript"></script>
		<script src="./js/jquery.dimensions.js" type="text/javascript"></script>
		<link href="./js/jquery.suggest.css" rel="stylesheet" type="text/css" />
		<title>CoNE 4 JQuery</title>
	</head>
	<body lang="de">
		<h1>CoNE 4 JQuery</h1>
		<form name="form1" method="get">
			Enter query string:
			<input class="journalSuggest" id="test1" size="100"/>
			
		</form>
		<script type="text/javascript">
			$(".journalSuggest").suggest("journals/query");
		</script>
	</body>
</html>