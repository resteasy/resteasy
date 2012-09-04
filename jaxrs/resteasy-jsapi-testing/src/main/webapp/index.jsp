<html>
<body>
<h2>RESTEasy JS-API Demo</h2>

<form>
    <input type='button' onclick='testPathParam();' value='Test Path Param'/>
    <input type='button' onclick='testFormParam();' value='Test Form Param'/>
    <input type='button' onclick='testFormParam2();' value='Test Form Param2'/>
    <input type='button' onclick='testQueryParam();' value='Test Query Param'/>
    <input type='button' onclick='testCookieParam();' value='Test Cookie Param'/>
    <input type='button' onclick='testMatrixParam();' value='Test Matrix Param'/>
    <input type='button' onclick='testHeaderParam();' value='Test Header Param'/>
    <input type='button' onclick='testRESTEasy731False();' value='RESTEASY-731-false'/>
    <input type='button' onclick='testRESTEasy731Zero();' value='RESTEASY-731-zero'/>

</form>

<div id="path_result"></div>
<div id="form_result"></div>
<div id="form_result2"></div>
<div id="query_result"></div>
<div id="cookie_result"></div>
<div id="matrix_result"></div>
<div id="header_result"></div>
<div id="testRESTEasy731FalseResult"></div>
<div id="testRESTEasy731ZeroResult"></div>

<%
    javax.servlet.http.Cookie cookie = new Cookie("username", "Weinan");
    response.addCookie(cookie);
%>
<script src="/resteasy-jsapi-testing/rest-js" type="text/javascript"></script>
<script type="text/javascript">
    var global_order_id = 0;
    function testPathParam() {
        document.getElementById('path_result').innerHTML =
                SmokeTestResource.testPathParam({id:global_order_id++});

    }

    function testFormParam() {
        document.getElementById('form_result').innerHTML =
                SmokeTestResource.testFormParam({key:["a", "b", "c"]});
    }

    function testFormParam2() {
        document.getElementById('form_result2').innerHTML =
                SmokeTestResource.testFormParam2({key:"xyz"});
    }

    function testQueryParam() {
        document.getElementById('query_result').innerHTML =
                SmokeTestResource.testQueryParam({key:["d", "e", "f"]});
    }

    function testCookieParam() {
        document.getElementById('cookie_result').innerHTML =
                SmokeTestResource.testCookieParam();
    }

    function testMatrixParam() {
        document.getElementById('matrix_result').innerHTML =
                SmokeTestResource.testMatrixParam({key:["g", "h", "i"]});
    }

    function testHeaderParam() {
        document.getElementById('header_result').innerHTML =
                SmokeTestResource.testHeaderParam();
    }

    function testRESTEasy731False() {
        document.getElementById('testRESTEasy731FalseResult').innerHTML =
                SmokeTestResource.testRESTEasy731False(false);
    }

    function testRESTEasy731Zero() {
        document.getElementById('testRESTEasy731ZeroResult').innerHTML =
                SmokeTestResource.testRESTEasy731Zero(0);
    }


</script>
</body>
</html>
