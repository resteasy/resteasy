<html>
<body>
<h2>RESTEasy JS-API Demo</h2>
<form>
<input type='button' onclick='callJsApi();' value='Get Orders'/>
</form>

<script src="/examples-jsapi/rest-js" type="text/javascript"></script>
<script type="text/javascript">
	var global_order_id = 0;
	function callJsApi() {
		var order = Orders.getOrder({id: global_order_id++});
		alert(order);
	}
</script>
</body>
</html>
