let username;
let userId;
let toName;
let toId;
let currenChattUser = {id: '', name: '', count: 0};
let unReadMsgUser = {id: '', count: 0}
let ids = [];
let imgUrl = "../../imgs/chatIc.png";
let webSocket;
let lockReconnect = false; // 网络断开重连
let wsCreateHandler = null;
let reconnectCount = 0;
let isConn = false;
let pageNum=1;

$(function () {
    // 关闭异步请求方式，使用同步请求
    username = $(".uname").val();
    userId = $(".uid").val();

    //移除右下角标，去除大小调节功能
    $(".bg_change_size").remove();

    // 建立连接
    createWebSocket();

    // 发送消息
    $(".sendMsg").on("click", function () {
        sendMsg(webSocket);
    });
    // 表情包
    emojiFace();
});


// 建立连接
function createWebSocket() {
    try {
        // 获取访问路径 带有端口号  ws://localhost/webSocket/001
        let host = window.location.host;
        // 创建WebSocket连接对象
        webSocket = new WebSocket(`ws://${host}/webSocket/${userId}`);
        // 加载组件
        initWsEventHandle();
    } catch (e) {
        writeToScreen("连接出错，正在尝试重新连接，请稍等。。。");
        // 尝试重新连接服务器
        reconnect();
    }
}

// 初始化组件
function initWsEventHandle() {
    try {
        // 建立连接
        webSocket.onOpen = function (evt) {
            onWsOpen(evt);
            // 建立连接之后，开始传输心跳包
            heartCheck.start();
            writeToScreen("连接成功。。。");
            isConn = true;
        };
        // 传送消息
        webSocket.onmessage = function (evt) {
            // 发送消息
            onWsMessage(evt);
            // 接收消息后 也需要心跳包的传送
            heartCheck.start();
            isConn = true;
        };
        // 关闭连接
        webSocket.onclose = function (evt) {
            // 关闭连接，可能是异常关闭，需要重新连接
            onWsClose(evt);
            // 尝试重新连接
            isConn = false;
        };
        // 连接出错
        webSocket.onerror = function (evt) {
            // 连接出错
            onWsError(evt);
            // 尝试重新连接
            reconnect();
            isConn = false;
        }
    } catch (e) {
        writeToScreen("初始化组件失败，正在重试，请稍后。。。");
        // 尝试重新创建连接
        reconnect();
        isConn = false;
    }

}

function onWsOpen(e) {
    //writeToScreen("连接成功。。。");
}

function onWsMessage(e) {
    //接收到服务器推送的消息后触发事件
    message(e);
}

function onWsClose(e) {
    closeFun(e);
}

function onWsError(e) {
    if (reconnectCount == 2) {
        writeToScreen("连接出错，正在尝试重新连接服务器，请稍侯。。。");
    }
}

function writeToScreen(message) {
    ml.msgBox(message, 5, 3);
}

function reconnect() {
    if (lockReconnect) {
        return;
    }
    if (reconnectCount >= 30) {
        writeToScreen("未收到服务器的响应，连接关闭。。。");
        webSocket.close();
        clearTimeout(wsCreateHandler);
        reconnectCount = 0;
        return false;
    }
    console.log("正在重新连接。。。" + reconnectCount);
    lockReconnect = true;
    // 没链接上会一直连接，设置延迟，避免过多请求
    wsCreateHandler && clearTimeout(wsCreateHandler);
    wsCreateHandler = setTimeout(function () {
        createWebSocket();
        lockReconnect = false;
        reconnectCount++;
        isConn = false;
    }, 3000);
}

async function message(event) {
    //获取服务端推送过来的消息
    let result = event.data;
    // 将message转为JSON对象
    let res = JSON.parse(result);
    // 组织好友列表
    let friendList = "";
    // 组织系统通知内容
    let sysMsg = "";
    if (res.type && res.type == "PONG") {
        isConn = true;
        //timeTip("连接成功，请刷新页面。。。");
        return;
    }
    if (res.sendErr) {
        writeToScreen(res.sendErr);
        return;
    }
    // 是否为系统消息
    if (res.systemMsgFlag) {
        //为系统消息则：1. 好友列表展示   2. 系统推广
        let allUser = res.message;// name = username_userId
        const data = {id: userId};
        await fetch("/user/queryFriendsList", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then(response => response.json()).then(data => {
            if (data.code == 200) {
                let    friends = data.data;


                // 遍历friends中的每个用户
                for (let i = 0; i < friends.length; i++) {
                    let isDuplicate = false;
                    let uniqueUser;
                    // 检查当前用户的姓名是否在allUser中
                    for (let j = 0; j < allUser.length; j++) {
                        if (friends[i].id == allUser[j].userId) {
                            isDuplicate = true;
                            uniqueUser=allUser[j];
                            break;
                        }
                    }
                    // 如果用户的姓名不是重复的，将其添加到uniqueUsers数组中
                    if (isDuplicate) {

                        let count = 0;
                        let isShow = "";
                        $.each(res.map, function (item, num) {
                            if (item && item == friends[i].id) {
                                count = Number(num) > 99 ? 99 : Number(num);
                                isShow = `style="display:inline;"`;
                                if (ids.indexOf(friends[i].id) == -1) {
                                    ids.push(friends[i].id);
                                }
                            }
                        })
                        if (friends[i].id != userId) {
                            // 组织好友列表
                            friendList += `<li class="friend-li" onclick='chatWith("${friends[i].id}","${friends[i].username}",this);'>
								<span><img class="friend-img" src="${imgUrl}"/></span>
								<span>${friends[i].username}</span>
								<span class="msg-count msg-count-${friends[i].id}" ${isShow} title="${count}条信息未读">${count}</span>
							</li>`;
                            // 组织系统通知
                            sysMsg += `<li class="sys-msg-li" style="color:#9d9d9d;font-family:宋体;">
						 	<span style="font-size:5px;color:#999;">${uniqueUser.dateStr}</span><br/>
						 	<span>好友<font style="color:blue;">${friends[i].username}</font>上线了</span>
						 </li>`;
                            tips("sendMsg", `用户：${friends[i].username}上线了`);
                        }
                    }else {
                        let count = 0;
                        let isShow = "";
                        $.each(res.map, function (item, num) {
                            if (item && item == friends[i].id.toString()) {
                                count = Number(num) > 99 ? 99 : Number(num);
                                isShow = `style="display:inline;"`;
                                if (ids.indexOf(friends[i].id.toString()) == -1) {
                                    ids.push(friends[i].id.toString());

                                }
                            }
                        })
                        if (friends[i].id != userId) {
                            // 组织好友列表
                            friendList += `<li class="friend-li" onclick='chatWith("${friends[i].id}","${friends[i].username}",this);'>
								<span><img style="filter: grayscale(100%) opacity(40%);" class="friend-img" src="${imgUrl}"/></span>
								<span>${friends[i].username}</span>
								<span class="msg-count msg-count-${friends[i].id}" ${isShow} title="${count}条信息未读">${count}</span>
							</li>`;
                        }
                    }
                }
            }
        }).catch(error => {
            console.error(error);
        });



        $(".friend-ul").html(friendList);
        $(".sys-msg-ul").html(sysMsg);
        // 不是系统消息
    } else {
      if(res.messageType==1){
          let contextMsg = res.message;
          let chatMsg = `<li class="friend-msg-li">
						<span><img class="friend-msg-img" src="${imgUrl}" /></span>
						<span class="friend-msg-span">${contextMsg}</span>
					</li>`;
          if (!currenChattUser.id || currenChattUser.id != res.fromId) {
              let count = Number($(".msg-count-" + res.fromId).text());
              if (!count || count == NaN) {
                  count = 0;
              }
              unReadMsgUser.id = res.fromId;
              $.post("/index/setCount", {fromId: res.fromId, userId: userId, count: count}, function (resData) {
                  if (resData.flag) {
                      count = resData.data > 99 ? 99 : resData.data;
                      currenChattUser.count = count;
                      unReadMsgUser.count = count;
                      $(".msg-count-" + res.fromId).text(count);
                      $(".msg-count-" + res.fromId).show();
                      $(".msg-count-" + res.fromId).attr("title", count + "条信息未读");
                  }
              }).catch(function (err) {
                  console.log(err);
              })
          }
          if (toId === res.fromId) {
              $(".chating-main-msg").append(chatMsg);
          }
          scrollIntoView();
      }else if(res.messageType==0){
          console.log("hhhhhhhhhhhhhhh")
      }
    }
}

var heartCheck = {
    // 在15s内若没收到服务端消息，则认为连接断开，需要重新连接
    timeout: 15000, // 心跳检测触发时间
    timeoutObj: null,
    serverTimeoutObj: null,
    // 重新连接
    reset: function () {
        clearTimeout(this.timeoutObj);
        clearTimeout(this.serverTimeoutObj);
        this.start();
    },
    // 开启定时器
    start: function () {
        let self = this;
        this.timeoutObj && clearTimeout(this.timeoutObj);
        this.serverTimeoutObj && clearTimeout(this.serverTimeoutObj);
        this.timeoutObj = setTimeout(function () {
            try {
                webSocket.send("PING");
            } catch (e) {
                writeToScreen("连接服务器出错。。。");
            }
            //内嵌定时器
            self.serverTimeoutObj = setTimeout(function () {
                // 若onclose方法会执行reconnect方法，我们只需执行close()就行，若直接执行reconnect会触发onclose导致重连两次
                reconnect();
                $("#documentDiv").remove();
            }, self.timeout);
        }, this.timeout);
    }
};

/**
 * 选择好友
 * @param id 好友id
 * @param name 好友名称
 * @param obj
 */
function chatWith(id, name, obj) {
    //切换用户，查询页数重新开始
    pageNum = 1;

    toName = name;
    toId = id;
    currenChattUser = {id: id, name: name, count: 0};
    $(".msg-count-" + id).hide();
    $(obj).addClass("selected-li").siblings().removeClass("selected-li");
    let chatNow = `正在和<span style="color: #db41ca;">${name}</span>聊天`;
    $(".chating>font").html(chatNow);
    $(".chating-main-msg").html("");

    getHistoryChatMessage();
}

  function getHistoryChatMessage(){
    if(pageNum===0){
        console.log("没有下一页");
        return;
    }
      const data = {fromId:userId,toId:toId,pageNum:pageNum,pageSize:20,isAsc:false,orderByField:"create_time"};
      fetch('/message/queryChatMessageList', {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json'
          },
          body: JSON.stringify(data)
      })
          .then(response => response.json())
          .then(data => {


              if (data.list!=null) {
                  //获取聊天数据
                  let chatData = '';
                  for (let i = data.list.length-1; i >=0; i--) {
                      if(data.list[i].fromId===userId){
                          let msg=data.list[i].message;
                          let img = `<span><img src='${imgUrl}' class="myself-msg-img"/></span>`;
                          let li = `<li class='myself-li'><span class="myself-msg-span">${msg}</span>${img}</li>`;
                          chatData=chatData+li;

                      }else if(data.list[i].toId===userId){
                          let contextMsg = data.list[i].message;
                          let chatMsg = `<li class="friend-msg-li">
						<span><img class="friend-msg-img" src="${imgUrl}" /></span>
						<span class="friend-msg-span">${contextMsg}</span>
					</li>`;
                          chatData=chatData+chatMsg;
                      }
                  }
                  if (chatData) {
                      if(pageNum===1){
                          //渲染聊天数据到聊天区
                          console.log("第一页");
                          $(".chating-main-msg").html(chatData);
                          scrollIntoView();
                      }else {
                          console.log("下一页");
                          const height = $(".chat-main")[0].scrollHeight;
                          $(".chating-main-msg").prepend(chatData);
                          $(".chat-main")[0].scrollTop = $(".chat-main")[0].scrollHeight-height;
                      }

                  }
                  let idx = ids.indexOf(toId);
                  if (idx != -1 && toId == ids[idx]) {
                      $.post("/index/clearCount", {fromId: toId, userId: userId}, function (data) {
                          $(".msg-count-" + toId).text(data);
                      })
                  }
              }
              if(data.hasNextPage===true){
                  console.log("有无下一页:"+data.hasNextPage);
                  pageNum=data.nextPage;
              }else {
                  pageNum=0;
              }
          }).catch(error => {
              console.error(error);
          });
  }

/**
 * 发送消息事件
 */
async function sendMsg(ws) {
    if (!toId || !toName) {
        tips("sendMsg", "请选择好友...");
        return;
    }
    let msg = $(".edit-msg").html();
    if (!msg) {
        tips("sendMsg", "请输入内容...");
        return;
    }
    if (isBase64Image(msg)) {
        console.log("这是一张图片");
        const str= substr(msg);
        const blob = await fetch(str).then((res) => res.blob());
        const originalFilename=getFileOriginalFilename(str);
        const formData = new FormData();
        formData.append("file", blob,"file."+originalFilename);
        await fetch("/file/uploadFile", {
            method: "POST",
            body: formData,
        }).then(response => response.json()).then(data => {
            if(data.code==200){
                msg='<img src="'+data.data+'" style="max-width:300px;height:auto;">';
            }

        }).catch(error => {
            console.error(error);
        });
    } else {
        console.log("不是一张图片");
    }

    let jsonMessage = {
        "fromName": username, //消息发送人姓名
        "fromId": userId, //消息发送人id
        "toName": toName, //消息接收人姓名
        "toId": toId, //消息接收人id
        "message": msg //发送的消息内容
    };
    // 发送数据给服务器
    ws.send(JSON.stringify(jsonMessage));
    // 显示发送数据
    let img = `<span><img src='${imgUrl}' class="myself-msg-img"/></span>`;
    let li = `<li class='myself-li'><span class="myself-msg-span">${msg}</span>${img}</li>`;
    $(".chating-main-msg").append(li);
    // 获取 sessionStorage 中的缓存消息
    // let chatData = sessionStorage.getItem(toId);
    // if (chatData) {
    //     li = chatData + li;
    // }
    // // 将最新的消息存放到 sessionStorage 中
    // sessionStorage.setItem(toId, li);
    //$(".edit-msg").val('');
    $(".edit-msg").html('');
    scrollIntoView();
}


 function isBase64Image(str) {
    try {
        const base64Header = "<img src=\"data:image/";
        const base64Index = str.indexOf(base64Header);
        if (base64Index === -1) {
            return false;
        } else {
            return true;
        }
    } catch (err) {
        return false;
    }
}

function getFileOriginalFilename(str){
    var start = "data:image/";
    var end = ";base64";
    var startIndex = str.indexOf(start);
    var endIndex = str.indexOf(end, startIndex + start.length);

    if (startIndex != -1 && endIndex != -1) {
        var result = str.substring(startIndex + start.length, endIndex);
        console.log("originalFilename:"+result);
        return result;
    } else {
        console.log("String not found.");
    }
}

function substr(str){
    var start = "<img src=\"";
    var end = "\" style=\"width:";

    var startIndex = str.indexOf(start);
    var endIndex = str.indexOf(end, startIndex + start.length);

    if (startIndex != -1 && endIndex != -1) {
        var result = str.substring(startIndex + start.length, endIndex);
        return result;
    } else {
        console.log("String not found.");
    }
}
/**
 * 获取访问路径中的指定参数值(http://127.0.0.1/views/chat.html?username=lover&userId=007)
 * http://127.0.0.1/views/chat.html?uid=007
 */
function GetQueryString(name) {
    let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    let r = window.location.search.substr(1).match(reg);//获取url中"?"后的字符串，并正则匹配
    let context = "";
    if (r) {
        context = r[2];
    }
    reg = null;
    r = null;
    return context == null || context == "" || context == "undefined" ? "" : context;
}

/**
 * 发送emoji表情
 */
function emojiFace() {
    $(".face").on('click', function () {
        // 初始化emoji插件
        $(".edit-msg").emoji({
            // 触发表情的按钮
            button: '.face',
            showTab: false,
            animation: 'slide',
            position: 'topLeft',
            icons: [
                {
                    name: 'QQ表情', //表情名,表情框中显示的名字
                    path: '../../lib/meoji/img/qq/', // 表情包所在的路径
                    maxNum: 91,
                    excludeNums: [41, 45, 54],
                    file: '.gif',
                    placeholder: '#qq_{alias}#'
                },
                {
                    name: '贴吧表情',
                    path: '../../lib/meoji/img/tieba/',
                    maxNum: 50,
                    excludeNums: [41, 45, 54],
                    file: '.jpg',
                    placeholder: '#tieba_{alias}#'
                }
            ]
        });
    })
}

/**
 * 发送图片
 */
$("#file").on('change', function () {
    sendImages(webSocket);
});

function readFiles() {
    const files = document.getElementById("file-input").files;

    for (const file of files) {
        const reader = new FileReader();
        reader.readAsDataURL(file);
            console.log("fileType:"+file.type);
            reader.onload = function(event) {
            const media = document.createElement(file.type.startsWith("video") ? "video" : "img");
            media.src = event.target.result;
            if (file.type.startsWith("video")) {
                media.addEventListener("loadeddata", async function () {
                    let msg;
                    if (!toId || !toName) {
                        tips("sendMsg", "请选择好友...");
                        return;
                    }
                    //将视频上传到服务器
                    const formData = new FormData();
                    formData.append("file", file);
                    await fetch("/file/uploadFile", {
                        method: "POST",
                        body: formData
                    }).then(response => response.json()).then(data => {
                        if (data.code == 200) {
                            msg = '<video src="' + data.data + '" style="max-width:300px;height:auto;" controls autoplay></video>';
                        }
                    }).catch(error => {
                        console.log(error);
                    });

                    let jsonMessage = {
                        "fromName": username, //消息发送人姓名
                        "fromId": userId, //消息发送人id
                        "toName": toName, //消息接收人姓名
                        "toId": toId, //消息接收人id
                        "message": msg //发送的消息内容
                    };
                    // 发送数据给服务器
                    webSocket.send(JSON.stringify(jsonMessage));
                    // 显示发送数据
                    let img = `<span><img src='${imgUrl}' class="myself-msg-img"/></span>`;
                    // let li = `<li class='myself-li'><div class="video-container">${msg}</div>${img}</li>`;
                    let li = `<li class='myself-li'><span class="myself-msg-span">${msg}</span>${img}</li>`;
                    $(".chating-main-msg").append(li);
                    $(".edit-msg").html('');
                    scrollIntoView();
                });
            } else {
                media.style.width = "auto";
                media.style.maxHeight = "100px";
                document.getElementById("edit-msg").appendChild(media);
            }
        };

    }
}

/**
 * 发送图片到服务器
 * @param ws
 */
function sendImages(ws) {
    let file = this.files[0];
    // 把文件发送到服务器,需要借助与html5的 FileReader
    let fr = new FileReader();
    fr.readAsDataURL(file);

    fr.onload = function () {
        // let img = fr.result;
        // ws.send('sendImage', {fromUserId: userId, toUserName: toName, toUserId: toId, img: img});
    }
}

/**
 * 等 图片加载完成后 在去执行方法
 * load 表示最后一张图片加载完成
 */
$('.chat-main .chating-main-msg span img:last').on('load', function () {
    scrollIntoView();
})

// 关闭连接
function closeFun(e) {
    console.log(e);
    //let tips = `<span style='font-size:5px;color:black;'>${new Date()}</span><br/>`;
    //$(".sys-msg").html(`${tips}用户：${username}<span style="float:right;color:red;">离开了</span>`);
}

/**
 * 滚动条显示在最底部
 */
function scrollIntoView() {
    $(".chat-main")[0].scrollTop = $(".chat-main")[0].scrollHeight;
}

function tips(clazz, msg) {
    $(".fun_win_div_tips_msg").remove();
    let html = `<p class="fun_win_div_tips_msg" style="z-index:10;margin: 5px -80px;">
			<span style="padding:3px 8px;text-align:center;box-shadow:0px 0px 2px 1px #6a5700;padding: 3px 8px;
			text-align:center;background-color: #fff;">
				${msg}
			</span>
		</p>`;
    setTimeout(function () {
        $(".fun_win_div_tips_msg").remove();
    }, 1800);
    $("." + clazz).after(html);
}

/**
 * 锁屏蒙层
 */
function timeTip(msg) {
    if (!msg) {
        msg = "连接出错，正在尝试重新连接。。。";
    }
    let width = document.body.scrollWidth;
    let height = document.body.scrollHeight;
    let time = 120;
    let html = `<div id="documentDiv" style="width:${width};height:${height};z-index: 99999;background: #000;opacity: 65%;">
			<h3 class="tipsDiv" id="tipsDiv" style="position: absolute;top: 50%;left: 50%;transform: translate(-50%, -50%);
						box-shadow: 0px 0px 5px 2px #c6c1c1;padding: 150px 45px;background:#fff;">
				${msg}<span id="time" style="color:red;">${time}</span>
			</h3>
		<div>`;
    var interval = setInterval(function () {
        if (time == 0) {
            clearInterval(interval);
            $("#tipsDiv").text("连接服务器失败，请稍后重试。。。");
        }
        $("#time").text(time--);
    }, 1000);
    if (!$("#documentDiv").hasClass("tipsDiv")) {
        $("body").append(html);
    }
}

// const button = document.getElementById('getUserList');
// button.addEventListener('click', getUserList);

function getUserList(){

    const data = {pageNum:1,pageSize:100,isAsc:false,orderByField:"created",id:userId};
    fetch("/user/getUserList", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body:JSON.stringify(data)
    }).then(response => response.json()).then(data => {
        if(data.code==200){
            console.log(data.data.list);

            const dropdownList = document.createElement('select');
            data.data.list.forEach(user => {

                const option = document.createElement('option');
                option.value = user.id;
                option.text = user.username;
                dropdownList.appendChild(option);

            });

            dropdownList.style.fontSize='16px';
            dropdownList.style.width='100px';
            dropdownList.style.maxHeight='200px';
            dropdownList.style.overflowY='auto';
            dropdownList.className='dropdownList';

            const dropdownContainer = document.getElementById('dropdown-container');
            dropdownContainer.appendChild(dropdownList);
            dropdownContainer.style.display = 'block';
            document.getElementById("getUserList").style.display = "none";


            const button = document.createElement('button');
            button.textContent = '添加';
            button.className='dropdown-button';
            dropdownContainer.appendChild(button);
            button.addEventListener('click', addUser);
        }

    }).catch(error => {
        console.error(error);
    });

    document.addEventListener('click', function(event) {
        const dropdownContainer = document.getElementById('dropdown-container');
        const clickedElement = event.target;

        // Check if the clicked element is outside the dropdown container
        if (!dropdownContainer.contains(clickedElement)) {
            // Hide the dropdown
            dropdownContainer.style.display = 'none';

            // Clear the dropdown content
            dropdownContainer.innerHTML = '';

            document.getElementById("getUserList").style.display = "block";

        }
    });


    function addUser(){
        const dropdownList = document.getElementsByClassName('dropdownList')[0];
        const selectedValue = dropdownList.value;
        const data = {userId:userId,friendId:selectedValue};
         fetch("/user/addUser", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then(response => response.json()).then(data => {
            console.log(data);
            if(data.code==200){
                let jsonMessage = {
                    "fromName": username, //消息发送人姓名
                    "fromId": userId, //消息发送人id
                    "toName": data.data.username, //消息接收人姓名
                    "toId": data.data.id, //消息接收人id
                    "message": "添加好友请求", //发送的消息内容
                    "messageType":0
                };
                // 发送数据给服务器
                webSocket.send(JSON.stringify(jsonMessage));
            }
        }).catch(error => {
            console.error(error);
        });
        }

}
