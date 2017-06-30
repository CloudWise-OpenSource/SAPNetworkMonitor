/*
    index.mustache模板的js处理文件。
*/
$(function(){

        //获取左侧列表，并生成导航
        getSideBar('监测点');

        $(".dataList td.operate").find("a").on("click",function(){
            var inputArea = $(this).parent().find("input")[0];

            var value = $(inputArea).val();
            $.dialog({
                title: '监测点详情',
                content: ''+value,
            });

        });


});