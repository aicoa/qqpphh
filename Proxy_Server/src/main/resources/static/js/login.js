
window.onload=function () {

    //登录
    function login() {
        var username = document.getElementById("username").value;
        var passwd = document.getElementById("passwd").value;
        var placeholder = document.getElementById("placeholder");
        if (username == null || passwd == null || username.length <= 0 || passwd.length <= 0) {
            placeholder.innerText = "用户名或密码不能为空";
        }else {
            var xhr = new XMLHttpRequest();
            xhr.open("GET","/api/admin/login?username="+username+"&passwd="+passwd,true);
            xhr.send();
            xhr.onreadystatechange=function (ev) {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    var res = JSON.parse(xhr.responseText);
                    if (res) {
                        // 登录成功，刷新父页面
                        parent.window.location.href = "index";
                    }else {
                        placeholder.innerText = "用户名或密码错误";
                    }
                }
            };
        }
    }

    //绑定登录事件
    function bindLogin() {
        var loginBtn = document.getElementById("loginBtn");
        loginBtn.addEventListener("click",login);
    }
    bindLogin();

}