var do4ServerMessage = function(msg){
	//客户端解析消息
	var _reg = /^\[(.*)\](\s\-\s(.*))?/g;
	var group = '';
	var header = "",content="",cmd="",time=0,sender="";
	while(group = _reg.exec(msg)){
		header = group[1];
		content = group[3];
	}
	var headers = header.split("][");
	cmd = headers[0];
	time = headers[1];
	sender = headers[2];//消息发送人
	
	if(cmd === "SYSTEM"){
		var online = headers[2];
		$("#onlinecount").html(online);
		//同时在聊天窗口显示系统消息
		showServerMessage(content);
	}else if(cmd === "CHAT"){
		//聊天窗口添加系统时间
		var date = new Date(parseInt(time));
		showServerMessage('<span class="time-label">' + date.format("hh:mm:ss") + '</span>');
		
		//将聊天消息添加到聊天面板中
		var contentDiv = '<div>' + content + '</div>';
        var usernameDiv = '<span>' + sender + '</span>';
        var section = document.createElement('section');
        
        //判断消息发送人是否自己
        if(sender === "SELF"){
        	section.className = 'user';
	        section.innerHTML = usernameDiv + contentDiv;
        }else{
	        section.className = 'service';
	        section.innerHTML = usernameDiv + contentDiv;
        }
        $("#onlinemsg").append(section);
	}
	scorllToBottom();
};
var scorllToBottom = function(){
	window.scrollTo(0,$("#onlinemsg")[0].scrollHeight);
}
var showServerMessage = function(c){
	var html = "";
    html += '<div class="msg-system">';
    html += c;
    html += '</div>';
    var section = document.createElement('section');
    section.className = 'system J-mjrlinkWrap J-cutMsg';
    section.innerHTML = html;
    
    $("#onlinemsg").append(section);
};
//扩展一个date对象的format方法
Date.prototype.format = function(format){ 
	var o = { 
	"M+" : this.getMonth()+1, //月 
	"d+" : this.getDate(), //日
	"h+" : this.getHours(), //时
	"m+" : this.getMinutes(), //分
	"s+" : this.getSeconds(), //秒
	"q+" : Math.floor((this.getMonth()+3)/3), //刻
	"S" : this.getMilliseconds() //毫秒
	} 
	
	if(/(y+)/.test(format)) { 
		format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	} 
	
	for(var k in o) { 
		if(new RegExp("("+ k +")").test(format)) { 
			format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
		} 
	} 
	return format; 
};
$(document).ready(function(){
	
	window.CHAT = {
		nickName:"匿名用户",
		socket:null,
		login:function(){
			$("#error-msg").empty();
			//用户注册的名字
			var nickname = $("#nickname").val();
			CHAT.nickName = nickname;
			
			var _reg = /^\S{1,10}/;
			if(!_reg.test($.trim(nickname))){
				$("#error-msg").html("请输入1-10位正确的昵称");
				return false;
			}
			$("#shownikcname").html(nickname);
			$("#loginbox").hide();
			$("#chatbox").show();
			CHAT.init();
		},
		init:function(){
			//判断浏览器是否支持WebSocket协议
			if(!window.WebSocket){
				window.WebSocket = window.MozWebSocket;
			}
			
			if(window.WebSocket){
				CHAT.socket = new WebSocket("ws://localhost:8088/im");
				CHAT.socket.onopen = function(e){
					console.log("客户端连接成功.");
					CHAT.socket.send("[LOGIN]["+new Date().getTime()+"]["+CHAT.nickName+"]");
				};
				CHAT.socket.onclose = function(e){
					console.log("客户端关闭连接.");
				};
				CHAT.socket.onmessage = function(e){
					console.log("客户端接收服务端信息："+e.data);
					do4ServerMessage(e.data);
				}
			}else{
				alert("您的浏览器不支持WebSocket协议！");
			}
		},
		logout:function(){
			location.reload();//刷新
		},
		chat:function(){
			var input = $("#send-message");
			if($.trim(input.html())===""){ return; }
			//离线判断
			if(CHAT.socket.readyState === WebSocket.OPEN){
				var msg = input.html().replace(/\n/ig,"<br/>");
				CHAT.socket.send("[CHAT]["+new Date().getTime()+"]["+CHAT.nickName+"] - "+msg);
				input.empty();
				input.focus();
			}else{
				showServerMessage("您以处于离线状态，无法发送消息。")
			}
		},
		//选择表情
		openFace:function(){
			var box = $("#face-box");
			//避免重复打开表情选择框
			if(box.hasClass("open")){
				box.hide();
				box.removeClass("open");
				return;
			}
			box.addClass("open");
			box.show();
			
			if(box.html() !== ""){ return; }
			
			var faceIcon = "";
			for(var i = 1; i <= 130; i ++){
				var path = '/images/face/' + i + ".gif";
				faceIcon += '<span class="face-item" onclick="CHAT.selectFace(\'' + path + '\')">'
				faceIcon += '<img src="' + path + '"/>';
				faceIcon += '</span>';
			}
			box.html(faceIcon);
		},
		//选择一张图片
		selectFace:function(path){
			var faceBox = $("#face-box");
			faceBox.hide();
			faceBox.removeClass("open");
			var img = '<img src="' + path + '"/>';
			$("#send-message").append(img);
			$("#send-message").focus();
		},

        keysend:function (type) {
            var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
            if (keyCode === 13) {
                if (type === 1){
                    this.login();
				}else if (type === 2){
                    this.chat();
				}
            }
        }
	};
});