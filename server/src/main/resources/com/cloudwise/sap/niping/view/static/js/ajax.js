/*
    公共的ajax请求方法，需要做以下处理
    如果第一个参数为字符串，则当做url处理，
    如果为对象，则url参数从opts里面获取。

    opts里面需要定义一个回调函数
        {
            callback:function(){},
            type: get/post/put/delete
            contentType:text/html | json
        }
    请求成功后（200）
        如果返回的是一个对象，则按照{code:***,message:****,data:****}处理
        如果是html文本，则直接返回给回调函数。
*/
function ajaxRequest(url, opts){
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