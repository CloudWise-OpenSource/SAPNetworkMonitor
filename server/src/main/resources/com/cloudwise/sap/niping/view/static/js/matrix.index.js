/*
    index.mustache模板的js处理文件。
*/
$(function(){

        //获取左侧列表，并生成导航
        getSideBar('监控任务');

        $.extend($.fn.dataTable.defaults, {
            searching: false,
            ordering:  false,
            bFilter:false,
            bLengthChange:false,

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

        //表单处理页面
        function renderTaskForm(context){
            $(context).find("#monitor").multiselect({
                buttonText: function(options, select) {
                    if (options.length === 0) {
                        return '请选择监控点...';
                    }
                    else if (options.length > 4) {
                        return '共选中'+options.length+'个监测点!';
                    }
                    else {
                        var labels = [];
                        options.each(function() {
                            if ($(this).attr('label') !== undefined) {
                                labels.push($(this).attr('label'));
                            }
                            else {
                                labels.push($(this).html());
                            }
                        });
                        return labels.join(', ') + '';
                    }
                }
            });
        }

        //任务列表页面处理函数
        function renderTaskTable(){
            var idToColors = ['gray','green','yellow','red'];
            //根据任务状态设置不同显示颜色
            $(".dataList td.taskName").find("span").each(function(){
               var colorId = parseInt($(this).data("colorid"));
               if(colorId>=0){
                    $(this).addClass(idToColors[colorId]);
               }
            })

           /* $(".dataList td.operate").find('[data-toggle="switch"]').each(function(){
                $(this).bootstrapSwitch();
            })*/
            //显示启动/暂停按钮
            /*$(".dataList td.operate").find("a.enableTask").each(function(){
                  var statusId = parseInt($(this).data("status"));
                  if(statusId == 0){
                    $(this).attr("title","启动");
                    $(this).find("i").addClass("icon-play-circle");
                  }else{
                     $(this).attr("title","暂停");
                     $(this).find("i").addClass("icon-ban-circle");
                  }
            });*/
            $(".dataList td.operate").find("input.enableTask").each(function(){
                  var statusId = parseInt($(this).data("status"));
                  if(statusId == 0){
                     $(this).bootstrapSwitch({state:false});
                  }else{
                     $(this).bootstrapSwitch({state:true});
                  }
            });

            //显示提示框
            $(".dataList a.tip-bottom").tooltip({ placement: 'bottom' });
        }

        //获取任务列表的函数
        function getTaskList(callback){

            //默认获取一下获取任务列表
            $.get(ENV.API.GET_TASK_LIST,function(tasks){
                if(tasks && tasks.length){
                     //清除datatable并重新绘制。
                    if ($('.data-table').hasClass('dataTable')) {
                    　　var dttable = $('.data-table').dataTable();
                    　　dttable.fnClearTable(); //清空一下table
                    　　dttable.fnDestroy(); //还原初始化了的datatable
                    }

                    $(".dataList table tbody").html(tasks);

                    //处理列表内容。
                    renderTaskTable();

                    //重新调用dataTable渲染一下。
                    $('.data-table').dataTable(tableOptions);

                    if(callback) callback();
                }
                else {
                     if ($('.data-table').hasClass('dataTable')) {
                    　　var dttable = $('.data-table').dataTable();
                    　　dttable.fnClearTable(); //清空一下table
                     }
                }
            })
        }

        //默认加载后的首次获取任务列表
        getTaskList();



        //=========处理页面的请求===========

        //创建任务界面。
        $(".createButton").on("click",function(){
            var self = this;
            //此处应该获取一个模板文件,里面的monitor需要后台获取。
            $.get(ENV.API.ADD_TASK,function(addtemp){
                $(".dataList").hide();
                $(self).hide();
                $(".dataEdit").show();
                $(".returnButton").show();
                $(".formPanel").html(addtemp);
                //处理一下表单页面。
                renderTaskForm($(".formPanel"));
            });

        });

        //返回任务列表
        $(".returnButton").on("click",function(){

            $(".dataList").show();
            $(this).hide();
            $(".dataEdit").hide();
            $(".createButton").show();

            //发起请求重新获取页面的内容.
            getTaskList();

        });

        //保存任务的公共处理函数
        function saveTask(context){

              var postObj = {};
              var queryParams = $(context).serialize();
              queryParams = decodeURIComponent(queryParams).split("&");

              if(queryParams && queryParams.length){
                      queryParams.forEach(function(item){
                          if(item){
                              var tmp = item.split("=");
                              //如果对象中已经有此对象，则将其改为拼接字符串
                              if($.inArray(tmp[0] ,Object.keys(postObj)) >= 0){
                                    postObj[tmp[0]] = postObj[tmp[0]] + "," + tmp[1];
                              }else{
                                  if(tmp && tmp[0].indexOf(".")>=0){
                                      var subItems = tmp[0].split(".");
                                      var length = subItems.length;

                                      var obj = {};
                                      obj[subItems[1]] = tmp[1];
                                      postObj[subItems[0]] = obj;

                                  }else{
                                      postObj[tmp[0]] = tmp[1];
                                  }
                              }

                          }
                      });
              }

              $.ajax({
                  url:ENV.API.SAVE_TASK,
                  type:'post',
                  contentType:'application/json',
                  data:JSON.stringify(postObj),
                  success:function(res){
                    if(res && res.code == 1000){
                        $(".returnButton").click();
                    }
                  }
              });
        }
        //添加任务到后端

        $(".formPanel").on("click",".saveTask",function(){
              event.preventDefault();
              //提交表单
              var context = $(".formPanel").find("form");
              saveTask(context);
        });

         //编辑任务
        $(".dataList").on("click",".editTask",function(){
            var taskUrl = $(this).data("href");
            $.get(ENV.API.GET_TASK + taskUrl,function(editForm){
                $(".formPanel").html(editForm);
                //处理一下表单页面。
                renderTaskForm($(".formPanel"));

                $(".dataList").hide();
                $(".createButton").hide();
                //默认设置下拉的值。
                $(".formPanel").find("select#interval").each(function(){
                    $(this).val(parseInt($(this).data("interval")));
                });
                //默认选中的multiSelect
                var monitors = '' + $(".formPanel").find("select#monitor").data("monitorids");
                if(monitors){
                    var monitorlist = monitors.split(",");
                    var targetSelect = $(".formPanel").find("select#monitor");
                    monitorlist.forEach(function(monitor){
                        $(targetSelect).multiselect('select', monitor);
                    })
                }

                $(".dataEdit").show();
                $(".returnButton").show();

            });
            return false;
        });

        //删除任务
        $(".dataList").on("click",".deleteTask", function(){

            var queryUrl = $(this).data("href");

            $.confirm({
                columnClass: 'span4 confirmbtn',
                title: '删除任务提示',
                content: '确定需要删除当前任务么?',
                buttons:{
                    close:{
                        keys: ['c'],
                        text:'取消',
                        btnClass:'btn btn-danger',
                    },
                    ok:{
                        keys: ['y'],
                        text:'确定',
                        btnClass:'btn btn-info',
                        action: function () {
                            //发起启动/暂停请求
                            $.ajax({
                                url: ENV.API.DELETE_TASK + queryUrl,
                                type:'delete',
                                success:function(res){
                                    if(res && res.code == 1000){
                                        //重新获取数据列表
                                        getTaskList();
                                    }
                                },
                                error:function(data){

                                }
                            })
                        }
                    }
                },
                offsetTop:50,
            });
            return false;
        });

        //启动，暂停任务
        $(".dataList").on("click", ".bootstrap-switch", function(){

            var input = $(this).find("input")[0];
            var status = parseInt($(input).data("status")), queryUrl = $(input).data("href");
            var toggleName = '', queryStatus = '';
            if(status == 0){
                toggleName = '启动';
                queryStatus = 'enable';
            }else{
                toggleName = '暂停';
                queryStatus = 'disable';
            }

            //发起启动/暂停请求
            $.ajax({
                url: ENV.API.PUT_TASK + queryUrl +'/'+ queryStatus,
                type:'put',
                success:function(res){
                    if(res && res.code == 1000){
                        //重新获取数据列表
                        getTaskList();
                    }
                },
                error:function(data){

                }
            });

        });

});