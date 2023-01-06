<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
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

<!-- 서버 환경별 페이플 해외카드 결제 스크립트 -->
<script src="https://demo-gpay.payple.kr/common/js/gpay-1.0.1.js"></script>
<!--<script src="https://gpay.payple.kr/common/js/gpay-1.0.1.js"></script> -->
<script>
	$(document).ready(function() {
		const orderConfirmFormSubmit = function gpayOrderConfirmFormSubmit() {
			// 버튼 중복클릭 방지
			// $('#gpayOrderFormSubmit').unbind('click');

			/**
			 * 결제요청 파라미터
			 * 결제요청 파라미터 중 필수가 아닌 선택 파라미터를 보내시면 결제창에 미리 해당 값을 입력하는 기능을 수행합니다.
			 * 고객이 입력해야하는 필드값을 줄어들게 하는 효과가 있습니다.
			 * (단, 결제창에 필드값만 해당 - service_oid , isDirect 제외)
			 */
			let obj = {};
      		obj.Authorization = "${access_token}";			// [필수] 발급받은 Access Token
			obj.service_id = "${service_id}"; 				// [필수] 파트너 ID
			obj.service_oid = "${service_oid}"; 			// [선택] 주문번호(미지정하는 경우 페이플에서 임의로 지정)
			obj.comments = "${comments}"; 					// [필수] 상품명
			obj.totalAmount = "${totalAmount}"; 			// [필수] 결제 요청금액
			obj.currency = "${currency}"; 					// [필수] 통화
			obj.firstName = "${firstName}"; 				// [선택] 카드소유주 이름
			obj.lastName = "${lastName}"; 					// [선택] 카드소유주 성
			obj.email = "${email}"; 						// [선택] 이메일 주소
			obj.phoneNumber = "${phoneNumber}"; 			// [선택] 휴대전화 번호
			/**
			 * [필수] 결제결과 반환(Return) URL
			 * - 결제완료 후 결제결과 파라미터와 함께 리다이렉트 되는 URL 로, 
			 *   테스트 시 파트너사가 테스트 할 경로를 알맞게 설정하셔야 정상적으로 결과를 받을 수 있습니다.
			 */
			obj.resultUrl = "${resultUrl}";
			/*
			 *  테스트 결제인 경우에만 필수로 보내는 파라미터(payCls)
			 *  payCls는 파트너 인증 토큰발급 응답값으로 반환되는 값이며,
			 *  테스트 결제시에만 필요합니다.
			 *  테스트 결제시 payCls = 'demo'
			 */
			obj.payCls = "${payCls}";
			obj.isDirect = "${isDirect}"; 						// [선택] 결제창 호출 다이렉트 여부 ("" | "Y")

			paypleGpayPaymentRequest(obj);
		}

		$('#gpayOrderFormSubmit').on('click', function(event) {
			orderConfirmFormSubmit();
		});

		// 뒤로가기 이벤트 발생하면 버튼 Click Bind
		window.onpageshow = function(event) {
			if (event.persisted || (window.performance && window.performance.navigation.type == 2)) {
				$('#gpayOrderFormSubmit').bind('click', function() {
					orderConfirmFormSubmit();
				});
			}
		}

	});
</script>

<body>
	<!-- 일반 결제 (Order Confirm) -->
	<div class="device__layout w-600">
		<div class="line_setter">
			<h4 class="tit__device mb-32">
				<img class="logo_in_text__md" src="/images/logo_full.svg" alt="" />
				<b> 해외결제 API - 결제창 호출</b>
			</h4>
			<div class="table-outter">
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
						<td>주문번호</td>
						<td>${service_oid}</td>
					</tr>
					<tr>
						<td>결제고객 이름</td>
						<td>${lastName} ${firstName}</td>
					</tr>
					<tr>
						<td>결제고객 휴대전화번호</td>
						<td>${phoneNumber}</td>
					</tr>
					<tr>
						<td>결제고객 이메일</td>
						<td>${email}</td>
					</tr>
					<tr>
						<td>상품명</td>
						<td>${comments}</td>
					</tr>
					<tr>
						<td>결제금액</td>
						<td>${currency} ${totalAmount}</td>
					</tr>
				</table>
			</div>
			<div class="btn_box has_space align_center">
				<div>
					결제창 호출방식 : ${isDirect}
				</div>
				<button class="btn cl_main btn_rounded btn_md" type="button" id="gpayOrderFormSubmit">해외결제하기</button>
			</div>
		</div>
	</div>
</body>

</html>
