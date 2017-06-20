/**
 * Name:
 * Created by authur on 17/6/16.
 */

var ENV = (function(){

    var host = 'http://10.0.1.181:8080/';
    var api = 'api/';
    return {
        DOMAIN: host,   //业务运维平台后端地址
        API:{
            GET_TASK_LIST: api + 'getTaskList',
            LOGIN: api + 'v1/auth/login'
        }
    }
})();