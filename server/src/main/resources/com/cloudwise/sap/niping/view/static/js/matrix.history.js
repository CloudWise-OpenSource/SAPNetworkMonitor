/**
 * Name:
 * Created by authur on 17/6/19.
 */

$(function(){

    /**
     * 各类型数据准备
     */
        //响应时间类型数据  生成1分钟间隔的 最近6个小时内的数据
        var responseTimeData = [], bandWidthData = [], usable = [];
        var date = Date.now();
        for(var i=0,length=60*6;i<length;i++){
            responseTimeData.push([date-i*60*1000,Math.round(Math.random() * 3000+100)]);
            //给带宽一半的点.
            if(i % 2 == 0){
                bandWidthData.push([date-i*60*1000,Math.round(Math.random() * 10000+100)]);
            }
        }
        responseTimeData.reverse();
        bandWidthData.reverse();

        usable.push({name:'可用',value:2000});
        usable.push({name:'不可用',value:300});

        //监测历史列表
        var monitorHistoryData = [];
        var nameCategory = ['稳定性监测','超时监测','时延监测'];
        var historyusable = ['可用','不可用'];

        function getHistoryData(length){
            var date = Date.now();
            var tmpDate = null;
            for(var j=0;j<length;j++){
                tmpDate = new Date(date - j * 1000);
                var time = tmpDate.getHours() + ":" + tmpDate.getMinutes()+":"+tmpDate.getSeconds();
                monitorHistoryData.push({
                    time: time,
                    name:nameCategory[j%3],
                    responseTime:Math.floor(Math.random()*3000 + 200),
                    usable:j%2,
                    errorCode:Math.floor(Math.random()*100 + 400),
                    bandWidth:Math.floor(Math.random()*10000 + 1000)
                });
            }
        }
        getHistoryData(30);
    //============================以上为模拟数据准备工作.=============================
    //响应时间图表
    var reponseTimeInstance = echarts.init(document.getElementById('responseTime'));
    var responseTimeOptions = {
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
            data: responseTimeData.map(function(item){
                var dateStr = new Date(item[0]);
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
            data: responseTimeData.map(function(item){
                return item[1];
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
    reponseTimeInstance.setOption(responseTimeOptions);

    //带宽图表
    var bandWidthInstance = echarts.init(document.getElementById('bandWidth'));
    var bandWidthOptions = {
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
            data: bandWidthData.map(function(item){
                var dateStr = new Date(item[0]);
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
            data: bandWidthData.map(function(item){
                return item[1];
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
    bandWidthInstance.setOption(bandWidthOptions);

    //可用性分析
    var usableInstance = echarts.init(document.getElementById('usable'));
    var usableOptions = {
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
            data: usable,
            radius:['60%','80%'],
            lineStyle:{
                normal:{
                    width:1,
                    color:'green'
                }
            },
        }]
    }
    usableInstance.setOption(usableOptions);

    //稳定性
    var stabilityInstance = echarts.init(document.getElementById('stability'));
    var stabilityOptions = {
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
            data: usable,
            radius:['60%','80%'],
            lineStyle:{
                normal:{
                    width:1,
                    color:'green'
                }
            },
        }]
    }
    stabilityInstance.setOption(stabilityOptions);

    //超时
    var timeoutInstance = echarts.init(document.getElementById('timeout'));
    var timeoutOptions = {
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
            data: usable,
            radius:['60%','80%'],
            lineStyle:{
                normal:{
                    width:1,
                    color:'green'
                }
            },
        }]
    }
    timeoutInstance.setOption(timeoutOptions);


    //监测历史列表
    monitorHistoryData.forEach(function(item){
        var tr = $("<tr class='data'></tr>");
        tr.append("<td>"+item.time+"</td>");
        tr.append("<td class='name'>"+item.name+"</td>");
        tr.append("<td>"+item.responseTime+"</td>");
        if(item.usable){
            tr.append("<td class='red'>"+historyusable[item.usable]+"</td>");
        }else{
            tr.append("<td class='green'>"+historyusable[item.usable]+"</td>");
        }

        tr.append("<td>"+item.errorCode+"</td>");
        tr.append("<td>"+item.bandWidth+"</td>");

        $(".monitorHistory").find("tbody").append(tr);
    });

    var datatablesInstance = $('.historytable').dataTable({
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
    });
    $("#historyCategory").on("change",function(){
        var filterValue = $(this).val();
        if(!filterValue || filterValue=='all'){
            $(".historytable").find("tr.data").show();
        }else{
            $(".historytable").find("tr.data").each(function(){
                if($(this).find("td.name").text()==filterValue){
                    $(this).show();
                }else{
                    $(this).hide();
                }
            })
        }

    });

    //国家\省\市联动

    var s=["s1","s2","s3"];
    var opt0 = ["国家","省份","城市"];

    function change(v){
        var str="0";
        for(i=0;i<v;i++){ str+=("_"+(document.getElementById(s[i]).selectedIndex-1));};
        var ss=document.getElementById(s[v]);
        with(ss){
            length = 0;
            options[0]=new Option(opt0[v],opt0[v]);
            if(v && document.getElementById(s[v-1]).selectedIndex>0 || !v)
            {
                if(dsy.Exists(str)){
                    ar = dsy.Items[str];
                    for(i=0;i<ar.length;i++)options[length]=new Option(ar[i],ar[i]);
                    if(v)options[1].selected = true;
                }
            }
            if(++v<s.length){change(v);}
        }
    }

    function setup()
    {
        for(i=0;i<s.length-1;i++){
            document.getElementById(s[i]).onchange=(function(v){
                console.log(v);
                change(v);
            })(i);

        }
        change(0);
    }

    setup();
});