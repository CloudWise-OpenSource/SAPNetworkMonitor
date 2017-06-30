//===============公共函数库==============

/***
 * 将params转为 a=b&c=d 格式
 * @param params
 */
function parseParamsToUrl(params){

    var queryParam = null;
    if(params){
        var keys = Object.keys(params);

        keys.forEach(function(key){
            queryParam = queryParam ? (queryParam + '&' + key + "=" + params[key]) : (key + "=" + params[key]);
        });
    }
    return queryParam;
}

/**
*   获取侧边栏
*/
function getSideBar(name){
    name = name ? name : '';
    $.get("/static/sidebar.mustache",function(template){
        $('#sidebar').append(template);

        var curPageName = name ? name : '';
        $("#sidebar").children("a").each(function(){
            $(this).append(curPageName);
        });
        $("#sidebar").find("li").each(function(){
            if($(this).find("span").html() == curPageName){
                $(this).addClass("active");
            }

            var href = $(this).find("a").attr("href");
            $("#breadcrumb").find("a.current").css("href",href).html(curPageName);
        });
    })
}

/**
    判断是否为空
*/
function isEmpty(val){
    if( val == null || val == undefined ) return true;
    else return false;
}


/**
    判断是否为对象
*/
function isObject(obj){
    return !(Object.prototype.toString.call(obj) !== '[object Object]' || obj instanceof Window);
}

/**
 * 判断是否是函数
 *
 * @param {*} obj 要判断的对象
 * @return {boolean} 是否是函数
 */
function isFunction(obj) {
    return typeof obj === 'function';
}

/**
 * 判断是否是空对象或空数组
 *
 * @param {Object|Array} obj 要判断的对象或数组
 * @return {boolean} 是否是空对象或空数组
 */
function isEmptyObject(obj) {
    for (var name in obj) {
        if (obj.hasOwnProperty(name)) {
            return false;
        }

    }

    return true;
}