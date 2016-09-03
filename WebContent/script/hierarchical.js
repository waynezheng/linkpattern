$(document).ready(function() {
	linkpattern_servlet = "/linkpattern/SLinkServlet";
	var url = window.location.href;
//	console.log(url);
//	console.log(getSpFromUrl(url));
	$(".form-search").find("#query").val(getSpFromUrl(url));
	var startpoint = getSpFromUrl(url);
	$(".input_uri").html(startpoint);
//	getPatternsForFilter();
	getHierarchicalLinks();
	getAllLinkedEntities();
	$("#searchq").live("click", function() {
		var input = $(".form-search").find("#query");
		uri = $(input).val();
		window.open(getHrefForLinkPattern(uri), "_self");
		$(".input_uri").html(uri);
	//	getPatternsForFilter();
		getHierarchicalLinks();
		getAllLinkedEntities();
	});
	
	
	//click link pattern to filter linked entities
//	$(".patterns .pattern").live("click", function() {
//		filterMembersByPatterns($(this));
//	});
	$(".jstree-node .jstree-anchor").live("click", function() {
		filterMembersByH($(this));
	});
	
	//remove a type filter
	$(".itag.patternfilter").live("click",function(){
		var box = $(".filter_box");
		if(box.find("a.itag.patternfilter").length>0){
		}else{
			box.html("");
		}
		$(this).remove();
		filterMembersByH();
	});
	
	//remove all type filters and return the original members
	$(".itag.main").live("click",function(){
		var box = $(".filter_box");
		box.html("");
		getAllLinkedEntities();
	});
	
	$(".continue_btn").live("click", function() {
		var $members_ul = $(".members");
		var $li = $members_ul.children("li");
		var sp = "";
		for (var i=0; i<$li.length; i++) {
			var a = $(($li)[i]).find("span").html();
			sp += a + ";";
		}
		sp = sp.substring(0, sp.length-1);
	//	console.log(sp);
		window.open(getHrefForLinkPattern(sp), "_blank");
		
	});
	if ($("#query").length > 0) {
		var lastTask = null;
		$("#query").autocomplete({
			source: function(request, response) {
				console.log("autocomplete!");
				var term = request.term;
				var data = {};
				data["prefix"] = term;
				data = JSON.stringify(data);
				data = ("getAutoComplete=" + encodeURIComponent(data));
				$("#query").autocomplete( "option", "autoFocus", false);
				$.ajax({
					url: linkpattern_servlet,
					async: true,
					data: data,
					dataType: "json",
					type: "post",
					beforeSend: function() {
						
					},
					success: function(result) {
						response($.map(result, function(item){
							var rt = {
									localname: item.localname,
									uri: item.uri
							};
							return rt;
						}));
					}
				});
			},
			minLength: 1,
			select: function(event, ui) {
				var uri = ui.item.uri;
				window.open(getHrefForLinkPattern(uri), "_self");
			}
			
		}).data("autocomplete")._renderItem = function(ul, item) {
		//	ul.css({"top":"64px","left":"180px","width":"640px"});
			
			return $("<li></li>")
			.data("item.autocomplete", item)
			.append("<a><strong>"+item.localname+"</strong></a>")
			.appendTo(ul);
		};
	}
	
});

//filter members by h
function filterMembersByH(ele) {
	if (ele) {
		var label = $(ele).html();
		var li = $(ele).parent();
		console.log(li.attr("members"));
		var members = li.attr("members");
		var intersectionflag = li.attr("intersection");
		var box = $('.filter_box').hide();
		if (box.find("a.itag").length > 0) {}
		else {
			var home = $("<a class='main itag'></a>").appendTo(box);
			home.html("<span>Filter path</span>");
		}
		var tag = $("<a class='itag patternfilter'></a>");
		tag.attr("intersectionflag", intersectionflag);
		tag.html(label);
		var close = $("<span class='close-btn close-icon'></span>").appendTo(tag);
		var props = $("<ul class='prop_list' style='display:none'></ul>").appendTo(tag);
		var memArray = members.split(";");
		console.log(memArray.length);
		for (var i=0; i<memArray.length; i++) {
			var mem = $("<li class='prop'>"+memArray[i]+"</li>").appendTo(props);
		}
		tag.appendTo(box);
	}
	var patternfilters = getCurrentPatternFilters();
	var uri = $(".input_uri").html();
	var data = {};
	data["uri"] = uri;
	data["patternfilters"] = patternfilters;
	data = JSON.stringify(data);
	data = ("getLinkedEntities=" + encodeURIComponent(data));
	$.ajax({
		url: linkpattern_servlet,
		async: true,
		data: data,
		dataType: "json",
		type: "post",
		beforeSend: function() {},
		success: function(jsob) {
			var result = jsob.entities;
			var members = $(".members").html("");
			for (var i=0; i<result.length; i++) {
				var uri = result[i];
				var span = $("<span class='member_span' title="+uri+"></span>");
				var a = $("<a class='member_a' href="+getHrefForLinkPattern(uri)+" target='_blank'></a>").appendTo(span);
				a.html(getLocalNameFromUri(uri));
				var span1 = $("<span class='member_uri' style='display:none;'></span>").appendTo(a);
				span1.html(uri);
				span.appendTo(members);
			}
			if (result.length != 0) {
				$("#continue_btn").show();
			}
			else {
				$("#continue_btn").hide();
			}
		}
	});
	
	
	
	
}

function getAllLinkedEntities() {
//	var uri = "http://dbpedia.org/resource/James_Cameron"; //to do
	var uri = $(".input_uri").html();
	var data = {};
	data["uri"] = uri;
	data = JSON.stringify(data);
	data = ("getAllLinkedEntities=" + encodeURIComponent(data));
	$.ajax({
		url: linkpattern_servlet,
		async: true,
		data: data,
		dataType: "json",
		type: "post",
		beforeSend: function() {
			
		},
		success: function(jsob) {
			console.log("getAllLinkedEntities successed");
			var result = jsob.entities;
			var members = $(".members").html("");
			for (var i=0; i<result.length; i++) {
				var uri = result[i];
				var span = $("<span class='member_span' title="+uri+"></span>");
				var a = $("<a class='member_a' href="+getHrefForLinkPattern(uri)+" target='_blank'></a>").appendTo(span);
				a.html(getLocalNameFromUri(uri));
				var span1 = $("<span class='member_uri' style='display:none;'></span>").appendTo(a);
				span1.html(uri);
				span.appendTo(members);
			}
			if (result.length != 0) {
				$("#continue_btn").show();
			}
		}
	});
}

function getCurrentPatternFilters() {
	var array = new Array();
	var flag = new Array();
	var blockID = new Array();
	var intersectionflag = new Array();
	var props = new Array();
	var filterbox = $(".filter_box");
	var filters = filterbox.children(".patternfilter");
//	for (var i=0; i<filters.length; i++) {
		var i = filters.length - 1;
		flag.push($(filters[i]).attr("flag"));
		blockID.push($(filters[i]).attr("blockID"));
		intersectionflag.push($(filters[i]).attr("intersectionflag"));
		var prop = new Array();
		var plist = $(filters[i]).find(".prop");
		for (var j=0; j<plist.length; j++) {
			console.log($(plist[j]).html());
			prop.push($(plist[j]).html());
		}
		props.push(prop);
//	}
	array.push(flag);
	array.push(blockID);
	array.push(intersectionflag);
	array.push(props);
	return array;
}

function getHierarchicalLinks() {
	var uri = $(".input_uri").html();
	var data = {};
	data["uri"] = uri;
	data = JSON.stringify(data);
	data = ("getHierarchicalLinks=" + encodeURIComponent(data));
	$.ajax({
		url: linkpattern_servlet,
		async: true,
		data: data,
		dataType: "json",
		type: "post",
		beforeSend: function() {},
		success: function(jsob) {
			$("#jstree").jstree({'core': {
				'data': jsob.h    
			}});
			if (jsob.h.length != 0) {
				$(".ul_title").show();
			}
		}
	});
}

/*function getPatternsForFilter() {
//	var uri = "http://dbpedia.org/resource/James_Cameron"; //to do
	var uri = $(".input_uri").html();
	var data = {};
	data["uri"] = uri;
	data = JSON.stringify(data);
	data = ("getAllLinks=" + encodeURIComponent(data));
	$.ajax({
		url: linkpattern_servlet,
		async: true,
		data: data,
		dataType: "json",
		type: "post",
		beforeSend: function() {
			
		},
		success: function(jsob) {
			console.log("getPatternsForFilter successed");
			var results = jsob.links;
			console.log(results.length);
			var patterns = $("ul.patterns");
			patterns.html("");
			for (var i=0; i<results.length; i++) {
				var result = results[i];
				var label = result.label;
				var flag = result.flag;
				var blockID = result.blockID;
				var intersectionflag = result.intersectionflag;
				var props = result.props;
				var num = result.num;
				var li = $("<li class='pattern'></li>");
				li.attr("flag", flag);
				li.attr("blockID", blockID);
				li.attr("intersectionflag", intersectionflag);
				var span = $("<span></span>").appendTo(li);
				var prop_ul = $("<ul class='prop_list' style='display:none;'></ul>");
				
				var linkname = "";
				for (var j=0; j<props.length; j++) {
					var prop = props[j].prop;
					var prop_li = $("<li class='prop'></li>").appendTo(prop_ul);
					prop_li.html(prop);
					if (prop.substring(0,1) == "1") 
						linkname += "is " + getLocalNameFromUri(prop) + " of&";
					else 
						linkname += getLocalNameFromUri(prop) + "&";
				}
				linkname = linkname.substring(0, linkname.length-1);
				span.html(linkname);
				li.append("<span>("+num+")</span>");
				li.append(prop_ul);
				patterns.append(li);
			}
		}
	});
}*/

function getLocalNameFromUri(uri) {
	var ret = null;
	var pos = uri.lastIndexOf('#');
	if (pos >= 0) {
		ret = uri.substring(pos + 1);
		ret = decodeURIComponent(ret);
		ret = ret.replace(/_/g,' ');
		var local_name = yagoTrim(uri, ret);
		return local_name;
	}
	pos = uri.lastIndexOf('/');
	if (pos >= 0 && pos < uri.length - 1) {
		ret = uri.substring(pos + 1);
		ret = decodeURIComponent(ret);
		ret = ret.replace(/_/g,' ');
		var local_name = yagoTrim(uri, ret);
		return local_name;
	} else if (pos >= 0) {
		var cut = uri.substring(0, uri.length - 1);
		pos = cut.lastIndexOf('/');
		if (pos >= 0) {
			ret = cut.substring(pos + 1);
			ret = decodeURIComponent(ret);
			ret = ret.replace(/_/g,' ');
			var local_name = yagoTrim(uri, ret);
			return local_name;
		}
	}
}

function yagoTrim(uri, ret){
	if(ret != null && uri.indexOf("yago") > 0){
		ret = ret.replace(/[0-9]+\b/i, "");
	}
	return ret;
}

function getHrefForLinkPattern(uri) {
	return "hierarchy.jsp?sp=" + uri;
}

function getSpFromUrl(url) {
	if (url.indexOf("?") == -1)
		return "";
	var str = url.split("?")[1];
	var re = str.split("=")[1];
	return re;
}
