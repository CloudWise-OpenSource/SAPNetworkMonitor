/**
 * Name:
 * Created by authur on 17/6/16.
 */

var ENV = (function(){

    var host = '/';
    var VERSION = host + 'api/';
    var TASK    = VERSION + 'tasks/';
    var MONITOR = VERSION + 'monitors/';
    var ANALYSIS = VERSION + 'analysis/';
    var HISTORY = VERSION + 'history/';

    return {
        DOMAIN: host,   //业务运维平台后端地址
        storage:{
            task:'taskId',
            monitor:'monitorId'
        },
        location:{
            analysis:ANALYSIS,
        },
        API:{
            GET_TASK_LIST: TASK,
            GET_TASK:TASK,
            PUT_TASK: TASK,
            DELETE_TASK:TASK,
            ADD_TASK: TASK + 'addTask',
            SAVE_TASK: TASK + 'task',
            GET_MONITOR_LIST: MONITOR,
            GET_ANALYSIS_LIST : ANALYSIS + 'list',
            HISTORY : HISTORY,
            GET_HISTORY_TASKS : HISTORY + 'tasks',
            GET_HISTORY_MONITORS : HISTORY + 'tasks/',
            GET_HISTORY_DATA : HISTORY + 'data/',
            GET_HISTORY_RESULT : HISTORY + 'results/',
        }
    }
})();
