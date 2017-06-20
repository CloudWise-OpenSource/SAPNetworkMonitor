
$(document).ready(function(){

	$.extend( $.fn.dataTable.defaults, {
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
	});
	
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


	$(".createButton").on("click",function(){
		$(".dataList").hide();
		$(this).hide();
		$(".dataEdit").show();
		$(".returnButton").show();
	});

	$(".returnButton").on("click",function(){
		$(".dataList").show();
		$(this).hide();
		$(".dataEdit").hide();
		$(".createButton").show();
	})

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

});
