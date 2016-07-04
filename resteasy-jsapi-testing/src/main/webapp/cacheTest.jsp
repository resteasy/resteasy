<html>
<body>
<h2>RESTEasy JS-API Cache Test</h2>

<form>
    UUID: <input id="uuid" name="uuid"/>
    <input type='button' onclick='testAjaxCallCache();' value='Ajax Call'/>
</form>

<div id="ajax_call_result"></div>

<script src="/resteasy-jsapi-testing/rest-js" type="text/javascript"></script>
<script type="text/javascript">
    REST.debug = true;
    REST.antiBrowserCache = true;

    function testAjaxCallCache() {
        var _uuid = document.getElementById('uuid').value;
        CachedResource.get({uuid:_uuid});
        document.getElementById('ajax_call_result').innerHTML = REST.lastRequest.status;
    }
</script>
</body>
</html>
