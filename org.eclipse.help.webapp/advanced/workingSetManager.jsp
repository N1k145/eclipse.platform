<%--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
--%>
<%@ include file="header.jsp"%>

<% 
	WorkingSetManagerData data = new WorkingSetManagerData(application, request);
	WebappPreferences prefs = data.getPrefs();
%>


<html>
<head>
<title><%=ServletResources.getString("SelectWorkingSet", request)%></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">


<style type="text/css">
<%@ include file="list.css"%>
</style>

<style type="text/css">
BODY {
	background-color: <%=prefs.getToolbarBackground()%>;
}

TABLE {
	width:auto;
}

TD, TR {
	margin:0px;
	padding:0px;
	border:0px;
}


#workingSetContainer {
	background:Window;
	border:1px solid ThreeDShadow;
	margin:0px 5px;
	padding:5px;
	overflow:auto;
}

.button {
	font:icon;
	border:1px solid #ffffff;
	margin:0px;
	padding:0px;
}

</style>

<script language="JavaScript" src="list.js"></script>
<script language="JavaScript">

function highlightHandler()
{
	document.getElementById('selectws').checked = true;
	enableButtons();
}

// register handler
_highlightHandler = highlightHandler;

function onloadHandler() {
	sizeButtons();
	enableButtons();
}

function sizeButtons() {
	var minWidth=50;
	if(document.getElementById("ok").width < minWidth){
		document.getElementById("ok").width = minWidth;
	}
	if(document.getElementById("cancel").width < minWidth){
		document.getElementById("cancel").width = minWidth;
	}
	if(document.getElementById("edit").width < minWidth){
		document.getElementById("edit").width = minWidth;
	}
	if(document.getElementById("remove").width < minWidth){
		document.getElementById("remove").width = minWidth;
	}
	if(document.getElementById("new").width < minWidth){
		document.getElementById("new").width = minWidth;
	}
}

function enableButtons() {
	if (document.getElementById('selectws').checked){
		document.getElementById("edit").disabled = (active == null);
		document.getElementById("remove").disabled = (active == null);
		document.getElementById("ok").disabled = (active == null);	
	} else {
		document.getElementById("edit").disabled = true;
		document.getElementById("remove").disabled = true;
		document.getElementById("ok").disabled = false;
	}
}

function selectRadio(radioId) {
	if (!(document.getElementById(radioId).checked)){
		document.getElementById(radioId).click();
	}
}

function getWorkingSet()
{
	if (active != null && document.getElementById("selectws").checked)
		return active.title;
	else
		return "";
}


function selectWorkingSet() {
	var workingSet = getWorkingSet();

	var search = window.opener.location.search;
	if (search && search.length > 0) {
		var i = search.indexOf("workingSet=");
		if (i >= 0)
			search = search.substring(0, i);
		else
			search += "&";
	} else {
		search = "?";
	}

	search += "workingSet=" + workingSet;

	window.opener.location.replace(
		window.opener.location.protocol +
		"//" +
		window.opener.location.host + 
		window.opener.location.pathname +
		search);

 	window.close();
}

function removeWorkingSet() {
	window.location.replace("workingSetManager.jsp?operation=remove&workingSet="+getWorkingSet());
}

var workingSetDialog;
var w = 300;
var h = 500;

function newWorkingSet() {
	workingSetDialog = window.open("workingSet.jsp?operation=add&workingSet="+getWorkingSet(), "workingSetDialog", "resizeable=no,height="+h+",width="+w );
	workingSetDialog.focus(); 
}

function editWorkingSet() {
	workingSetDialog = window.open("workingSet.jsp?operation=edit&workingSet="+getWorkingSet(), "workingSetDialog", "resizeable=no,height="+h+",width="+w );
	workingSetDialog.focus(); 
}

function closeWorkingSetDialog()
{
	try {
		if (workingSetDialog)
			workingSetDialog.close();
	}
	catch(e) {}
}

</script>

</head>

<body onload="onloadHandler()" onunload="closeWorkingSetDialog()">
<form>
<div style="overflow:auto;height:250px;width:100%;">
  	<table id="filterTable" cellspacing=0 cellpading=0 border=0 align=center  style="background:<%=prefs.getToolbarBackground()%>;margin-top:5px;width:100%;">
		<tr><td onclick="selectRadio('alldocs')">
			<input id="alldocs" type="radio" name="workingSet" onclick="enableButtons()"><%=ServletResources.getString("All", request)%>
		</td></tr>
		<tr><td onclick="selectRadio('selectws')">
			<input id="selectws" type="radio" name="workingSet"  onclick="enableButtons()"><%=ServletResources.getString("selectWorkingSet", request)%>:		
		</td></tr>
		<tr><td>
			<div id="workingSetContainer" style="overflow:auto; height:150px;">

<table id='list'  cellspacing='0' style="width:100%;">
<% 
String[] wsets = data.getWorkingSets();
String workingSetId = "";
for (int i=0; i<wsets.length; i++)
{
	if (data.isCurrentWorkingSet(i))
		workingSetId = "a" + i;
%>
<tr class='list' id='r<%=i%>' style="width:100%;">
	<td align='left' class='label' nowrap style="width:100%; padding-left:5px;">
		<a id='a<%=i%>' 
		   href='#' 
		   onclick="active=this;highlightHandler()"
   		   ondblclick="selectWorkingSet()"
		   title="<%=wsets[i]%>">
		   <%=wsets[i]%>
		 </a>
	</td>
</tr>

<%
}		
%>

</table>
			</div>
		</td></tr>
		<tr id="actionsTable" valign="bottom"><td>
  			<table cellspacing=10 cellpading=0 border=0 style="background:transparent;">
				<tr>
					<td style="border:1px solid WindowText; padding:0px; margin:0px;">
						<input class='button'  type="button" onclick="newWorkingSet()" value='<%=ServletResources.getString("NewWorkingSetButton", request)%>...'  id="new" alt='<%=ServletResources.getString("NewWorkingSetButton", request)%>'>
					</td>
					<td style="border:1px solid WindowText; padding:0px; margin:0px;">
					  	<input class='button' type="button" onclick="editWorkingSet()" value='<%=ServletResources.getString("EditWorkingSetButton", request)%>...'  id="edit" disabled='<%=data.getWorkingSet() == null ?"true":"false"%>' alt='<%=ServletResources.getString("EditWorkingSetButton", request)%>'>
					</td>
					<td style="border:1px solid WindowText; padding:0px; margin:0px;">
					  	<input class='button' type="button" onclick="removeWorkingSet()" value='<%=ServletResources.getString("RemoveWorkingSetButton", request)%>'  id="remove" disabled='<%=data.getWorkingSet() == null ?"true":"false"%>' alt='<%=ServletResources.getString("RemoveWorkingSetButton", request)%>'>
					</td>
				</tr>
  			</table>
		</td></tr>
	</table>
</div>
<div style="height:50px;">
	<table valign="bottom" align="right" style="background:<%=prefs.getToolbarBackground()%>">
		<tr id="buttonsTable" valign="bottom"><td valign="bottom" align="right">
  			<table cellspacing=10 cellpading=0 border=0 align=right  style="background:transparent;">
				<tr>
					<td style="border:1px solid WindowText; padding:0px; margin:0px;">
						<input class='button' type="button" onclick="selectWorkingSet()" value='<%=ServletResources.getString("OK", request)%>' id="ok" alt='<%=ServletResources.getString("OK", request)%>'>
					</td>
					<td style="border:1px solid WindowText; padding:0px; margin:0px;">
					  	<input class='button' type="button" onclick="window.close()" value='<%=ServletResources.getString("Cancel", request)%>' id="cancel" alt='<%=ServletResources.getString("Cancel", request)%>'>
					</td>
				</tr>
  			</table>
		</td></tr>
	</table>
</div>
</form>
<script language="JavaScript">
	var selected = selectTopicById('<%=workingSetId%>');
	if (!selected)
		document.getElementById("alldocs").checked = true;
	else
		document.getElementById("selectws").checked = true;
		
</script>

</body>
</html>
