
$(document).ready(function() {

	$.getJSON('/service/login', function(data) {
		console.log(data);
		if(data.name.length > 0) {
			$('#account').val(data.name);
		} else {
			BootstrapDialog.show({
				title: '账号',
				message: '<div class="input-group"><input type="text" class="form-control" placeholder="请输入你的数天邮箱账号"><span class="input-group-addon">@digisky.com</span></div>',
				closable: false,
				buttons: [{
	        		label: '确定',
	        		action: function(dialogRef) {
	        			var account = dialogRef.getModalBody().find('input').val().trim();
	        			var bln = /^[a-z]+$/g.test(account);
	        			console.log(account, bln);
	        			if(!bln) {
	        				BootstrapDialog.alert('请输入你的数天邮箱账号，就是你的名字全拼哦');
	        			} else {
	        				$('#account').val(account);
	        				$.post('/service/signin', {account: account}, function(data) {}, 'json');
	        				dialogRef.close();
	        			}
	        		}
	        	}]
			});
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

    function ordering() {
    	var account = $('#account').val();
    	if($.trim(account.toLowerCase()).length == 0) {
    		$('#account').focus();
            BootstrapDialog.alert('账号没有填哦，亲');
            return false;
        }
        if(/^[a-z]+$/g.test(account) == false) {
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
		html += '<p>请尽快付款，目前支持：现金和支付宝</p>';
		html += '<p><img src="pay.jpg" alt="pay" class="img-thumbnail"></p>';
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
		$.getJSON('/service/currentOrders', function(data) {
			console.log(data);
			var totalPrice = 0;
			var totalPricePay = 0;
			var totalPriceNotPay = 0;
			for (var i = data.length - 1; i >= 0; i--) {
				var price = parseInt(data[i].foodItem.price);
				totalPrice += price;
				if(data[i].pay) {
					totalPricePay += price;
				} else {
					totalPriceNotPay += price;
				}
			};
			var _label = '未付'+totalPriceNotPay+'元, 已付'+totalPricePay+'元, 合计'+totalPrice+'元';
			var dialog = new BootstrapDialog({
				title: '当日统计',
				message : function(dialogRef) {
					var $message = $('<div></div>');
					var $table = $('<table class="table table-striped"></table>');
					var $thead = $('<thead><tr><th>序号</th><th>姓名</th><th>盒饭</th><th>价格</th><th>付款</th><th>时间</th></tr></thead>');
					$table.append($thead);
					var $tbody = $('<tbody></tbody>');

					$.each(data, function(i, item) {
						var date = new Date();
						date.setTime(item.datetime);
						var dateStr = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+' '+date.getHours()+':'+date.getMinutes()+':'+date.getSeconds();
						var color = item.pay ? '' : 'style="color:red"';
						$tbody.append($('<tr '+ color +'><td>'+(i+1)+'</td><td>'+item.user.name+'</td><td>'+item.foodItem.name+'</td><td>'+item.foodItem.price+'</td><td>'+item.pay+'</td><td>'+dateStr+'</td></tr>'));
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

