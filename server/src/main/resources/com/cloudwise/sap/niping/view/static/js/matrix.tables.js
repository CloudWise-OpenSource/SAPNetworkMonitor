
$(document).ready(function(){

	/*$.extend( $.fn.dataTable.defaults, {
		searching: false,
		ordering:  false,
		bFilter:false,
		bLengthChange:false,

	} );

	$('.data-table').dataTable({
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
	});*/

	$('input[type=checkbox],input[type=radio],input[type=file]').uniform();

	//$('select').select2();

	$("span.icon input:checkbox, th input:checkbox").click(function() {
		var checkedStatus = this.checked;
		var checkbox = $(this).parents('.widget-box').find('tr td:first-child input:checkbox');
		checkbox.each(function() {
			this.checked = checkedStatus;
			if (checkedStatus == this.checked) {
				$(this).closest('.checker > span').removeClass('checked');
			}
			if (this.checked) {
				$(this).closest('.checker > span').addClass('checked');
			}
		});
	});

	//=========处理页面的请求===========

	/*$(".createButton").on("click",function(){
		$(".dataList").hide();
		$(this).hide();
		//清空表单
		$('.editForm')[0].reset();
		$(".dataEdit").show();
		$(".returnButton").show();
	});

	$(".returnButton").on("click",function(){

		//发起请求重新获取页面的内容.
		$.get("/api/tasks",function(tasks){
			if(tasks && tasks.length){
				$(".dataList table tbody").html(tasks);
			}
		});
		$(".dataList").show();
		$(this).hide();
		$(".dataEdit").hide();
		$(".createButton").show();
	});


	$("#monitor").multiselect({
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

	$(".editTask").on("click",function(){
		var taskUrl = $(this).data("href");
		$.get(taskUrl,function(editForm){
			$(".formPanel").html(editForm);
			$(".dataList").hide();
			$(".createButton").hide();
			$(".dataEdit").show();
			$(".returnButton").show();
		});
		return false;
	});


	$(".deleteTask").on("click",function(){

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
						alert("删除成功!");
					}
				}
			},
			offsetTop:50,
		});
		return false;
	})

	$(".enableTask").on("click",function(){

		$.confirm({
			columnClass: 'span4 confirmbtn',
			title: '启动/暂停任务提示',
			content: '确定需要启动/暂停当前任务么?',
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
						alert("成功!");
					}
				}
			},
			offsetTop:50,
		});
		return false;
	})*/

	$(".logout").on("click",function(){
        $.get("/api/logout",function(tasks){

        });
    });
});