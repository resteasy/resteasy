<html>
<body>
<h2>RESTEasy JS-API Smoke Test</h2>

<form>
    <input type='button' onclick='testPathParam();' value='Test Path Param'/>
    <input type='button' onclick='testFormParam();' value='Test Form Param'/>
    <input type='button' onclick='testFormParam2();' value='Test Form Param2'/>
    <input type='button' onclick='testForm();' value='Test Form'/>
    <input type='button' onclick='testForm2();' value='Test Form2'/>
    <input type='button' onclick='testForm3();' value='Test Form3'/>
    <input type='button' onclick='testPrefixForm();' value='Test Prefix Form'/>
    <input type='button' onclick='testQueryParam();' value='Test Query Param'/>
    <input type='button' onclick='testCookieParam();' value='Test Cookie Param'/>
    <input type='button' onclick='testMatrixParam();' value='Test Matrix Param'/>
    <input type='button' onclick='testHeaderParam();' value='Test Header Param'/>
    <input type='button' onclick='testRESTEasy731False();' value='RESTEASY-731-false'/>
    <input type='button' onclick='testRESTEasy731Zero();' value='RESTEASY-731-zero'/>
    <input type='button' onclick="testSublocator()" value="Test Subresource Locator"/>
</form>

<div id="path_result"></div>
<div id="form_param_result"></div>
<div id="form_param_result2"></div>
<div id="form_result"></div>
<div id="form_result2"></div>
<div id="form_result3"></div>
<div id="prefix_form_result"></div>
<div id="query_result"></div>
<div id="cookie_result"></div>
<div id="matrix_result"></div>
<div id="header_result"></div>
<div id="testRESTEasy731FalseResult"></div>
<div id="testRESTEasy731ZeroResult"></div>
<div id="sublocatorResult"></div>

<%
    javax.servlet.http.Cookie cookie = new Cookie("username", "Weinan");
    response.addCookie(cookie);
%>
<script src="/resteasy-jsapi-testing/rest-js" type="text/javascript"></script>
<script type="text/javascript">
    var global_order_id = 0;

    function testSublocator() {
        document.getElementById('sublocatorResult').innerHTML =
                BookImpl.getChapter.getTitle({number:2})+BookImpl.getChapter.getBody({number:2});
    }

    function testPathParam() {
        document.getElementById('path_result').innerHTML =
                SmokeTestResource.testPathParam({id:global_order_id++});

    }

    function testFormParam() {
        document.getElementById('form_param_result').innerHTML =
                SmokeTestResource.testFormParam({key:["a", "b", "c"]});
    }

    function testFormParam2() {
        document.getElementById('form_param_result2').innerHTML =
                SmokeTestResource.testFormParam2({key:"xyz"});
    }

    function testForm() {
        document.getElementById('form_result').innerHTML =
                SmokeTestResource.testRESTEasy805({
                    myMap:[
                        {"myMap[foo].bar":".-_~=&"},
                        {"myMap[.-_~].bar":".-_~=&"},
                        {"myMap[b794c4a0-14b7-0130-c2da-20c9d04983db].bar":".-_~=&"}
                    ]
                });
    }

    function testForm2() {
        document.getElementById('form_result2').innerHTML =
                SmokeTestResource.testRESTEasy805Case2({stuff:"A-&1", myHeader:"2B=_2", number:14});
    }

    function testForm3() {
        document.getElementById('form_result3').innerHTML =
                SmokeTestResource.testRESTEasy805Case3({
                    foos:[
                        {"foos[2].bar":'A'},
                        {"foos[1].bar":'B'},
                        {"foos[0].bar":'C'}
                    ]
                });
    }

    function testPrefixForm() {
        document.getElementById('prefix_form_result').innerHTML =
                SmokeTestResource.postPrefixForm({
                    telephoneNumbers:[
                        {"telephoneNumbers[0].countryCode":1},
                        {"telephoneNumbers[0].number":1},
                        {"telephoneNumbers[1].countryCode":1},
                        {"telephoneNumbers[1].number":1}
                    ],
                    address:[
                        {"address[INVOICE].street":"1"},
                        {"address[INVOICE].houseNumber":1},
                        {"address[SHIPPING].street":"1"},
                        {"address[SHIPPING].houseNumber":1}
                    ]
                });
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
