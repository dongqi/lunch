var remote = 'http://192.168.61.152:9527';
$(document).ready(function() {
	/*
	var foods = [ {
		'name' : '清烧丸子','id':1,
		'price' : 13
	}, {
		'name' : '青椒豆干回锅','id':2,
		'price' : 13
	}, {
		'name' : '藿香鸡','id':3,
		'price' : 15
	}, {
		'name' : '土豆烧排骨','id':4,
		'price' : 16
	}, {
		'name' : '红烧牛腩','id':5,
		'price' : 18
	}, {
		'name' : '梅菜扣肉','id':6,
		'price' : 12
	}, {
		'name' : '粉蒸排骨','id':7,
		'price' : 15
	}, {
		'name' : '土家香碗','id':8,
		'price' : 16
	}, {
		'name' : '苕子蒸蛋','id':9,
		'price' : 10
	}, {
		'name' : '凉拌耳片','id':10,
		'price' : 13
	}, {
		'name' : '三丝白肉','id':11,
		'price' : 13
	}, {
		'name' : '麻辣鸡块','id':12,
		'price' : 15
	} ];
	*/
	var foods = [];
	$.ajax({
		type : 'GET',
		async : false,
		url : remote+'/get',
		dataType : 'jsonp',
		jsonp : 'callbackparam',
		jsonpCallback: 'success_jsonpCallback',
		success : function(json) {
			console.log(json);
			/**/
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
		}
	});

	/*
	$.each(foods, function(i, item) {
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
	*/
	
	
	//退
	$('a[value2]').on('click', function() {
		console.log('退按钮事件', $('#myModal'));
		
	});
	
	$('#myModal').on('hidden.bs.modal', function (e) {
		$(this).find(':text').val('');
		$(this).find('#orderConfirm').text('');
		console.log('hidden.bs.modal');
	});
	
	$('#myModal').find('#btnOK').on('click', function() {
		console.log('订单确定按钮事件');
		
	});
	
	$('#foodInput').on('click', function() {
		$('#foodList').modal('toggle');
	});
	
	$('#foodList').find('#btnInput').on('click', function() {
	    var content = $('#foodList').find('#inputContent').val();
	    console.log(content);
	    var data = {menuContent : content};
	    console.log('remote:', remote);
	    $.ajax({
	         type: "get",
	         async: false,
	         data: data,
	         url: remote+'/upload',
	         dataType: "jsonp",
	         jsonp: "callbackparam", //服务端用于接收callback调用的function名的参数
	         jsonpCallback: "success_jsonpCallback", //callback的function名称,服务端会把名称和data一起传递回来
	         success: function(json) {
	             console.log(json);
	             alert('上传成功');
	         }
	     });
    });

    function ordering() {
		console.log('订按钮事件');
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
		$('#myModal').modal('toggle');
	}
});

