var remote = 'http://127.0.0.1:9527';
$(document).ready(function() {
	var currentDate = new Date();
	var foods = [];
	requestServer('GET', remote+'/get', {}, function(json) {
		console.log(json);
			
		$.each(json, function(i, item) {
			var data = '<div class="col-xs-6 col-md-4">';
		  	data += '<div class="thumbnail">';
		  	data += '<div class="caption">';
		  	data += '<h3>'+item.name+'</h3>';
		  	data += '<p>'+item.price+'元</p>';
		  	data += '<p><a value1="'+item.id+'" href="#" class="btn btn-primary" role="button" id="ding_'+item.id+'">订</a> <a value2="'+item.id+'" id="tui_'+item.id+'" href="#" class="btn btn-default" role="button">退</a></p>';
		  	data += '</div>';
		  	data += '</div>';
		  	data += '</div>';
		  	$('#foodItems').after(data);
		});
		foods = json;
		//订
		$('a[value1]').on('click', ordering);
		//退
		$('a[value2]').on('click', cancel);
	});
	
	$('#myModal').on('hidden.bs.modal', function (e) {
		$(this).find(':text').val('');
		$(this).find('#orderConfirm').text('');
		console.log('hidden.bs.modal');
	});
	
	$('#myModal').find('#btnOK').on('click', function() {
		var _id = $('#myModal').find('#orderConfirm').attr('value');
		var _email = $('#myModal').find('#account').val();
		console.log('确定', _email, _id);
		if(_email.length == 0) {
			alert('请输入你的数天邮箱账号');
			return;
		}
		console.log('订单确定按钮事件:', _id);
		requestServer('GET', remote+'/ordering', {id: _id, email: _email}, function(json) {
			if(json.success) {
				alert('下单成功');
				$('#myModal').hidden();
			} else {
				alert('下单失败');
			}
		});
	});
	
	$('#foodInput').on('click', function() {
		$('#foodList').modal('toggle');
	});
	
	$('#foodList').find('#btnInput').on('click', function() {
	    var content = $('#foodList').find('#inputContent').val();
	    var data = {menuContent : content};
	    
	    requestServer('GET', remote+'/upload', data, function(json) {
	    	alert('上传成功');
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
		console.log('订-按钮事件');
		var id = $(this).attr('value1');
		var orderInfo = null;
		for(var index = 0; index < foods.length; index++) {
			if(foods[index].id == id) {
				orderInfo = foods[index];
				break;
			}
		}
		console.log(orderInfo);
		$('#myModal').find('#orderConfirm').text(orderInfo.name+' '+orderInfo.price);
		$('#myModal').find('#orderConfirm').attr('value', orderInfo.id);
		$('#myModal').modal('toggle');
	}

	function cancel() {
		console.log('退-按钮事件', $('#myModal'));
	}
});

