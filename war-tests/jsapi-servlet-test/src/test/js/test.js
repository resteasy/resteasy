var baseURI = 'http://localhost:9095';

Envjs(baseURI+'/test.html', {
	scriptTypes : {
	"text/javascript"   : true
	},
	logLevel: Envjs.DEBUG
});

REST.loglevel = 1;

function testFunctions() {
	assertNotNull("get function", MyResource.get);
}

function testGet() {
	var data = MyResource.get();
	assertEquals("ok", data);
}

function testGetFoo() {
	var data = MyResource.getFoo();
	assertEquals("foo", data);
}

function testGetParam() {
	var value = "bla";
	var data = MyResource.getParam({param: value});
	assertEquals(value, data);
}

function testGetFooParam() {
	var data = MyResource.getFooParam({param: "paramv", other: "otherv",
		q : "qv", c : "cv", h: "hv", m: "mv"});
	// cookie is null since env.js doesn't support cookies yet :(
	assertEquals("param=paramv;other=otherv;q=qv;c=null;h=hv;m=mv;", data);
}

function testPutFooParam() {
	var data = MyResource.putFooParam({param: "paramv", other: "otherv",
		q : "qv", c : "cv", h: "hv", m: "mv", $entity: "entityv"});
	// cookie is null since env.js doesn't support cookies yet :(
	assertEquals("param=paramv;other=otherv;q=qv;c=null;h=hv;m=mv;entity=entityv;", data);
}

function testGetXML() {
	var data = MyResource.getXML();
	print(data);
	assertTrue(data instanceof Document);
	var root = data.documentElement;
	assertEquals("test", root.nodeName);
	assertEquals(1, root.childNodes.length);
	assertEquals("var", root.childNodes[0].nodeName);
	assertEquals(1, root.childNodes[0].childNodes.length);
	assertEquals("foo", root.childNodes[0].childNodes[0].nodeValue);
}

function testGetJSON() {
	var data = MyResource.getJSON();
	assertEquals(data['var'], "foo");
}

function testGetJSONStarMIME() {
	var data = MyResource.getJSONStarMIME();
	assertEquals(data['var'], "foo");
}

function testPutJSON() {
	var toSend = {'var' : "ok"};
	var data = MyResource.putJSON({$entity: toSend});
	assertEquals("ok", data);
}

function testPutXML() {
	var toSend = document.implementation.createDocument(null, "test", null);
	var v = toSend.createElement("var");
	toSend.documentElement.appendChild(v);
	v.appendChild(toSend.createTextNode("ok"));
	var data = MyResource.putXML({$entity: toSend});
	assertEquals("ok", data);
}

function testGetMultiRepresentationXML(){
	var data = MyResource.getMultiRepresentation({$accepts: "application/xml"});
	assertTrue(data instanceof Document);
	var root = data.documentElement;
	assertEquals(23, root.childNodes.length);
}

function testGetMultiRepresentationJSON(){
	var data = MyResource.getMultiRepresentation({$accepts: "application/json"});
	assertTrue(data instanceof Array);
	assertEquals(23, data.length);
}

function testLookup(){
	var value = "foo";
	var data = MyResource.lookup({id: value});
	assertEquals(baseURI+"/rest/mine/"+value, data);
}

function testSubResource() {
	var data = MyResource.getSubResource.get();
	assertEquals("Hello", data);
}

function testSubResourceWithPath() {
	var data = MyResource.getSubResource.getWithPath();
	assertEquals("Hello withPath", data);
}

function testSubResource2() {
	var data = MyResource.getSubResource2.get({id: "a", foo: "b"});
	assertEquals("Hello a/b", data);
}

function testSubResource2WithPath() {
	var data = MyResource.getSubResource2.getWithPath({id: "a", foo: "b", bar: "c"});
	assertEquals("Hello withPath a/b/c", data);
}

function testDoubleSubResource() {
	var data = MyResource.getSubResource2.getSubResource.get({id: "a"});
	assertEquals("Hello", data);
}

function testDoubleSubResourceWithPath() {
	var data = MyResource.getSubResource2.getSubResource.getWithPath({id: "a"});
	assertEquals("Hello withPath", data);
}

function testForm() {
	var data = MyResource.postForm({a: "aa", b: "bb"});
	assertEquals("aa/bb", data);
}

// Encoding

function testUTF8(){
	assertEquals("%61", REST.Encoding.percentUTF8(0x61));
	assertEquals("%ce%91", REST.Encoding.percentUTF8(0x0391));
	assertEquals("%e2%89%a2", REST.Encoding.percentUTF8(0x2262));
	assertEquals("%f0%a3%8e%b4", REST.Encoding.percentUTF8(0x233B4));
}

function testPercentByte(){
	assertEquals("%05", REST.Encoding.percentByte(5));
	assertEquals("%20", REST.Encoding.percentByte(32));
}

function testEncoder(){
	assertEquals("abc", REST.Encoding.encodeFormNameOrValue("abc"));
	assertEquals("%c3%a9", REST.Encoding.encodeFormNameOrValue(String.fromCharCode(233))); //"é"
	assertEquals("%f0%9f%82%84", REST.Encoding.encodeFormNameOrValue(utf16Encode([0x1F084])));//"🂄"
}

function utf16Encode(input) {
    var output = [], i=0, len=input.length, value;
    while (i < len) {
        value = input[i++];
        if ( (value & 0xF800) === 0xD800 ) {
            throw new RangeError("UTF-16(encode): Illegal UTF-16 value");
        }
        if (value > 0xFFFF) {
            value -= 0x10000;
            output.push(String.fromCharCode(((value >>>10) & 0x3FF) | 0xD800));
            value = 0xDC00 | (value & 0x3FF);
        }
        output.push(String.fromCharCode(value));
    }
    return output.join("");
}

function testEncoders(){
	assertEquals("abc%24%2d%5f%2e%2b%21%2a%27%28%29%2c%2f%3f%26%3d%23+%0D%0A", REST.Encoding.encodeFormNameOrValue("abc$-_.+!*'(),/?&=# \n"));
	assertEquals("azAZ09-._~!$&'()*+,%3b=:@%c3%a9%2f%3f%23%5b%5d", REST.Encoding.encodePathParamValue("azAZ09-._~!$&'()*+,;=:@" + String.fromCharCode(233) +"/?#[]"));//"é"
	assertEquals("azAZ09-._~!$&'()*+,%3b=:@%c3%a9%2f%3f%23%5b%5d", REST.Encoding.encodePathSegment("azAZ09-._~!$&'()*+,;=:@" + String.fromCharCode(233) +"/?#[]"));//"é"
	assertEquals("azAZ09-._~!$&'()*+,%3b%3d:@%c3%a9%2f%3f%23%5b%5d", REST.Encoding.encodePathParamName("azAZ09-._~!$&'()*+,;=:@" + String.fromCharCode(233) +"/?#[]"));//"é"
	assertEquals("azAZ09-._~!$%26'()*%2b,;%3d:@%c3%a9/?%23%5b%5d", REST.Encoding.encodeQueryParamNameOrValue("azAZ09-._~!$&'()*+,;=:@" + String.fromCharCode(233) +"/?#[]"));//"é"
}