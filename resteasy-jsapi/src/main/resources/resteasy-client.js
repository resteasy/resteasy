// namespace
var REST = {
	apiURL : null,
	loglevel : 0
};

// constructor
REST.Request = function (){
	REST.log("Creating new Request");
}

REST.Request.prototype = {
		uri : null,
		method : "GET",
		username : null,
		password : null,
		acceptHeader : "*/*",
		contentTypeHeader : null,
		async : true,
		queryParameters : [],
		matrixParameters : [],
		cookies : [],
		headers : [],
		entity : null,
		execute : function(callback){
			var request = new XMLHttpRequest();
			var url = this.uri;
			var restRequest = this;
			for(var i=0;i<this.matrixParameters.length;i++){
				url += ";" + REST.encodePathParamName(this.matrixParameters[i][0]);
				url += "=" + REST.encodePathParamValue(this.matrixParameters[i][1]);
			}
			for(var i=0;i<this.queryParameters.length;i++){
				if(i == 0)
					url += "?";
				else
					url += "&";
				url += REST.encodeQueryParamNameOrValue(this.queryParameters[i][0]);
				url += "=" + REST.encodeQueryParamNameOrValue(this.queryParameters[i][1]);
			}
			for(var i=0;i<this.cookies.length;i++){
				document.cookie = escape(this.cookies[i][0]) 
					+ "=" + escape(this.cookies[i][1]);
			}
			request.open(this.method, url, this.async, this.username, this.password);
			var acceptSet = false;
			var contentTypeSet = false;
			for(var i=0;i<this.headers.length;i++){
				if(this.headers[i][0].toLowerCase() == 'accept')
					acceptSet = this.headers[i][1];
				if(this.headers[i][0].toLowerCase() == 'content-type')
					contentTypeSet = this.headers[i][1];
				request.setRequestHeader(REST.encodeHeaderName(this.headers[i][0]),
						REST.encodeHeaderValue(this.headers[i][1]));
			}
			if(!acceptSet)
				request.setRequestHeader('Accept', this.acceptHeader);
			if(this.entity && !contentTypeSet && this.contentTypeHeader){
				contentTypeSet = this.contentTypeHeader;
				request.setRequestHeader('Content-Type', this.contentTypeHeader);
			}
			if(callback){
				request.onreadystatechange = function() {
					if(this.readyState == 4) {
						var entity;
						if(this.status >= 200 && this.status < 300){
							var contentType = request.getResponseHeader("Content-Type");
							if(contentType != null){
								if(REST._isXMLMIME(contentType))
									entity = this.responseXML;
								else if(contentType.equals("application/json"))
									entity = JSON.parse(this.responseText);
								else
									entity = this.responseText;
							}else
								entity = this.responseText;
						}
						callback(this.status, this, entity);
					}
				};
			}
			var data = this.entity;
			if(this.entity){
				if(this.entity instanceof Element){
					if(!contentTypeSet || REST._isXMLMIME(contentTypeSet))
						data = REST.serialiseXML(this.entity);
				}else if(this.entity instanceof Object){
					if(!contentTypeSet || contentTypeSet.equals("application/json"))
						data = JSON.stringify(this.entity);
				}
			}
			REST.log("Content-Type set to "+contentTypeSet);
			REST.log("Entity set to "+data);
			request.send(data);
		},
		setAccepts : function(acceptHeader){
			REST.log("setAccepts("+acceptHeader+")");
			this.acceptHeader = acceptHeader;
		},
		setCredentials : function(username, password){
			this.password = password;
			this.username = username;
		},
		setEntity : function(entity){
			REST.log("setEntity("+entity+")");
			this.entity = entity;
		},
		setContentType : function(contentType){
			REST.log("setContentType("+contentType+")");
			this.contentTypeHeader = contentType;
		},
		setURI : function(uri){
			REST.log("setURI("+uri+")");
			this.uri = uri;
		},
		setMethod : function(method){
			REST.log("setMethod("+method+")");
			this.method = method;
		},
		setAsync : function(async){
			REST.log("setAsync("+async+")");
			this.async = async;
		},
		addCookie : function(name, value){
			this.cookies.push([name, value]);
		},
		addQueryParameter : function(name, value){
			this.queryParameters.push([name, value]);
		},
		addMatrixParameter : function(name, value){
			this.matrixParameters.push([name, value]);
		},
		addHeader : function(name, value){
			this.headers.push([name, value]);
		}
}

REST.log = function(string){
	if(REST.loglevel > 0)
		print(string);
}

REST._isXMLMIME = function(contentType){
	return contentType.equals("text/xml")
			|| contentType.equals("application/xml")
			|| (contentType.indexOf("application/") == 0
				&& contentType.lastIndexOf("+xml") == (contentType.length - 4));
}

//see http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
REST.encodeHeaderName = function (val){
	// token+ from http://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html#sec2
	// FIXME: implement me
	return val;
}

//see http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
REST.encodeHeaderValue = function (val){
	// *TEXT or combinations of token, separators, and quoted-string from http://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html#sec2
	// FIXME: implement me
	return val;
}

// see http://www.ietf.org/rfc/rfc3986.txt
REST.encodeQueryParamNameOrValue = function (val){
	// FIXME: implement me
	return val;
}

//see http://www.ietf.org/rfc/rfc3986.txt
REST.encodePathSegment = function (val){
	// FIXME: implement me
	return val;
}

//see http://www.ietf.org/rfc/rfc3986.txt
REST.encodePathParamName = function (val){
	// FIXME: implement me
	return val;
}

//see http://www.ietf.org/rfc/rfc3986.txt
REST.encodePathParamValue = function (val){
	// FIXME: implement me
	return val;
}

REST.serialiseXML = function(node){
	if (typeof XMLSerializer != "undefined")
		return (new XMLSerializer()).serializeToString(node) ;
	else if (node.xml) return node.xml;
	else throw "XML.serialize is not supported or can't serialize " + node;
}