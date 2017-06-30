/**
 * Name:
 * Created by authur on 17/6/19.
 */

$(function(){

     //获取左侧列表，并生成导航
     getSideBar('历史记录');

    //获取条件列表
    //获取任务
    var getHistoryTask = function(dtd){
　　　　 var dtd = $.Deferred();
        $.get(ENV.API.GET_HISTORY_TASKS,function(res){
              if(res && res.code == 1000){
                    var tasks = res.data || [];
                    tasks.forEach(function(task){
                        $("#taskList").append("<option value="+task.taskId+">"+task.name+"</option>");
                    });

                    dtd.resolve();
              }
        })
　　　　 return dtd.promise(); // 返回promise对象
　　};
    //获取监测点
    var getHistoryMonitors = function(dtd){
　　　　 var dtd = $.Deferred();

        var taskId = $("#taskList").val();

        //获取拼接参数
        var params = {
            country : $("#country").val(),
            province : $("#province").val(),
            city : $("#city").val()
        }
        var queryParams = parseParamsToUrl(params);
        queryParams = (queryParams && queryParams.length) ? '?'+queryParams : '';

        queryParams = decodeURIComponent(queryParams);
        if(!isEmpty(taskId)){
            $.get(ENV.API.GET_HISTORY_MONITORS + taskId + "/monitors" + queryParams,function(res){
                  if(res && res.code == 1000){
                        var monitors = res.data || [];
                        //清空列表
                        $("#monitorList").html('');
                        monitors.forEach(function(monitor){
                            $("#monitorList").append("<option data-ip="+ monitor.ip +" value="+monitor.monitorId+">"+monitor.name+"</option>");
                        });
                        if(monitors && monitors.length){
                            $(".ipInfo").html(monitors[0].ip);
                        }
                        dtd.resolve();
                  }
            })
    　　　　 return dtd.promise(); // 返回promise对象
        }
　　};

    //获取国家  省市
    var getCountry = function(dtd){
        var dtd = $.Deferred();
        $.get(ENV.API.HISTORY + 'country',function(res){
              if(res && res.code == 1000){
                    var countries = res.data || [];
                    countries.forEach(function(country){
                        $("#country").append("<option value="+country+">"+country+"</option>");
                    });

                    dtd.resolve();
              }
        })
　　　　 return dtd.promise(); // 返回promise对象
    }
    //获取省份列表
    var getProvinces = function(country){
        $.get(ENV.API.HISTORY + country + "/province",function(res){
              if(res && res.code == 1000){
                    var provinces = res.data || [];
                    provinces.forEach(function(province){
                        $("#province").append("<option value="+province+">"+province+"</option>");
                    });

              }
        })
    }

    //获取城市列表
    var getCities = function(country, province){
        $.get(ENV.API.HISTORY + country + "/" + province + "/city",function(res){
              if(res && res.code == 1000){
                    var cities = res.data || [];
                    cities.forEach(function(city){
                        $("#city").append("<option value="+city+">"+city+"</option>");
                    });
              }
        })
    }

    //清空下拉列表
    var clearSelect = function(id, defaultValue){
        if(id){
            $(id).html('<option value="">'+defaultValue+'</option>');
        }
    }

    getCountry().then(function(){
         $("#country").on("change",function(){
              var country = $(this).val();
              if(country && country.length){
                   getProvinces(country);
              }else{
                //清空省市列表.
                clearSelect("#province", "省份");
                clearSelect("#city", "城市");
              }
         });


         $("#province").on("change",function(){
               var country = $("#country").val();
               var province = $(this).val();
               if(province && province.length){
                    getCities(country, province);
               }else{
                    //清空城市列表.
                    clearSelect("#city", "城市");
               }
         });
    })

    //获取数据内容
    var getHistoryData = function(dtd){
        var dtd = $.Deferred();

        var taskId = $("#taskList").val();
        var monitorId = $("#monitorList").val();

        if(!isEmpty(taskId) || !isEmpty(monitorId)){
            //获取拼接参数
            var params = {
                time:$("#interval").val(),
//                country : $("#country").val(),
//                province : $("#province").val(),
//                city : $("#city").val()
            }
            var queryParams = parseParamsToUrl(params);
            queryParams = (queryParams && queryParams.length) ? '?'+queryParams : '';

            queryParams = decodeURIComponent(queryParams);

            $.get(ENV.API.GET_HISTORY_DATA + taskId + "/" + monitorId + queryParams,function(res){
                  if(res && res.code == 1000){
                        dtd.resolve(res);
                  }
            })
        }
        return dtd.promise();
    }

    /**
        清空某个图表
    */
    var clearEchart = function(domId){
        if(isEmpty(domId)) return false;

        var dom = document.getElementById(domId.slice(1));
        var instance = echarts.getInstanceByDom(dom);
        if(instance){
            instance.dispose();
        }
    }

    /**
        渲染图表页面
    */
    var renderHistoryData = function(data){
        var domIds = {
            avMetrics:{dom:'#responseTime',option:responseTime},
            trMetrics:{dom:'#bandWidth',option:bandWidth},
            performance:{dom:'#usable',option:performance},
            stability:{dom:'#stability',option:stability},
            idleTimeout:{dom:'#timeout',option:timeout}
        }
        if(isEmpty(data) || !isObject(data)){
            Object.keys(domIds).forEach(function(key){
                clearEchart(domIds[key]['dom']);
                $(domIds[key]['dom']).addClass("chartNoData").html("暂无数据");
            });
        }else{
            Object.keys(data).forEach(function(key){
                //如果数据为空
                if(isEmpty(data[key]) || isEmptyObject(data[key])){
                    clearEchart(domIds[key]['dom']);
                    $(domIds[key]['dom']).addClass("chartNoData").html("暂无数据");
                }else{

                    var curFunc = domIds[key]['option'];
                    var options = isFunction(curFunc) ? curFunc(data[key]) : null;

                    var instance = echarts.getInstanceByDom(document.getElementById(domIds[key]['dom'].slice(1)));
                    //如果有echart实例
                    if( !isEmpty(instance) && options){
                        //$(domIds[key]['dom']).removeClass("chartNoData").html('');

                        instance.setOption(options);

                    }else if(options){
                        //如果没有echart实例 则用options初始化一个
                        $(domIds[key]['dom']).removeClass("chartNoData").html('');
                        instance = echarts.init(document.getElementById(domIds[key]['dom'].slice(1)));

                        instance.setOption(options);

                    }else{
                        //如果没有实例并且没有返回options，则清空显示
                        clearEchart(domIds[key]['dom']);
                        $(domIds[key]['dom']).addClass("chartNoData").html("暂无数据");
                    }
                }
            });
        }
    }



    //任务和监测点加载完成以后，执行请求。
    getHistoryTask().then(function(){
        //获取任务的监测点列表
        getHistoryMonitors().then(function(){
            getHistoryData().then(function(res){
                if(res && res.code == 1000){
                    renderHistoryData(res.data);
                }
            });

            //获取列表数据
            getHistoryMonitorData();
        });
    }).fail(function(){
        alert("query time out!");
    });


    //===============处理条件选择=============

    function flushHistoryData(){
        getHistoryData().then(function(res){
            if(res && res.code == 1000){
                renderHistoryData(res.data);
            }
        });

        //获取列表数据
        getHistoryMonitorData();
    }
    //拼接监测点的响应事件
    $("#monitorList").on("change",function(event){
        var target = event ? event.target.selectedIndex : 0;
        var options = event ? event.target.children : [];
        //更换ip地址信息
        if(options.length){
            $(".ipInfo").html($(options[target]).data("ip") || '');
        }

        flushHistoryData();
    });

    $("#interval").on("change",function(){
        flushHistoryData();
    });

    $("#taskList").on("change",function(){
        flushHistoryData();
    });

    $("#country").on("change",function(){
        getHistoryMonitors();
    });
    $("#province").on("change",function(){
        getHistoryMonitors();
    });
    $("#city").on("change",function(){
        getHistoryMonitors();
    });

    //所有图表的配置文件函数
    //响应时间
    function responseTime(data){
        if(isEmpty(data) || isEmptyObject(data)) return null;

        return {
                color:["#37b4b3","red"],
                title: {
                    show:false
                },
                tooltip: {
                    trigger:'axis',
                    formatter:'{a}:{c}ms'
                },
                legend: {
                    show:false,
                    data:['响应时间']
                },
                grid:{
                    left:60,
                    right:40,
                    top:10,
                    bottom:20
                },
                xAxis: {
                    data: data.map(function(item){
                        var dateStr = new Date(item.time);
                        return dateStr.getHours()+":"+dateStr.getMinutes()+":"+dateStr.getSeconds();
                    }),
                    axisLine:{
                        show:true,
                    },
                    splitLine:{
                        show:false,
                    },
                    axisTick:{
                        show:true,
                    }
                },
                yAxis: {
                    interval:1000,
                    axisLine:{
                        show:true,
                    },
                    splitLine:{
                        show:false,
                    },
                    axisTick:{
                        show:true,
                    },
                    axisLabel:{
                        formatter:"{value}ms"
                    }
                },
                visualMap: {
                    show:false,
                    pieces: [{
                        gt: 0,
                        lte: 2000,
                        color: '#37b4b3',
                    }, {
                        gt: 2000,
                        color: 'red'
                    }],
                    outOfRange: {
                        color: '#999'
                    }
                },
                series: [{
                    name: '响应时间',
                    type: 'line',
                    data: data.map(function(item){
                        return item.value;
                    }),
                    lineStyle:{
                        normal:{
                            width:1,
                        }
                    },
                    markLine:{
                        silent: true,
                        data: [{
                            yAxis: 2000
                        }]
                    }
                }]
            }
    }

    //bandWidth data
    function bandWidth(data){
        if(isEmpty(data) || isEmptyObject(data)) return null;

        return {
                color:["#37b4b3","red"],
                title: {
                    show:false
                },
                tooltip: {
                    trigger:'axis',
                    formatter:'{a}:{c}KB/S'
                },
                legend: {
                    show:false,
                    data:['带宽']
                },
                grid:{
                    left:80,
                    right:40,
                    top:10,
                    bottom:20
                },
                xAxis: {
                    data: data.map(function(item){
                        var dateStr = new Date(item.time);
                        return dateStr.getHours()+":"+dateStr.getMinutes()+":"+dateStr.getSeconds();
                    }),
                    axisLine:{
                        show:true,
                    },
                    splitLine:{
                        show:false,
                    },
                    axisTick:{
                        show:true,
                    }
                },
                yAxis: {
                    axisLine:{
                        show:true,
                    },
                    splitLine:{
                        show:false,
                    },
                    axisTick:{
                        show:true,
                    },
                    axisLabel:{
                        formatter:"{value}KB/S"
                    }
                },
                series: [{
                    name: '带宽',
                    type: 'line',
                    data: data.map(function(item){
                        return item.value;
                    }),
                    lineStyle:{
                        normal:{
                            width:1,
                            color:'#37b4b3'
                        }
                    },
                    markPoint:{
                        show:false,
                    },
                    itemStyle:{
                        normal:{
                            borderColor:'#37b4b3',
                        }
                    }
                }]
            }
    }

    function changeDataToRender(data){
        var mapping = [
        {name:'usable', value:'可用',}
        ,{name:'notUsable',value:'不可用'}]

        if(isEmpty(data) || isEmptyObject(data)) return null;

        var resultData = [];
        Object.keys(data).forEach(function(key){
            var dataName = key;
            mapping.forEach(function(map){
                if(map.name == key){
                      dataName = map.value;
                }
            })
            resultData.push({name:dataName,value:data[key]});
        })

        return resultData;

    }
    //usagable data
    function performance(data){

        var resultData = changeDataToRender(data);
        if(isEmpty(resultData) || isEmptyObject(resultData)) return null;

        return {
                color:["#37b4b3","red"],
                title: {
                    show:false
                },
                tooltip: {
                    trigger:'item',
                },
                legend: {
                    show:false,
                    data:['可用性']
                },
                series: [{
                    name: '可用性',
                    type: 'pie',
                    data: resultData,
                    radius:['55%','75%'],
                    lineStyle:{
                        normal:{
                            width:1,
                            color:'green'
                        }
                    },
                }]
            }
    }
    //usagable data
    function stability(data){
        var resultData = changeDataToRender(data);
        if(isEmpty(resultData) || isEmptyObject(resultData)) return null;

        return {
                   color:["#37b4b3","red"],
                   title: {
                       show:false
                   },
                   tooltip: {
                       trigger:'item',
                   },
                   legend: {
                       show:false,
                       data:['可用性']
                   },
                   series: [{
                       name: '可用性',
                       type: 'pie',
                       data: resultData,
                       radius:['55%','75%'],
                       lineStyle:{
                           normal:{
                               width:1,
                               color:'green'
                           }
                       },
                   }]
               }
    }
    //usagable data
    function timeout(data){
        var resultData = changeDataToRender(data);
        if(isEmpty(resultData) || isEmptyObject(resultData)) return null;

        return {
                   color:["#37b4b3","red"],
                   title: {
                       show:false
                   },
                   tooltip: {
                       trigger:'item',
                   },
                   legend: {
                       show:false,
                       data:['可用性']
                   },
                   series: [{
                       name: '可用性',
                       type: 'pie',
                       data: resultData,
                       radius:['55%','75%'],
                       lineStyle:{
                           normal:{
                               width:1,
                               color:'green'
                           }
                       },
                   }]
               }
    }


    //监测历史列表
    var tableOptions = {
                       "bJQueryUI": false,
                       "bPaginate":false,
                       "searching": false,
                       "ordering":  false,
                       "bFilter":false,
                       "bLengthChange":false,
                       "oLanguage": {//语言设置
                           "sLengthMenu": "",
                           "sZeroRecords": "没有历史数据",
                           "sInfo": "",
                           "sInfoEmtpy": "没有历史数据",
                           "sProcessing": '<i class="fa fa-coffee"></i> 正在加载数据...',
                           "oPaginate": {
                               "sFirst": "首页",
                               "sPrevious": "前一页",
                               "sNext": "后一页",
                               "sLast": "尾页"
                           }
                       }
                   }

    function getHistoryMonitorData(){

        var taskId = $("#taskList").val();
        var monitorId = $("#monitorList").val();

        if(!isEmpty(taskId) || !isEmpty(monitorId)){
            //获取拼接参数
            var params = {
                time:$("#interval").val(),
                type : $("#historyCategory").val(),
            }
            var queryParams = parseParamsToUrl(params);
            queryParams = (queryParams && queryParams.length) ? '?'+queryParams : '';

            queryParams = decodeURIComponent(queryParams);

            $.get(ENV.API.GET_HISTORY_RESULT + taskId + "/" + monitorId + queryParams,function(res){
                  //清除datatable并重新绘制。
                  if ($('.historytable').hasClass('dataTable')) {
                  　　var dttable = $('.historytable').dataTable();
                  　　dttable.fnClearTable(); //清空一下table
                  　　dttable.fnDestroy(); //还原初始化了的datatable
                  }
                  if(res && res.length){
                        $(".monitorHistory").find("table tbody").html(res);
                        $(".historytable tbody").html(res);
                        //重新调用dataTable渲染一下。
                        $('.historytable').dataTable(tableOptions);

                        $(".historytable").find("td.red").on("click","a",function(){
                            var inputArea = $(this).parent().find("input")[0];

                            var value = $(inputArea).val();
                            $.dialog({
                                title: '错误信息',
                                content: ''+value,
                            });

                            return false;
                        });

                  }
            })
        }
    }


    $("#historyCategory").on("change",function(){
        getHistoryMonitorData();
    });


});