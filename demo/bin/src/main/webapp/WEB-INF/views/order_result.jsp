<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
	<title>Payple Global Payment</title>
	<link rel="stylesheet" href="/css/style.css" type="text/css"/>
	<!-- mobile setting -->
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
	<meta name="theme-color" content="#7852e8" />
	<meta name="msapplication-navbutton-color" content="#7852e8" />
	<meta name="apple-mobile-web-app-status-bar-style" content="#7852e8" />
	<!-- mobile setting end-->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
</head>

<script type="text/javascript">
	$(document).ready(function() {

		const result = '${result}';
		const api_id = '${api_id}';
		const service_oid = '${service_oid}';
		const comments = '${comments}';
		const totalAmount = '${totalAmount}';
		const currency = '${currency}';
		const resultUrl = '${resultUrl}';

		// 결제 성공
		if (result === 'A0000') {
			$('#payConfirmCancel').css('display', 'inline');
		}

		const payCancelAction = function gpayCancelConfirmAction () {
			const con = "승인취소요청을 전송합니다. \n 진행하시겠습니까? ";
			if (confirm(con) == true) {
				// 버튼 중복클릭 방지
				$('#payConfirmCancel').unbind('click');

				let formData = new FormData();
				formData.append('comments', comments);
				formData.append('service_oid', service_oid);
				formData.append('pay_id', api_id);
				formData.append('totalAmount', totalAmount);
				formData.append('currency', currency);
				formData.append('resultUrl', resultUrl);
          
				$.ajax({
					type: 'POST',
					cache: false,
					processData: false,
					contentType: false,
					async: false,
					url: '/cancel',
					dataType: 'json',
					data: formData,
					success: function(res) {
						console.log(res);

						if (res.result === 'A0000') {
							alert(res.message);
							$('#payConfirmCancel').css('display', 'none');

						} else {
							// 결제취소 실패시, 취소버튼 클릭 가능하게
							$('#payConfirmCancel').bind('click', function() {
								payCancelAction();
							});
							if (res.message) {
								alert(res.message)
							} else {
								alert('승인취소 요청 실패');
							}
						}
						let table_data = "";

						$.each(res, function (key, value) {
								table_data += '<tr><td>'+key+'</td><td> '+value+'</td><tr>';
						});

						$('#payRefundResult').append(table_data);
					},
					error: function(err) {
						console.log(err);
						// 결제취소 실패시, 취소버튼 클릭 가능하게
						$('#payConfirmCancel').bind('click', function() {
							payCancelAction();
						});
					}
				});
			}
		}

		$('#payConfirmCancel').on('click', function() {
			payCancelAction();
		});
	});

</script>

<body>
	<div class="device__layout w-600" id="responseBody">
		<div class="line_setter">
			<h4 class="tit__device mb-32">
				<img class="logo_in_text__md" src="/images/logo_full.svg" alt="" />
				<b>해외결제 API - 결제결과</b>
			</h4>
			<br /><br />
			<div id="payResTable">
				<b>Response (일반결제 결과)</b><br /><br />
				<div class="table-outter" id="payResult">
					<table class="model-01">
						<colgroup>
							<col style="width:50%;">
							<col style="width:50%;">
						</colgroup>
						<tr>
							<th>파라미터 항목</th>
							<th>파라미터 값</th>
						</tr>
						<tr>
							<td>type</td>
							<td>${type}</td>
						</tr>
						<tr>
							<td>result</td>
							<td>${result}</td>
						</tr>
						<tr>
							<td>message</td>
							<td>${message}</td>
						</tr>
						<tr>
							<td>resultUrl</td>
							<td>${resultUrl}</td>
						</tr>
						<tr>
							<td>api_id</td>
							<td>${api_id}</td>
						</tr>
						<tr>
							<td>api_date</td>
							<td>${api_date}</td>
						</tr>
						<tr>
							<td>service_oid</td>
							<td>${service_oid}</td>
						</tr>
						<tr>
							<td>comments</td>
							<td>${comments}</td>
						</tr>
						<tr>
							<td>pay_type</td>
							<td>${pay_type}</td>
						</tr>
						<tr>
							<td>card_number</td>
							<td>${card_number}</td>
						</tr>
						<tr>
							<td>totalAmount</td>
							<td>${totalAmount}</td>
						</tr>
						<tr>
							<td>currency</td>
							<td>${currency}</td>
						</tr>
						<tr>
							<td>firstName</td>
							<td>${firstName}</td>
						</tr>
						<tr>
							<td>lastName</td>
							<td>${lastName}</td>
						</tr>
						<tr>
							<td>address1</td>
							<td>${address1}</td>
						</tr>
						<tr>
							<td>locality</td>
							<td>${locality}</td>
						</tr>
						<tr>
							<td>administrativeArea</td>
							<td>${administrativeArea}</td>
						</tr>
						<tr>
							<td>postalCode</td>
							<td>${postalCode}</td>
						</tr>
						<tr>
							<td>country</td>
							<td>${country}</td>
						</tr>
						<tr>
							<td>email</td>
							<td>${email}</td>
						</tr>
						<tr>
							<td>phoneNumber</td>
							<td>${phoneNumber}</td>
						</tr>
						<tr>
							<td>billing_key</td>
							<td>${billing_key}</td>
						</tr>
						<tr>
							<td>submitTimeUtc</td>
							<td>${submitTimeUtc}</td>
						</tr>
					</table>
				</div>
				<div class="btn_box has_space align_center">
					<button class="btn cl_main btn_rounded btn_md" type="button" id="payConfirmCancel" style="display:none">
						결제승인취소
					</button>
				</div>
			</div>
			<b>Response (취소 결과)</b><br /><br />
			<div class="table-outter">
				<table class="model-01" id="payRefundResult">
					<colgroup>
						<col style="width:50%;">
						<col style="width:50%;">
					</colgroup>
					<tr>
						<th>파라미터 항목</th>
						<th>파라미터 값</th>
					</tr>
				</table>
			</div>
		</div>
	</div>
</body>

</html>