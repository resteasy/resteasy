<html>
<body>
<h2>RESTEasy JS-API Demo</h2>

<form>
    <input type='button' onclick='testPathParam();' value='Test Path Param'/>
    <input type='button' onclick='testFormParam();' value='Test Form Param'/>
    <input type='button' onclick='testQueryParam();' value='Test Query Param'/>
    <input type='button' onclick='testCookieParam();' value='Test Cookie Param'/>
    <input type='button' onclick='testMatrixParam();' value='Test Matrix Param'/>
</form>
<%
    javax.servlet.http.Cookie cookie = new Cookie("test-cookie", "Hello");
    response.addCookie(cookie);
%>
<script src="/resteasy-jsapi-testing/rest-js" type="text/javascript"></script>
<script type="text/javascript">
    var global_order_id = 0;
    function testPathParam() {
        var order = SmokeTestResource.testPathParam({id:global_order_id++});
        alert(order);
    }

    function testFormParam() {
        SmokeTestResource.testFormParam({key:["val1", "val2", "val3"]});
    }

    function testQueryParam() {
        alert(SmokeTestResource.testQueryParam({key:["val1", "val2", "val3"]}));
    }

    function testCookieParam() {
        alert(SmokeTestResource.testCookieParam());
    }

    function testMatrixParam() {
        alert(SmokeTestResource.testMatrixParam({key:["val1", "val2", "val3"]}));
    }
</script>
</body>
</html>
