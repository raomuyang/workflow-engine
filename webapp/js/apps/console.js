/**
 * Created by raomengnan on 17-1-22.
 */
var domain = "http://127.0.0.1:8082"

var INSTANCE_COUNT = "/instance/count/workflow/{workflowid}"
var INSTANCE_FINISHED = "/instance/count/workflow/{workflowid}/finished"
var INSTANCES = "/instance/workflow/{workflowid}"
var ERRORS_COUNT = "/logs/count/workflow/{workflowid}"
var WORKFLOW_INFO = "/workflow/{workflowid}"
var ERRORS = "/logs/workflow/{workflowid}"
var WORKFLOWS = "/workflow/"

$(document).ready( function(){
    var workflow = $.getUrlParam("workflow")
    $("#wf-instances").attr("href", "instances.html?workflow="+workflow)
    $("#wf-log").attr("href","logs.html?workflow="+workflow)

    initErrorCalendar()
    initData()
    initTimeLine()
    initWorkflow()
});

function initData() {
    var workflow = $.getUrlParam("workflow")

    $.ajax({
        url:domain + ERRORS_COUNT.replace("{workflowid}", workflow),
        type:"GET",
        success:function (response) {
            $("#bugs").html(response)
        },
        error:function (response) {
            console.log(response)
        }
    })

    $.ajax({
        url:domain + INSTANCE_COUNT.replace("{workflowid}", workflow),
        type:"GET",
        success:function (response) {
            $("#instances").html(response)
        },
        error:function (response) {
            console.log(response)
        }
    })

    $.ajax({
        url:domain + INSTANCE_FINISHED.replace("{workflowid}", workflow),
        type:"GET",
        success:function (response) {
            $("#finished").html(response)
        },
        error:function (response) {
            console.log(response)
        }
    })

    $.ajax({
        url:domain + WORKFLOW_INFO.replace("{workflowid}", workflow),
        type:"GET",
        success:function (response) {
            var $div = '<div id="workflow" />'
            $("#values").append($div)
            $("#workflow").val(response)

            var stopTime = response["stopTime"]
            $("#wf-id").html(response["id"])
            $("#app-id").html(response["application"])

            $("#exit").html(timeStamp2DataStr(response["stopTime"]))
        },
        error:function (response) {
            console.log(response)
        }
    })
    
}
function initFlot(instances) {
    var date = new Date()
    var y = date.getFullYear()
    var m = (date.getMonth()+1)
    var d = date.getDate()

    var instanceCount = [];
    var count= [0,0,0,0,0,0,0]
    instances.forEach(function (e) {
        var date2 = new Date(e["createTime"])
        if(date2.getFullYear() == y && date2.getMonth() + 1 == m){
            var diff = d - date2.getDate()
            if(diff >= 0 && diff <7)
                count[6-diff] = count[6-diff] + 1
        }
    })

    for(var i = 0; i < 7; i++)
        instanceCount.push([i, count[i]])


    var x_ticks = []
    for(var i = 0; i < 7; i ++)
        x_ticks.push([i, y + "/" + m + "/" + (d - (6-i))])


    var options = {
        series: {
            lines: {
                show: true,
                    lineWidth: 2,
                    fill: true,
                    fillColor: {
                    colors: [{
                        opacity: 0.0
                    }, {
                        opacity: 0.2
                    }]
                }
            },
            points: {
                radius: 5,
                    show: true
            },
            grow: {
                active: true,
                    steps: 50
            },
            shadowSize: 2
        },
        grid: {
            hoverable: true,
                clickable: true,
                tickColor: "#f0f0f0",
                borderWidth: 1,
                color: '#f0f0f0'
        },
        colors: ["#65bd77"],

        xaxis:{
            ticks:x_ticks
    },
        yaxis: {
            ticks: 0
        },
        tooltip: true,
            tooltipOpts: {
        content: "instances create in %x numbers is %y.4",
            defaultTheme: false,
            shifts: {
            x: 0,
                y: 2
        }
    }
    }


    $.plot($("#flot-1ine"), [{data: instanceCount}],options);
}

function initErrorCalendar() {
    var cTime = new Date()

    var workflow = $.getUrlParam("workflow")

    var events = []
    $.ajax({
        url:domain + ERRORS.replace("{workflowid}", workflow),
        type:"GET",
        success:function (response) {
            response.forEach(function (e) {
                var d = new Date(e["date"])
                events.push([
                    d.getDate()+ "/" + (d.getMonth()+1)+"/" + d.getFullYear() ,
                    'Exception',
                    '#',
                    '#fb6b5b',
                    e["msg"]])
                
            })

            var theMonths = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

            var theDays = ["S", "M", "T", "W", "T", "F", "S"];

            $('#calendar').calendar({
                months: theMonths,
                days: theDays,
                events: events,
                popover_options:{
                    placement: 'top',
                    html: true
                }
            });
        },
        error:function (response) {
            console.log(response)
        }
    })

}

function initTimeLine() {
    var workflow = $.getUrlParam("workflow")
    $.ajax({
        url:domain + INSTANCES.replace("{workflowid}", workflow),
        type:"GET",
        success:function (response) {
            if($("#instances-values").length == 0){
                var $tmp = '<div id="instances-values"/>'
                $("#values").append($tmp)
            }


            var $span_finished = '<span class="fa-stack pull-left m-l-xs"> <i class="fa fa-circle text-success fa-stack-2x"></i> <i class="fa fa-check text-white fa-stack-1x"></i> </span>'
            var $span_run = '<span class="fa-stack pull-left m-l-xs"> <i class="fa fa-circle text-info fa-stack-2x"></i> <i class="fa fa-play-circle text-white fa-stack-1x"></i> </span>'
            var $span_wait = '<span class="fa-stack pull-left m-l-xs"> <i class="fa fa-circle text-info fa-stack-2x"></i> <i class="fa fa-coffee text-white fa-stack-1x"></i> </span>'
            var $span_warning = '<span class="fa-stack pull-left m-l-xs"> <i class="fa fa-circle text-danger fa-stack-2x"></i> <i class="fa fa-warning text-white fa-stack-1x"></i> </span>'

            var $i_tmp =
                '<article id="{id}" class="comment-item">'+
                    '{span}'+
                    '<section class="comment-body m-b-lg">'+
                        '<header> create new instance <a href="' +
                            domain + '/instance/all-details/' + '{id}" ' +
                            'class="text-info">{id}</a>'+
                             '<span class="text-muted text-xs"> {date} </span>'+
                        '</header>'+
                    '</section>'+
                '</article>'
            response.forEach(function (e) {
                var date = timeStamp2DataStr(e['createTime'])
                var status = e["status"]
                var $span = $span_warning
                
                if(status == "completed")
                    $span = $span_finished
                else if(status == "running")
                    $span = $span_run
                else if(status == "created")
                    $span = $span_wait

                var $t = $i_tmp.replace("{span}", $span)
                    .replace(/{id}/g, e["instanceId"])
                    .replace("{date}", date)

                $("#in-list").append($t)

                initFlot(response)
            })
        },
        error:function (response) {
            console.log(response)
        }
    })
}

function initWorkflow() {
    var $tmp = '<li><a href="console.html?workflow={id}">{id}</a></li>'

    var workflow = $.getUrlParam("workflow")
    $.ajax({
        url:domain + WORKFLOWS.replace("{workflowid}", workflow),
        type:"GET",
        success:function (response) {
            response.forEach(function (e) {
                var $t = $tmp.replace(/{id}/g,e)
                $('#wf-list').append($t)
            })
        },
        error:function (response) {
            console.log(response)
        }
    })
}

(function($){
    $.getUrlParam = function(name)
    {
        var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r!=null) return unescape(r[2]); return null;
        }
    })(jQuery);