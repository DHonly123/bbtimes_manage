﻿var soureUrl="http://www.ibbtimes.com/upload/";//设置图片资源的nginx映射的根url


__CreateJSPath = function (js) {
    var scripts = document.getElementsByTagName("script");
    var path = "";
    for (var i = 0, l = scripts.length; i < l; i++) {
        var src = scripts[i].src;
        if (src.indexOf(js) != -1) {
            var ss = src.split(js);
            path = ss[0];
            break;
        }
    }
    var href = location.href;
    href = href.split("#")[0];
    href = href.split("?")[0];
    var ss = href.split("/");
    ss.length = ss.length - 1;
    href = ss.join("/");
    if (path.indexOf("https:") == -1 && path.indexOf("http:") == -1 && path.indexOf("file:") == -1 && path.indexOf("\/") != 0) {
        path = href + "/" + path;
    }
    return path;
}

function baseHeader(userid){
	return	soureUrl+userid+"/header/header48x48.jpg?rd="+new Date().valueOf();
}

function classHeader(classid){
	return	soureUrl+"/circle/"+classid+"/header128x128.jpg?rd="+new Date().valueOf();
}

function schoolHeader(schoolid){
	return	soureUrl+"/organization/"+schoolid+"/header150x150.png?rd="+new Date().valueOf();
}


var bootPATH = __CreateJSPath("boot.js");

//debugger
mini_debugger = true;   

//miniui
document.write('<script src="' + bootPATH + 'jquery-1.9.1.min.js" type="text/javascript"></sc' + 'ript>');
document.write('<script src="' + bootPATH + 'miniui/miniui.js" type="text/javascript" ></sc' + 'ript>');
document.write('<link href="' + bootPATH + 'miniui/themes/default/miniui.css" rel="stylesheet" type="text/css" />');
document.write('<link href="' + bootPATH + 'miniui/themes/blue2010/skin.css" rel="stylesheet" type="text/css" />');
document.write('<link href="' + bootPATH + 'miniui/themes/icons.css" rel="stylesheet" type="text/css" />');

//skin
var skin = getCookie("miniuiSkin");
if (skin) {
    document.write('<link href="' + bootPATH + 'miniui/themes/' + skin + '/skin.css" rel="stylesheet" type="text/css" />');
}


////////////////////////////////////////////////////////////////////////////////////////
function getCookie(sName) {
    var aCookie = document.cookie.split("; ");
    var lastMatch = null;
    for (var i = 0; i < aCookie.length; i++) {
        var aCrumb = aCookie[i].split("=");
        if (sName == aCrumb[0]) {
            lastMatch = aCrumb;
        }
    }
    if (lastMatch) {
        var v = lastMatch[1];
        if (v === undefined) return v;
        return unescape(v);
    }
    return null;
}


function readCookie(name, index, reg) {
	var strCookie = document.cookie;
	var attCookie = strCookie.split(";");
	var uCookie;
	for ( var i = 0; i < attCookie.length; i++) {
		var attr = attCookie[i].split('=');
		if (attr[0].replace(/(^\s*)|(\s*$)/g, "") == name) {
			uCookie = attr[1];
			break;
		}
	}

	if (uCookie != null && uCookie.length > 0) {
		var values = uCookie.split(reg);
		for ( var t = 0; t < values.length; t++) {
			if (t == index) {
				return values[t];
			}

		}
	}
	return;
}

var WinAlerts = window.alert;
window.alert = function (e) {
	if (e != null && e.indexOf("www.miniui.com")>-1){
	//和谐了
	}
	else{
		WinAlerts (e);
	}

};