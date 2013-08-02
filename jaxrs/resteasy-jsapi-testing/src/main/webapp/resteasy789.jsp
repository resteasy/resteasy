<html>
<body>
<h2>RESTEASY-789 - JSAPIServlet does not generate inherited methods correctly</h2>

<form>

    <input type='button' onclick='testAdd();' value='Test Add'/>
    <input type='button' onclick='testMinus();' value='Test Minus'/>
</form>

<div id="add_result"></div>
<div id="minus_result"></div>

<script src="/resteasy-jsapi-testing/rest-js" type="text/javascript"></script>
<script type="text/javascript">
    function testAdd() {
        document.getElementById('add_result').innerHTML =
                Add.operate({operand1:1, operand2:1});
    }

    function testMinus() {
        document.getElementById('minus_result').innerHTML =
                Minus.operate({operand1:1, operand2:1});
    }
</script>
</body>
</html>
