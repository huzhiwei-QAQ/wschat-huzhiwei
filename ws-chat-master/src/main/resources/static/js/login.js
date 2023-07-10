$(function () {
    $(".close").click(function () {
        $(".box").hide();
    });
    $("#loginFormBtn").click(function () {
        if (loginCheckData()) {
            login();
        }
    });

    $(".uphone").bind("input propertychange", function () {
        let phone = $(this).val();
        $(".phone").val(phone);
        if (phone.length == 11) {
            phone = phone.substr(0, 3) + "******" + phone.substr(9, 11);
        }
        $(this).val(phone);
    })
    changeValue();
});

function login() {
    let params = {};
    params.phone = $('.phone').val();
    params.password = $(".upwd").val();
    $.post("/index/login", params, function (res) {
        if (res.flag) {
            ml.msgBox(res.message);
            location.href = `/index/toChatroom/${res.token}`;
        } else {
            ml.msgBox(res.msg, 5, 5);
        }
    }).catch(function (err) {
        ml.msgBox(err.responseJSON.status + "：" + err.responseJSON.message, 5, 5);
    })
}

function loginCheckData() {
    let phone = $('.phone').val();
    if (!phone) {
        ml.tips("请输入正确的手机号!", "phone");
        return false;
    }
    let regZh = /[\u4e00-\u9fa5][\u3000-\u301e\ufe10-\ufe19\ufe30-\ufe44\ufe50-\ufe6b\uff01-\uffee]/;
    if (regZh.test(phone)) {
        ml.tips("手机号不可包含中文字符!", "phone");
        $('.phone').val('');
        return false;
    }
    //1.验证手机号 规则：第一位1，第二位是358中的一个，第三到十一位必须是数字。总长度11
    let reg = /^[1][3589][0-9]{9}$/;
    if (!reg.test(phone)) {
        ml.tips("输入的手机号格式不正确!", "phone");
        return false;
    }
    let password = $('.upwd').val();
    if (!password) {
        //ml.msgBoxBtn("请输入密码!");
        ml.tips("请输入密码!", "upwd");
        return false;
    }
    if (regZh.test(password)) {
        ml.tips("密码不可包含中文字符!", "upwd");
        return false;
    }
    if (password.length < 5) {
        ml.tips("密码必须为6~15个字符之间!", "upwd");
        return false;
    }
    if (password.length > 20) {
        ml.tips("密码必须为6~20个字符之间!", "upwd");
        return false;
    }
    // let regPwd = /[A-Za-z0-9.!?]{6,20}/;
    // if (regPwd.test(password)) {
    //     ml.tips("密码仅支持大小写字母和.!?符号", "upwd");
    //     return false;
    // }
    return true;
}

function changeValue() {
    $(".showPwd").bind("input propertychange", function () {
        if ($(this).prop("checked")) {
            $(".upwd").attr("type", "text");
        } else {
            $(".upwd").attr("type", "password");
        }
    })
}

function registerAcc() {
    // ml.tips("注册功能暂未开放使用", "register");
    location.href =`/user/register`;
}



