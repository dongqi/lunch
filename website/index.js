
$(document).ready(function() {
	console.log('hello world');

	$.getJSON('/service/login', function(data) {
		console.log(data);
		if(data.name.length > 0) {
			$('#account').val(data.name);
		} else {

		}
	});

	var currentDate = new Date();
	var foods = [];
	dataLoading();
	
	//菜单录入
	$('#foodInput').on('click', function() {
		var _email = $('#account').val();
    	if($.trim(_email.toLowerCase()) !== 'hushaojun' && $.trim(_email.toLowerCase()) !== 'admin') {
            BootstrapDialog.alert('无操作权限');
            return false;
        }
		
        BootstrapDialog.show({
        	title: '菜单录入',
        	message: '<div><textarea class="form-control" rows="15"></textarea></div>',
        	closable: false,
        	buttons: [{
        		label: '取消',
        		action: function(dialogRef) {
        			dialogRef.close();
        		}
        	}, {
        		label: '上传',
        		action: function(dialogRef) {
        			var menuContent = dialogRef.getModalBody().find('textarea').val();
        			if($.trim(menuContent.toLowerCase()).length == 0) {
	                    BootstrapDialog.alert('上传菜单不能为空');
	                    return false;
	                }
	                console.log('菜单录入', menuContent);
	                $.post('/service/upload', {menuContent: menuContent}, function(data) {
	                	console.log(data);
	                	BootstrapDialog.alert('上传成功');
	                }, 'json');
				    location.reload();
				    dialogRef.close();
        		}
        	}]
        });
	});

	function requestServer(_type, _url, _data, _success) {
		$.ajax({
			type: _type,
			async: false,
			data: _data,
			url: _url,
			success: _success,
			dataType: "jsonp",
			jsonp: "callbackparam", //服务端用于接收callback调用的function名的参数
			jsonpCallback: "success_jsonpCallback", //callback的function名称,服务端会把名称和data一起传递回来
		});
	}

    function ordering() {
    	var account = $('#account').val();
    	if($.trim(account.toLowerCase()).length == 0) {
            BootstrapDialog.alert('账号没有填哦，亲');
            return false;
        }
        if(/\w+/g.test(account) == false) {
        	BootstrapDialog.alert('请输入你的数天邮箱账号，就是你的名字全拼哦');
        	return false;
        }
		var id = $(this).attr('value1');
		var orderInfo = null;
		for(var index = 0; index < foods.length; index++) {
			if(foods[index].id == id) {
				orderInfo = foods[index];
				break;
			}
		}
		console.log(orderInfo);
		var html = '<div class="input-group"><input id="account" type="text" class="form-control" placeholder="请输入你的数天邮箱账号" aria-describedby="basic-addon2"><span class="input-group-addon" id="basic-addon2">@digisky.com</span></div>';
		html = '<p class="lead">'+orderInfo.name+' '+orderInfo.price+'元 1份</p>';
		BootstrapDialog.show({
			title: '订单确认',
			message: html,
			closable: false,
			size: BootstrapDialog.SIZE_SMALL,
            buttons: [{
            	label: '取消',
            	action: function(dialogRef) {
            		dialogRef.close();
            	}
            }, {
                label: '确定',
                action: function(dialogRef) {
                	$.post('/service/ordering', {id: orderInfo.id, username: account}, function(json) {
                		var msg = '下单成功';
						
						BootstrapDialog.alert({
							closable: false,
							title: '消息',
							message: msg,
							buttonLabel: '关闭'
						});
                	}, 'json');
					dialogRef.close();
                }
            }]
		});
	}

	function cancel() {
		var account = $('#account').val();
    	if($.trim(account.toLowerCase()).length == 0) {
            BootstrapDialog.alert('账号没有填哦，亲');
            return false;
        }
        if(/^[a-z]+$/g.test(account) == false) {
        	BootstrapDialog.alert('请输入你的数天邮箱账号，就是你的名字全拼哦');
        	return false;
        }
		var id = $(this).attr('value2');
		var orderInfo = null;
		for(var index = 0; index < foods.length; index++) {
			if(foods[index].id == id) {
				orderInfo = foods[index];
				break;
			}
		}
		console.log('取消订单', orderInfo);
		BootstrapDialog.confirm('你确定要取消吗？', function(result){
            if(result) {
                requestServer('GET', remote+'/cancel', {account: account}, function(json) {
                	BootstrapDialog.show({
                		title: '消息',
                		message: '取消成功',
                		buttons: [{
                			label: '关闭',
                			action: function(dialogRef) {dialogRef.close();}
                		}]
                	});

                });
            }
        });
	}

	//当日统计
	$('#list').on('click', function() {

		requestServer('GET', remote+'/currentList', {}, function(json) {
			var totalPrice = 0;
			for (var i = json.length - 1; i >= 0; i--) {
				var price = json[i].price;
				totalPrice += parseInt(price);
			};
			var _label = '合计'+totalPrice+'元';
			var dialog = new BootstrapDialog({
				title: '当日统计',
				message : function(dialogRef) {
					var $message = $('<div></div>');
					var $table = $('<table class="table table-striped"></table>');
					var $thead = $('<thead><tr><th>姓名</th><th>盒饭</th><th>价格</th></tr></thead>');
					$table.append($thead);
					var $tbody = $('<tbody></tbody>');

					$.each(json, function(i, item) {
						$tbody.append($('<tr><td>'+item.user+'</td><td>'+item.foodName+'</td><td>'+item.price+'</td></tr>'));
					});
					$table.append($tbody);
					$message.append($table);
					console.log($message);
					return $message;
				},
				buttons: [{
					label: _label,
					cssClass: 'btn-primary',
					action: function(dialogRef) {
						dialogRef.close();
					}
				}]
			});
			dialog.open();

		});
	});

	function dataLoading() {
		$.getJSON('/service/list', function(json) {
			$('#foodItems').empty();
			console.log('dataLoading: ', json);
			for(var index = 0; index < json.length; index++) {
				var item = json[index];
				if(item.valid) {
					var data = '<div class="col-xs-6 col-md-4">';
				  	data += '<div class="thumbnail">';
				  	data += '<div class="caption">';
				  	data += '<h3>'+item.name+'</h3>';
				  	data += '<p>'+item.price+'元</p>';
				  	data += '<p><a value1="'+item.id+'" href="#" class="btn btn-primary" role="button" id="ding_'+item.id+'">订</a> <a value2="'+item.id+'" id="tui_'+item.id+'" href="#" class="btn btn-default" role="button">退</a></p>';
				  	data += '</div>';
				  	data += '</div>';
				  	data += '</div>';
				  	$('#foodItems').append(data);
				}
			}
			
			foods = json;
			//订
			$('a[value1]').on('click', ordering);
			//退
			$('a[value2]').on('click', cancel);
			console.log('dataLoading after: ', $('#foodItems'));
		});

	}	
});

