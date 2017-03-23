window.BaseData = {
	path: "${common\.base-path}",	//项目的根路径
	imgUrl: "${common\.img-url}",	//图片地址路径
	tmpPath: "${tmp_file!'tmp'}",	//临时文件存放的文件名
	menus: "MENU_IDS", 		//当前menu的key值
	dialog_req: "${dialog_req_key!'DIALOG_REQ'}",//标记此次请求是弹出框发送的请求，controller返回callback(closeDialog(), response)格式的数据
	userInfo: null,
	suc: "${code\.suc}",	//请求返回成功的标识
	noLogin: "${code\.no\.login}",//Ajax请求返回未登录状态
	code: "${code_key!'code'}",   	//code的key值
	msg: "${msg_key!'msg'}",     	//msg的key值
	data: "${data_key!'data'}",   	//data的key值
	init: function(data) {
		return utils.extend(this, data);
	}
};