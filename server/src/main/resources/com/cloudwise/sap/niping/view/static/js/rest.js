/**
 * Name:
 * Created by authur on 17/6/16.
 */

function empty() {

}
/**
 * @class Response
 */
var Response  = function(){

    this.xhr = xhr;

    /**
     * 获取请求处理码
     * @returns {string}
     */
    function getCode() {
        return this.code;
    }

    /**
     * 获取XHR
     * @returns {Object}
     */
    function getXHR() {
        return this.xhr;
    }

    /**
     * 获取所有响应header头消息  跨域只是allow headers
     * @returns {Object}
     */
    function getHeaders() {
        return this.xhr.getAllResponseHeaders();
    }

    /**
     * 根据name获取响应header头消息   跨域的时候,不允许的头是不能获取的.
     * @param {string} name header名
     * @returns {?string}
     */
    function getHeader(name) {
        return this.xhr.getResponseHeader(name);
    }

    /**
     * 获取HTTP状态码
     * @returns {number}
     */
    function getStatus() {
        return this.xhr.status;
    }

    /**
     * 获取HTTP状态消息
     * @returns {*}
     */
    function getStatusText() {
        return this.xhr.statusText;
    }

    /**
     * 获取原始的响应内容(未格式化)
     * @returns {*|string}
     */
    function getContent() {
        return this.xhr.responseText;
    }

    /**
     * 设置响应结果
     * @param {Object} response
     * @param {string} response.code
     * @param {string=} response.msg
     * @param {*=} response.data
     */
    function setResponse(response) {

        response = response || {};

        this.code = response.code || 'error';
        this.msg = response.msg;
        this.data = response.data;

        return this;
    }

    /**
     * 获取响应结果
     * @returns {{code: (int|string|*), data: *, msg: *}}
     */
    function getResponse() {
        return {
            code: this.code,
            data: this.data,
            msg: this.msg
        }
    }

    /**
     * 是否请求成功(请求过程未发生错误,接口正常返回消息)
     * @returns {boolean}
     */
    function isRequestSuccess() {

        if($.isNumeric(this.code) && this.code > 0) {
            return true;
        }
        return false;
        //return ['serverError', 'error', 'parsererror', 'timeout'].indexOf(this.code) > -1;
    }

    /**
     * 请求是否出现错误
     * @returns {boolean}
     */
    function isRequestError() {
        return !this.isRequestSuccess();
    }

    /**
     * 判断code
     * @param {string} code
     * @returns {boolean}
     */
    function is(code) {

        return this.code === code;
    }

    /**
     * 请求成功,并且获取数据或服务器处理成功
     * @returns {boolean}
     */
    function isSuccess() {
        return this.is('success');
    }

    /**
     * 请求不成功或者服务器处理不成功
     * @returns {boolean}
     */
    function isError() {
        return !this.isSuccess();
    }

    /**
     * 响应结果是否包含数据
     * @returns {boolean}
     */
    function hasData() {
        return this.data !== null && this.data !== undefined && !$.isEmptyObject(this.data);
    }

    /**
     * 响应结果是否包含消息
     * @returns {boolean}
     */
    function hasMsg() {
        return typeof this.msg === 'string' && this.msg !== '';
    }

    /**
     * 获取响应消息
     * @returns {string}
     */
    function getMsg() {

        if (this.hasMsg()) {
            return this.msg;
        }
    }
}


// 后端接口地址
var APIDomain = ( ENV && ENV.DOMAIN) ? ENV.DOMAIN : location.origin;
/**
 * 发起一个符合后端restful规范的ajax请求
 * @param {string=} url
 * @param {Object=} opts
 * @returns {Deferred}
 */
function rest(url, opts) {

    if ($.isPlainObject(url)) {
        opts = url;
        url = undefined;
    }
    opts = opts || {};

    opts.url = url || opts.url || '';

    if (!/^http[s]?:\/\//.test(opts.url)) {
        opts.url = APIDomain + opts.url
    }


    var success = $.isFunction(opts.success) ? opts.success : empty;
    var error = $.isFunction(opts.error) ? opts.error : empty;
    var complete = $.isFunction(opts.complete) ? opts.complete : empty;
    var dataFilter = opts.dataFilter;


    var deferred = $.Deferred();

    var silent = false;

    // 静默处理, 发生错误时忽略错误
    deferred.silent = function (_silent) {
        silent = _silent;
        return deferred;
    };

    function onComplete(xhr, status) {

        var response = new Response(xhr);

        // 请求成功
        if (status === 'success') {

            var jsonResponse = xhr.responseJSON || {};

            // 数据过滤器
            if ($.isFunction(dataFilter)) {

                jsonResponse.data = dataFilter.call(xhr, jsonResponse.data, jsonResponse, xhr);
            }

            response.setResponse(jsonResponse);


        } else {

            response.setResponse({
                code: status || 'serverError'
            });
        }

        var responseData = response.data || null;

        // 服务器处理成功或者获取数据成功
        if (response.isSuccess()) {

            deferred.resolve(response, responseData, xhr);
            // 回调
            success.call(xhr, response, responseData, xhr);

        } else {

            if (!silent) {
                // 非静默状态下, 会自动处理错误
                if (response.is('error')) { //此处error自定义
                    //跳转到登录页面
                    return false;

                } else {
                    // todo 请求发送错误时提示以及静默处理(待完善)
                    //  T.modalAlert(response.getMsg(), 'error');
                }
            }

            deferred.reject(response, responseData, xhr);

            error.call(xhr, response, responseData, xhr);
        }

        complete.call(xhr, response, responseData, xhr);

    }


    // 发送请求
    var ajax = $.ajax($.extend(true, opts, {
        error: empty,
        success: empty,
        complete: onComplete,
        dataFilter: null,
        dataType: 'json',
        deferred: deferred,
        xhrFields: {
            //允许不同源的ajax带有cookie信息
            withCredentials: true
        }
    }));

    // 终端请求
    deferred.abort = function () {
        ajax.abort();
    };

    return deferred.promise();

}

/**
 * 发送一个GET请求
 * @param {string} url URL地址
 * @param {Object=} data URL参数
 * @param {Object=} opts ajax选项
 * @returns {Deferred}
 */
function restGet(url, data, opts) {

    opts = opts || {};

    opts.data = data || {};

    opts.type = 'GET';

    return rest(url, opts);
}

/**
 * 发送一个POST请求
 * @param {string} url URL地址
 * @param {Object=} data post数据
 * @param {Object=} opts ajax选项
 * @returns {Deferred}
 */
function restPost(url, data, opts) {

    opts = opts || {};

    opts.type = 'POST';
    opts.data = data || {};

    return rest(url, opts);
}