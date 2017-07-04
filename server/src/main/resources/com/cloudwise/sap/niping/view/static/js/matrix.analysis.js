/*
    index.mustache模板的js处理文件。
    create by authur.wang
*/
$(function(){

        //获取左侧列表，并生成导航
        getSideBar('数据分析');

        $.extend($.fn.dataTable.defaults, {
            searching: false,
            ordering:  false,
            bFilter:false,
            bLengthChange:false,
            bInfo:false,
        });

        var tableOptions = {
            "bJQueryUI": true,
            "sPaginationType": "full_numbers",
            "sDom": '<""l>t<"F"fp>',
            "oLanguage": {
                "oPaginate": {
                    "sFirst": "首页",
                    "sPrevious": "前一页",
                    "sNext": "后一页",
                    "sLast": "尾页"
                }
            }
        }


        //任务列表页面处理函数
        function renderAnalysisTable(){
            //设置表格头部
            if(parseInt($("#interval").val())){
                $(".dataList th.usability").html("可用率");
            }else{
                $(".dataList th.usability").html("可用性");
            }

        }

        //获取任务列表的函数
        function getAnalysisList(params, callback){

            //处理请求参数
            params = params || {};
            //拼接为get请求参数.
            var queryParams = parseParamsToUrl(params);
            queryParams = (queryParams && queryParams.length) ? '?'+queryParams : '';

            //默认获取一下获取任务列表
            $.get(ENV.API.GET_ANALYSIS_LIST + queryParams,function(analysis){

                //清除datatable并重新绘制。
                if ($('.data-table').hasClass('dataTable')) {
                　　var dttable = $('.data-table').dataTable();
                　　dttable.fnClearTable(); //清空一下table
                　　dttable.fnDestroy(); //还原初始化了的datatable
                }

                if(analysis && analysis.length){

                    $(".dataList table tbody").html(analysis);
                    //处理列表内容。
                    //renderAnalysisTable();

                    //重新调用dataTable渲染一下。
                    $('.data-table').dataTable(tableOptions);

                    if(callback) callback();
                }
            })
        }

        function getAnalysisListByParams(){
            var taskList = $("#taskList").val();
            var interval = $("#interval").val();
            var params = {};
            if( taskList) {
                params.taskId = taskList;
            }
            //处理表头
            renderAnalysisTable();
            if(interval){
                params.time = interval;
            }
            getAnalysisList(params);
        }

        //默认加载后的首次获取分析列表
        //首先处理一下taskd的选中状态
        var taskId = localStorage.getItem(ENV.storage.task);

        if(!isEmpty(taskId) && taskId.length){
            $("#taskList").find("option").each(function(index,item){
                if($(item).val() == taskId){
                    $("#taskList").val(taskId);
                }
            });
        }
        getAnalysisListByParams();

        $("#taskList").on("change",function(){
                //重置本地的存储taskId
                if($(this).val()){
                    localStorage.setItem(ENV.storage.task, $(this).val());
                }
                getAnalysisListByParams();
        });

        $("#interval").on("change",function(){
                getAnalysisListByParams();
        });

        //查看详情链接点击
        $(".dataList").on("click",".operate a", function(){
            if($(this).data("monitorid")){
                localStorage.setItem(ENV.storage.monitor, $(this).data("monitorid"));
            }
            window.location.href = $(this).attr("href");
        });

});