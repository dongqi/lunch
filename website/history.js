$(document).ready(function() {
	console.log('history...');
	$('#datetimepicker').datetimepicker({
		locale: 'zh_cn'
		,format: 'YYYYMMDD'
	});

	$('#queryBtn').on('click', function() {
		var date = $('#datetimepicker').data('DateTimePicker').date();
		console.log('....', date.format('YYYYMMDD'));

		
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
});