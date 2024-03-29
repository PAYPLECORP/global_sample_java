<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
  <title>Payple Global Payment - BillingKey</title>
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
    let service_oid = "${service_oid}";                  // [선택] 주문번호
    const comments = "${comments}";                        // [필수] 상품명
    const billing_key = "${billing_key}"                   // [필수] 빌링키 (카드정보를 암호화 한 키 값)
    const securityCode = "${securityCode}"                 // [필수] 카드 CVC/CVV 번호
    const totalAmount = "${totalAmount}";                  // [필수] 결제 요청금액
    const currency = "${currency}";                        // [필수] 통화
    const firstName = "${firstName}";                      // [선택] 카드소유주 이름 (보내지 않을 경우, 최초 결제시 입력한 카드소유주 이름으로 결제요청이 됩니다.)
    const lastName = "${lastName}";                        // [선택] 카드소유주 성 (보내지 않을 경우, 최초 결제시 입력한 카드소유주 성으로 결제요청이 됩니다.)
    const email = "${email}";                              // [선택] 이메일 주소  (보내지 않을 경우, 최초 결제시 입력한 이메일 주소로 결제요청이 됩니다.)
    const resultUrl = "${resultUrl}";                      // [선택] 해당 파라미터(resultUrl)는 별도의 기능은 하지 않으나, 파트너사에서 빌링키 결제 성공시 리다이렉트 하는 등 활용할 수 있는 파라미터입니다.
    let api_id = "";

    $('#payBillingKey').on('click', function() {
      var con = "빌링키 결제요청을 전송합니다. 진행하시겠습니까?\n";
      
      if (confirm(con) == true) { 
        // 버튼 중복클릭 방지
        console.log('전송 승인')
        $('#payBillingKey').unbind('click');

        let formData = new FormData();
        // formData.append('service_oid', service_oid);
        formData.append('comments', comments);
        formData.append('billing_key', billing_key);
        formData.append('securityCode', securityCode);
        formData.append('totalAmount', totalAmount);
        formData.append('currency', currency);
        formData.append('firstName', firstName);
        formData.append('lastName', lastName);
        formData.append('email', email);
        formData.append('resultUrl', resultUrl);

        $.ajax({
          type: 'POST',
          cache: false,
          processData: false,
          contentType: false,
          async: false,
          url: '/billingKey',
          dataType: 'json',
          data: formData,
          success: function(res) {
            console.log(res);

            $('#billingOrderBody').css('display', 'none'); 
            $('#responseBody').css('display', 'block');

            if (res.result === 'A0000') {
              alert(res.message);
              $('#payConfirmCancel').css('display', 'block');
              
              // api_id Settings
              api_id = res.api_id;
              service_oid = res.service_oid;

            } else {
              if (res.message) {
                alert(res.message)
              } else {
                alert('빌링키 결제 요청 실패');
              }
            }
            let table_data = "";

            $.each(res, function (key, value) {
                table_data += '<tr><td>'+key+'</td><td>'+value+'</td><tr>';
            });

            $('#billingResult').append(table_data);
          }, 
          error: function(err) {
            console.log(err);
          }
        });
      }
    })

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
  <!-- 빌링키 결제 (Order Confirm) -->
  <div class="device__layout w-600" id="billingOrderBody">
    <div class="line_setter">
      <h4 class="tit__device mb-32">
        <img class="logo_in_text__md" src="/images/logo_full.svg" alt="" />
        <b>해외결제 API - 빌링키 결제</b>
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
            <td>빌링키</td>
            <td>${billing_key}</td>
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
        <button class="btn cl_main btn_rounded btn_md" type="button" id="payBillingKey">빌링키 결제하기</button>
      </div>
    </div>
  </div>
  <!-- 빌링키 결제 결과 -->
  <div class="device__layout w-600" id="responseBody" style="display:none">
    <div class="line_setter">
      <h4 class="tit__device mb-32">
        <img class="logo_in_text__md" src="/images/logo_full.svg" alt="" />
        해외결제 결과
      </h4>
      <br /><br />
      <div id="billingTable">
        <b>Billing Key Response (빌링키 결제 결과)</b><br /><br />
        <div class="table-outter">
          <table class="model-01" id='billingResult'>
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