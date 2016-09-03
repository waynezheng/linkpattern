<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Link Pattern(3)</title>
<link rel="stylesheet" type="text/css" href="css/linkpattern.css" />
<link rel="stylesheet" type="text/css" href="css/bootstrap1.css" />
<link rel="stylesheet" type="text/css" href="css/jquery-ui.css" />
<script type="text/javascript" src="script/jquery.js"></script>
<script type="text/javascript" src="script/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="script/bmc3.js"></script>


</head>

<body>
	<div class="form-search search">
			<a class="lp_title" href="index.jsp"><img alt="Link Pattern" src="icon/lp2.png"></a>
            <input id="query" type="text" style="width:40%; margin-left:5px;" size="50" value="" placeholder="Enter a URI."  onblur="if(this.value == '') {this.style.color='#AAAAAA'}" onfocus="this.style.color='#333333'" class ="itext default" name="query">
            <button type="button"  class="ibutton_blue fb"  style="height:30px;" id="searchq">Go</button>
    	<div class="example_div">
    		<strong>Example:</strong>
    		<a class="example_a" href="topk.jsp?sp=http://dbpedia.org/resource/Nanjing">Nanjing</a>	
    		<a class="example_a" href="topk.jsp?sp=http://dbpedia.org/resource/Steven_Spielberg">Steven Spielberg</a>
    		<a class="example_a" href="topk.jsp?sp=http://dbpedia.org/resource/Abraham_Lincoln">Abraham Lincoln</a>
    	</div>
<!--     	<div class="example_div">
    		<strong>Task:</strong>
    		<a class="example_a" href="http://localhost:8080/HLinkPattern/bmc3.jsp?sp=http://dbpedia.org/resource/GIMP">1</a>
    		<a class="example_a" href="http://localhost:8080/HLinkPattern/bmc3.jsp?sp=http://dbpedia.org/resource/Wikipedia">2</a>
    		<a class="example_a" href="http://localhost:8080/HLinkPattern/bmc3.jsp?sp=http://dbpedia.org/resource/Brooklyn_Bridge">3</a>
    		<a class="example_a" href="http://localhost:8080/HLinkPattern/bmc3.jsp?sp=http://dbpedia.org/resource/Steven_Spielberg">4</a>
    		<a class="example_a" href="http://localhost:8080/HLinkPattern/bmc3.jsp?sp=http://dbpedia.org/resource/Universal_Studios">5</a>
    		<a class="example_a" href="http://localhost:8080/HLinkPattern/bmc3.jsp?sp=http://dbpedia.org/resource/Abraham_Lincoln">6</a>
    		<a class="example_a" href="http://localhost:8080/HLinkPattern/bmc3.jsp?sp=http://dbpedia.org/resource/John_F._Kennedy">7</a>
    		<a class="example_a" href="http://localhost:8080/HLinkPattern/bmc3.jsp?sp=http://dbpedia.org/resource/Natalie_Portman">8</a>
    		<a class="example_a" href="http://localhost:8080/HLinkPattern/bmc3.jsp?sp=http://dbpedia.org/resource/Garry_Marshall">9</a>
    		<a class="example_a" href="http://localhost:8080/HLinkPattern/bmc3.jsp?sp=http://dbpedia.org/resource/Sydney">10</a>
    		<a class="example_a" href="http://localhost:8080/HLinkPattern/bmc3.jsp?sp=http://dbpedia.org/resource/Los_Angeles">11</a>
    		<a class="example_a" href="http://localhost:8080/HLinkPattern/bmc3.jsp?sp=http://dbpedia.org/resource/Belgium">12</a>
    		
    	</div> -->
    </div>
	<div class="linkpattern">	
		<div class="filterBox itagBox">
		<div class="filter_box"></div>
		</div>
		
		<div class="pin-wrapper" style="float:left">
		<div class="ul_title">Link Patterns</div>
		<ul class="patterns">
		</ul>
		<div id="jstree"></div>
		</div>
		<div class="main-cntr" style="float:left">
		<div style="margin-bottom:40px;"></div>
		<div class="view-content" style="padding:10px;">
		<!-- 	<button id="continue_btn" class="continue_btn">Continue</button> -->
			<ul class="members" style="list-style:none">
			</ul>
		</div>
		
		</div>
	</div>
	<span class="input_uri" style="display:none"></span>
</body>

</html>