/**
 * Created by Raomengnan on 2015/12/5.
 */

function getLocalDate(){
    var mDate = new Date();
    var date = mDate.getFullYear()+"-"+mDate.getMonth()+"-"+mDate.getDate()+" "+mDate.getHours()+":"+mDate.getMinutes()+":"+mDate.getSeconds();
    return date;
}

function getLocalTime(){
    var mDate = new Date();
    var time = mDate.getHours()+":"+mDate.getMinutes()+":"+mDate.getSeconds();
    return time;
}

function timeStamp2Str(timeSta){

	var date = new Date(timeSta)
	var month = date.getMonth()+1>=10?date.getMonth()+1:("0"+(date.getMonth()+1))
	var day = date.getDate()>=10?date.getDate():("0"+date.getDate())
	var hour = date.getHours()>=10?date.getHours():("0"+date.getHours())
	var minute = date.getMinutes()>=10?date.getMinutes():("0"+date.getMinutes())
	var second = date.getSeconds()>=10?date.getSeconds():("0"+date.getSeconds())
	var dateStr = date.getFullYear()+"-"+month+"-"+day +" " + hour+":"+minute+":"+second;
	return dateStr;
}

function timeStamp2DataStr(timeSta) {
	var date = new Date(timeSta)
	var day = date.getDate()>=10?date.getDate():("0"+date.getDate())
	var month = date.getMonth()+1>=10?date.getMonth()+1:("0"+(date.getMonth()+1))
	var dateStr = date.getFullYear()+"-"+month+"-"+day
	return dateStr
}

function myDateFormat(timeStr){
	arr=timeStr.split(" ");
	if(arr.length == 2 && arr[1].split(":").length==3){
		if(arr[1].split(".").length > 1){
			timeArr=arr[1].split(".");
			time = timeArr[0];
			date1=arr[0]+" "+time;
			return date1;
		}
		else return timeStr;
	}
	else if(arr.length == 1){
		date2=arr[0] + " " + getLocalTime();
		return date2;
	}
	return -1;
	
}

function diffTime(startDate,endDate) {
	var diff=endDate.getTime() - startDate.getTime();//时间差的毫秒数  

	//计算出相差天数  
	var days=Math.floor(diff/(24*3600*1000));

	//计算出小时数  
	var leave1=diff%(24*3600*1000);    //计算天数后剩余的毫秒数  
	var hours=Math.floor(leave1/(3600*1000));
	//计算相差分钟数  
	var leave2=leave1%(3600*1000);        //计算小时数后剩余的毫秒数  
	var minutes=Math.floor(leave2/(60*1000));

	//计算相差秒数  
	var leave3=leave2%(60*1000);      //计算分钟数后剩余的毫秒数  
	var seconds=Math.round(leave3/1000);

	var returnStr = seconds;
	if(minutes>0) {
		returnStr = minutes + ":" + returnStr;
	}
	if(hours>0) {
		returnStr = hours + ":" + returnStr;
	}
	if(days>0) {
		returnStr = days + "D " + returnStr;
	}
	return returnStr;
}

(function($){
	$.getUrlParam = function(name)
	{
		var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r!=null) return unescape(r[2]); return null;
	}
})(jQuery);

