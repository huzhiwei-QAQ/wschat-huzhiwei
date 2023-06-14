/** 该工具基于JQuery实现,使用该工具前先导入jQuery
 * ml前端工具封装
 * @auther molu
 * @date 2021-09-02
 */
var ml = {};

/**
 * 判断val值是否为null,undefined
 * @param {Object} val
 * @return {boolean} true || false
 */
ml.isBlank = function (value) {
    if (!value || value == "" || value.length === 0 || value == " " || value == "	" ||
        typeof value === 'undefined' || typeof value === 'null' || typeof value === 'NaN' ||
        value === null || value === NaN || value === undefined) {
        return true;
    }
    return false;
}

/**
 * 如果值是undefined,null 转为 ""
 * @param {Object} val
 * @return str
 */
ml.empty = function (val) {
    return ml.isBlank(val) ? "" : val;
}


/**
 * msg消息弹窗
 * @param msg 文本消息
 * @param bgcolor 弹窗背景颜色 非必填 默认green
 * @param bgcolor timeout 显示时长 毫秒值 非必填 默认3秒
 */
ml.msgBox = function (msg, bgcolor, timeout) {
    bgcolor = ml.empty(bgcolor);
    if (!ml.empty(msg)) {
        return false;
    }
    if (bgcolor) {
        bgcolor = `background-color:${bgcolor};`;
    } else {
        bgcolor = "background-color: green;";
    }
    if (ml.isBlank(timeout)) {
        timeout = 3000;
    } else {
        timeout = timeout * 1000;
    }
    var html = `<div class="ml-box-msg-win-main" 
                     style="opacity: 0.8; border-radius: 3px; padding: 10px; position: absolute;
                     top: 50%;left: 50%;transform: translate(-50%, -50%);${bgcolor}">
                    <span style="color: white;width: auto;">${msg}</span>
                </div>`;
    $("body").append(html);
    setTimeout(function () {
        $(".ml-box-msg-win-main").remove();
    }, timeout);
}

function moveDiv() {
    var box = document.getElementsByClassName("msgBoxBtn_divMain_divWin")[0]; //获取元素
    var x, y; //鼠标相对与div左边，上边的偏移
    var isDrop = false; //移动状态的判断鼠标按下才能移动
    box.onmousedown = function (e) {
        var e = e || window.event; //要用event这个对象来获取鼠标的位置
        x = e.clientX - box.offsetLeft;
        y = e.clientY - box.offsetTop;
        isDrop = true; //设为true表示可以移动
    }
    document.onmousemove = function (e) {
        //是否为可移动状态                　　　　　　　　　　　 　　　　　　　
        if (isDrop) {
            var e = e || window.event;
            var moveX = e.clientX - x; //得到距离左边移动距离                    　　
            var moveY = e.clientY - y; //得到距离上边移动距离
            //可移动最大距离
            var maxX = document.documentElement.clientWidth - box.offsetWidth;
            var maxY = document.documentElement.clientHeight - box.offsetHeight;
            moveX = Math.min(maxX, Math.max(0, moveX));
            moveY = Math.min(maxY, Math.max(0, moveY));
            box.style.left = moveX + "px";
            box.style.top = moveY + "px";
        }
    }
    document.onmouseup = function () {
        isDrop = false; //设置为false不可移动
    }
}

ml.tips = function (msg, clazz) {
    if (!msg) {
        return;
    }
    $(".fun_win_div_tips_msg").remove();
    let html = `<p class="fun_win_div_tips_msg" style="z-index:10;margin-left: 50px;position: absolute;">
					<span style="padding:3px 8px;text-align:center;box-shadow:0px 0px 2px 1px #6a5700;padding: 3px 8px;
					text-align:center;background-color: #fff;">
						${msg}
					</span>
				</p>`;
    setTimeout(function () {
        $(".fun_win_div_tips_msg").remove();
    }, 1800);
    if (!clazz) {
        return ml.msgBoxBtn(msg);
    }
    $("." + clazz).after(html);
}

ml.msgBoxBtn = function (msg, title, callback) {
    if (ml.isBlank(msg)) return;
    if (ml.isBlank(title)) title = "提示";
    var left = (Number($(document.body).width()) - (Number(msg.toString().length * 15) / 2)) / 2;
    var top = (Number($(document.body).height()) / 3);
    var css = `margin:auto;padding: 10px 20px;display:block;width:150px;margin-top:20%;font-size:16px;border-radius:5px;z-index:1000;position:sticky;max-width:550px;left:${left}px;top:${top}px;`;
    var html = `<div class="ml-box-confirm-win-main" style="${css}">
					<div style="background:#D5FF84;cursor:default;text-align:center; padding:5px 0; height:20px;" class="titleMain">${title}</div>
					<div style="padding: 5px 10px; background: #fff; text-align: center;font-size:15px;">${msg}</div>
					<div style="text-align: center; padding: 10px; background: #fff;">
						<button id="sure">确定</button>
						<button id="cancel" style="margin-left: 5%;">取消</button>
					</div>
				</div>`;
    $("body").append(html);
    dragPanelMove(".titleMain", ".ml-box-confirm-win-main");
    $("#cancel").click(function () {
        $(".ml-box-confirm-win-main").remove();
        callback({res: false, text: msg, title: title});
    });
    $("#sure").click(function () {
        $(".ml-box-confirm-win-main").remove();
        callback({res: true, text: msg, title: title});
    });

};

/**
 * JQuery拖动DIV
 */
function dragPanelMove(titleDiv, mainWinDiv) {
    $(titleDiv).mousedown(function (e) {
        var isMove = true;
        var div_x = e.pageX - $(mainWinDiv).offset().left;
        var div_y = e.pageY - $(mainWinDiv).offset().top;
        $(document).mousemove(function (e) {
            if (isMove) {
                var obj = $(mainWinDiv);
                obj.css({"left": e.pageX - div_x, "top": e.pageY - div_y});
            }
        }).mouseup(
            function () {
                isMove = false;
            });
    });
}
