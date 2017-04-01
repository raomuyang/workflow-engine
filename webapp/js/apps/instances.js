/**
 * Created by raomengnan on 17-1-24.
 */

var domain = "http://127.0.0.1:8082"

var INSTANCES = "/instance/workflow/{workflowid}"
var WORKFLOW_INFO = "/workflow/{workflowid}"
var WORKFLOWS = "/workflow/"
var CREATE_INSTANCE = "/instance/new/workflow/{workflowid}"

$(document).ready( function(){

    initWorkflow()
    initInfo()
    initTimeLine()
});

function initInfo() {
    var workflow = $.getUrlParam("workflow")

    $.ajax({
        url:domain + WORKFLOW_INFO.replace("{workflowid}", workflow),
        type:"GET",
        success:function (response) {
            var $div = '<div id="workflow" />'
            $("#values").append($div)
            $("#workflow").val(response)

            var startTime = response["startTime"]
            $("#wf-id").html(response["id"])
            $("#app-id").html(response["application"])
            $("#start-time").html(timeStamp2DataStr(startTime))
        },
        error:function (response) {
            console.log(response)
        }
    })
}

function initWorkflow() {
    var $tmp = '<li><a href="instances.html?workflow={id}">{id}</a></li>'

    var workflow = $.getUrlParam("workflow")
    $("#wf-log").attr("href","logs.html?workflow="+workflow)
    $("#wf-console").attr("href","console.html?workflow="+workflow)
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

function initTimeLine() {
    var workflow = $.getUrlParam("workflow")
    $.ajax({
        url:domain + INSTANCES.replace("{workflowid}", workflow),
        type:"GET",
        success:function (response) {

            response.forEach(function (e) {
                addItem(e)
            })

        },
        error:function (response) {
            console.log(response)
        }
    })

}

var alt = "alt"
function addItem(e) {

    $("#foot").remove()

    if($("#instances-values").length == 0){
        var $tmp = '<div id="instances-values"/>'
        $("#values").append($tmp)
    }

    var $tmp =
        '<article class="timeline-item {alt}">'+
        '<div class="timeline-caption">'+
        '<div class="panel panel-default">'+
        '<div class="panel-body"> <span class="arrow {arrow}"></span> ' +
        '<span class="timeline-icon"><i class="fa {icon} time-icon {bg}"></i></span> ' +
        '<span class="timeline-date">{date}</span>'+
        '<h5> <span>Created</span> <a id="instance-id" href="' + domain +
        '/instance/all-details/{id}">{id}</a> </h5>'+
        '<p>{status}</p>'+
        '</div>'+
        '</div>'+
        '</div>'+
        '</article>'

    var icon_finished = 'fa-check-circle'
    var icon_wait = 'fa-coffee'
    var icon_run = 'fa-caret-square-o-right'
    var icon_error = 'fa-bug'
    var icon_expired = "fa-warning"


    var arrow = "right"

    var date = timeStamp2DataStr(e['createTime'])
    var status = e["status"]
    var icon = icon_wait
    var bg = "bg-info"

    if(status == "completed"){
        icon = icon_finished
        bg = "bg-success"
    }
    else if(status == "running")
        icon = icon_run
    else if(status == "exception") {
        icon = icon_error
        bg = "bg-danger"
    }
    else if(status == "expired"){
        icon = icon_expired
        bg = "bg-danger"
    }

    if(alt == "alt"){
        alt = ""
        arrow = "left"
    }
    else{
        arrow = "right"
        alt = "alt"
    }

    var $t = $tmp.replace("{alt}", alt)
        .replace(/{id}/g, e["instanceId"])
        .replace("{date}", date)
        .replace("{status}", status)
        .replace("{icon}", icon)
        .replace("{arrow}", arrow)
        .replace("{bg}", bg)

    $(".timeline").append($t)
    $(".timeline").append('<div class="timeline-footer" id="foot"><a href="#" onclick="createInstance()"><i class="fa fa-plus time-icon inline-block bg-dark"></i></a></div>')
}

function createInstance() {
    var workflow = $.getUrlParam("workflow")
    axios({
        method:"PUT",
        url: domain + CREATE_INSTANCE.replace("{workflowid}", workflow),
        data:{}
    }).then(function (response) {
        if(response.data["status"])
            addItem(response.data["instance"])
        alert("创建新的实例:" + response.data["status"])
    })
}

$(".time_line").on('click',function () {
    createInstance()
})
