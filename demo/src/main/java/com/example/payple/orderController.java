package com.example.payple;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class orderController extends GlobalPaypleController {
	
	String service_id = "demo"; // 테스트 ID

	/*
	 * order.jsp : 결제정보 페이지
	 */
	@RequestMapping(value = "/order")
	public String order(Model model) {
		model.addAttribute("service_id", service_id); // 파트너 ID
		model.addAttribute("service_oid", ""); // 주문번호
		model.addAttribute("comments", "Payple global payments"); // 상품명
		model.addAttribute("totalAmount", "1.00"); // 결제 요청금액
		model.addAttribute("currency", "USD"); // 통화
		model.addAttribute("firstName", "Payple"); // 카드소유주 이름
		model.addAttribute("lastName", "Inc"); // 카드소유주 성
		model.addAttribute("country", "KR"); // 국가
		model.addAttribute("address1", "14, Teheran-ro 34-gil, Gangnam-gu"); // 도로명
		model.addAttribute("locality", "Seoul"); // 시/구/군
		model.addAttribute("postalCode", "06220"); // 우편번호
		model.addAttribute("email", "test@payple.kr"); // 이메일 주소
		model.addAttribute("phoneNumber", "01012345678"); // 휴대전화 번호

		return "order";
	}

	/*
	 * order_confirm.jsp : 결제창 호출 페이지
	 */
	@RequestMapping(value = "/confirm")
	public String order_confirm(HttpServletRequest request, Model model) {

		model.addAttribute("service_id", service_id);
		model.addAttribute("service_oid", request.getParameter("service_oid"));
		model.addAttribute("comments", request.getParameter("comments"));
		model.addAttribute("totalAmount", request.getParameter("totalAmount"));
		model.addAttribute("currency", request.getParameter("currency"));
		model.addAttribute("lastName", request.getParameter("lastName"));
		model.addAttribute("firstName", request.getParameter("firstName"));
		model.addAttribute("phoneNumber", request.getParameter("phoneNumber"));
		model.addAttribute("email", request.getParameter("email"));
		model.addAttribute("country", request.getParameter("country"));
		model.addAttribute("address1", request.getParameter("address1"));
		model.addAttribute("locality", request.getParameter("locality"));
		model.addAttribute("administrativeArea", request.getParameter("administrativeArea"));
		model.addAttribute("postalCode", request.getParameter("postalCode"));
		model.addAttribute("isDirect", request.getParameter("isDirect"));
		model.addAttribute("resultUrl", "http://localhost:8080/result"); // 결제결과 반환 URL

		// 파트너 인증 토큰 발급
		JSONObject obj = new JSONObject();
		obj = partnerTokenRequest();

		// 파트너 인증 토큰 발급 후 결제요청 시 필요한 필수 파라미터
		model.addAttribute("access_token", obj.get("access_token")); // 발급받은 Access Token
		/*
		 * 테스트 결제인 경우에만 필수로 보내는 파라미터(payCls) payCls는 파트너 인증 토큰발급 응답값으로 반환되는 값이며, 테스트
		 * 결제시에만 필요합니다. 테스트 결제시 payCls = 'demo'
		 */
		model.addAttribute("payCls", obj.get("payCls"));

		return "order_confirm";
	}

	/*
	 * order_result.jsp : 결제결과 확인 페이지
	 */
	@RequestMapping(value = "/result")
	public String order_result(HttpServletRequest request, Model model) {

		// 1. 결제결과 모두 출력
		Enumeration<String> params = request.getParameterNames();
		String result = "";

		while (params.hasMoreElements()) {
			String name = (String) params.nextElement();
			result += name + " => " + request.getParameter(name) + "<br>";
		}
		model.addAttribute("result", result);

		// 2. 결제결과 파라미터로 받기 - 응답 파라미터를 받아서 활용해보세요.
		model.addAttribute("type", request.getParameter("type")); // 요청종류 [결제: PAYMENT | 취소: CANCEL]
		model.addAttribute("result", request.getParameter("result")); // 응답 코드
		model.addAttribute("message", request.getParameter("message")); // 응답 메시지
		model.addAttribute("resultUrl", request.getParameter("resultUrl")); // 결제결과 반환(Return) URL
		model.addAttribute("api_id", request.getParameter("api_id")); // 결제 요청 고유키
		model.addAttribute("api_date", request.getParameter("api_date")); // 결제 시간 (페이플 서버기준: GMT +9)
		model.addAttribute("service_oid", request.getParameter("service_oid")); // 주문번호
		model.addAttribute("comments", request.getParameter("comments")); // 상품명
		model.addAttribute("pay_type", request.getParameter("pay_type")); // 결제수단
		model.addAttribute("card_number", request.getParameter("card_number")); // 카드번호 (일부 마스킹 처리)
		model.addAttribute("totalAmount", request.getParameter("totalAmount")); // 결제 요청금액
		model.addAttribute("currency", request.getParameter("currency")); // 통화
		model.addAttribute("firstName", request.getParameter("firstName")); // 카드소유주 이름
		model.addAttribute("lastName", request.getParameter("lastName")); // 카드소유주 성
		model.addAttribute("address1", request.getParameter("address1")); // 도로명
		model.addAttribute("locality", request.getParameter("locality")); // 시/구/군
		model.addAttribute("administrativeArea", request.getParameter("administrativeArea")); // 도/시 (국가가 미국(US), 혹은
																								// 캐나다(CA)인 경우에는 선택한 도/시
																								// 코드가 반환됩니다.)
		model.addAttribute("postalCode", request.getParameter("postalCode")); // 우편번호
		model.addAttribute("country", request.getParameter("country")); // 국가
		model.addAttribute("email", request.getParameter("email")); // 이메일 주소
		model.addAttribute("phoneNumber", request.getParameter("phoneNumber")); // 휴대전화 번호
		model.addAttribute("billing_key", request.getParameter("billing_key")); // 빌링키 (카드정보를 암호화 한 키 값)
		model.addAttribute("submitTimeUtc", request.getParameter("submitTimeUtc")); // 결제 시간 (사용자 입장에서 기준이 되는 결제시간: GMT)

		return "order_result";
	}

	/*
	 * Cancel : 결제취소
	 */
	@ResponseBody
	@PostMapping(value = "/cancel")
	public JSONObject payCancel(HttpServletRequest request) {
		JSONObject resultObj = new JSONObject(); 
		
		// 파트너 인증 토큰 발급
		JSONObject obj = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		
		obj = partnerTokenRequest();

		// 파트너 인증 응답값
		String access_token = (String) obj.get("access_token");		// [필수] 발급받은 Access Token

		// 결제취소 요청 파라미터
		// service_id												// [필수] 파트너 ID
		String comments = request.getParameter("comments"); 		// [필수] 상품명
		String service_oid = request.getParameter("service_oid"); 	// [필수] 주문번호
		String pay_id = request.getParameter("pay_id"); 			// [필수] 취소할 결제건의 api_id
		String totalAmount = request.getParameter("totalAmount"); 	// [필수] 결제 취소 요청금액
		String currency = request.getParameter("currency"); 		// [필수] 통화
		String resultUrl = request.getParameter("resultUrl"); 		// [선택] 그대로 응답 파라미터로 반환

		try {

			// 결제취소 요청 전송
			JSONObject cancelObj = new JSONObject();

			cancelObj.put("service_id", service_id);
			cancelObj.put("comments", comments);
			cancelObj.put("service_oid", service_oid);
			cancelObj.put("pay_id", pay_id);
			cancelObj.put("totalAmount", totalAmount);
			cancelObj.put("currency", currency);
			cancelObj.put("resultUrl", resultUrl);

			String cancelURL = "https://demo-api.payple.kr/gpay/cancel"; // TEST
			// String cancelURL = "https://api.payple.kr/gpay/cancel"; // REAL
			
			URL url = new URL(cancelURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("content-type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + access_token); 	// [필수] 발급받은 Access Token
			con.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(cancelObj.toString());
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();

			JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());
			
			if(jsonObject.get("result").equals("A0000")) {
				JSONObject resultInfoObj = (JSONObject) jsonObject.get("info");
				
				resultObj.put("type", jsonObject.get("type"));
				resultObj.put("result", jsonObject.get("result"));
				resultObj.put("message", jsonObject.get("message"));
				resultObj.put("resultUrl", jsonObject.get("resultUrl"));
				resultObj.put("api_date", jsonObject.get("api_date"));
				// info Data
				resultObj.put("service_oid", resultInfoObj.get("service_oid"));
				resultObj.put("totalAmount", resultInfoObj.get("totalAmount"));
				resultObj.put("currency", resultInfoObj.get("currency"));
				resultObj.put("submitTimeUtc", resultInfoObj.get("submitTimeUtc"));
			} else {
				resultObj.put("result", jsonObject.get("result"));
				resultObj.put("message", jsonObject.get("message"));
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultObj;

	}

	// 빌링키 결제 (billingKey.jsp)
	@RequestMapping(value = "/order_billingKey")
	public String paySimpleSendRoute(HttpServletRequest request, Model model) {

		model.addAttribute("service_oid", request.getParameter("service_oid"));
		model.addAttribute("comments", request.getParameter("comments"));
		model.addAttribute("billing_key", request.getParameter("billing_key"));
		model.addAttribute("securityCode", request.getParameter("securityCode"));
		model.addAttribute("totalAmount", request.getParameter("totalAmount"));
		model.addAttribute("currency", request.getParameter("currency"));
		model.addAttribute("lastName", request.getParameter("lastName"));
		model.addAttribute("firstName", request.getParameter("firstName"));
		model.addAttribute("phoneNumber", request.getParameter("phoneNumber"));
		model.addAttribute("email", request.getParameter("email"));
		model.addAttribute("country", request.getParameter("country"));
		model.addAttribute("address1", request.getParameter("address1"));
		model.addAttribute("locality", request.getParameter("locality"));
		model.addAttribute("administrativeArea", request.getParameter("administrativeArea"));
		model.addAttribute("postalCode", request.getParameter("postalCode"));
		model.addAttribute("isDirect", request.getParameter("isDirect"));
		model.addAttribute("resultUrl", "http://localhost:8080/result"); // 결제결과 반환 URL

		return "order_billingKey";
	}

	/*
	 * billingKey : 빌링키 결제
	 */
	@ResponseBody
	@PostMapping(value = "/billingKey")
	public JSONObject paySimpleSend(HttpServletRequest request) {
		
		JSONObject resultObj = new JSONObject(); 

		// 파트너 인증 토큰 발급
		JSONObject obj = new JSONObject();
		JSONParser jsonParser = new JSONParser();

		obj = partnerTokenRequest();

		// 파트너 인증 응답값
		String access_token = (String) obj.get("access_token"); // [필수] 발급받은 Access Token

		// 빌링키 요청 파라미터
		// service_id 																																	// [필수] 파트너 ID
		// String service_oid = request.getParameter("service_oid"); 										// [선택] 주문번호
		String comments = request.getParameter("comments"); 											// [필수] 상품명
		String billing_key = request.getParameter("billing_key"); 								// [필수] 빌링키 (카드정보를 암호화 한 키 값)
		String securityCode = request.getParameter("securityCode"); 							// [필수] 카드 CVC/CVV 번호
		String totalAmount = request.getParameter("totalAmount"); 								// [필수] 결제 요청금액
		String currency = request.getParameter("currency"); 											// [필수] 통화
		String firstName = request.getParameter("firstName"); 										// [선택] 카드소유주 이름 (보내지 않을 경우, 최초 결제시 입력한 카드소유주 이름으로 결제요청이 됩니다.)
		String lastName = request.getParameter("lastName"); 											// [선택] 카드소유주 성 (보내지 않을 경우, 최초 결제시 입력한 카드소유주 성으로 결제요청이 됩니다.)
		String country = request.getParameter("country"); 												// [선택] 국가 (보내지 않을 경우, 최초 결제시 입력한 국가로 결제요청이 됩니다.)
		String administrativeArea = request.getParameter("administrativeArea"); 	// [선택] 도/시 (보내지 않을 경우, 최초 결제시 입력한 도/시로 결제요청이 됩니다.)
		String locality = request.getParameter("locality"); 											// [선택] 시/구/군 (보내지 않을 경우, 최초 결제시 입력한 시/구/군으로 결제요청이 됩니다.)
		String address1 = request.getParameter("address1"); 											// [선택] 도로명  (보내지 않을 경우, 최초 결제시 입력한 도로명으로 결제요청이 됩니다.)
		String postalCode = request.getParameter("postalCode"); 									// [선택] 우편번호  (보내지 않을 경우, 최초 결제시 입력한 우편번호로 결제요청이 됩니다.)
		String email = request.getParameter("email"); 														// [선택] 이메일 주소  (보내지 않을 경우, 최초 결제시 입력한 이메일 주소로 결제요청이 됩니다.)
		String phoneNumber = request.getParameter("phoneNumber"); 								// [선택] 휴대전화 번호  (보내지 않을 경우, 최초 결제시 입력한 휴대전화 번호로 결제요청이 됩니다.)
		String resultUrl = request.getParameter("resultUrl"); 										// [선택] 해당 파라미터(resultUrl)는 별도의 기능은 하지 않으나, 파트너사에서 빌링키 결제 성공시 리다이렉트 하는 등 활용할 수 있는 파라미터입니다.

		try {
			// 빌링키 결제 요청 전송
			JSONObject bilingObj = new JSONObject();

			bilingObj.put("service_id", service_id);
			// bilingObj.put("service_oid", service_oid);
			bilingObj.put("comments", comments);
			bilingObj.put("billing_key", billing_key);
			bilingObj.put("securityCode", securityCode);
			bilingObj.put("totalAmount", totalAmount);
			bilingObj.put("currency", currency);
			bilingObj.put("firstName", firstName);
			bilingObj.put("lastName", lastName);
			bilingObj.put("country", country);
			bilingObj.put("administrativeArea", administrativeArea);
			bilingObj.put("locality", locality);
			bilingObj.put("address1", address1);
			bilingObj.put("postalCode", postalCode);
			bilingObj.put("email", email);
			bilingObj.put("phoneNumber", phoneNumber);
			bilingObj.put("resultUrl", resultUrl);

			String bilingURL = "https://demo-api.payple.kr/gpay/billingKey"; // TEST
			// String bilingURL = "https://api.payple.kr/gpay/billingKey";	 // REAL
			
			URL url = new URL(bilingURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("content-type", "application/json");
			con.setRequestProperty("charset", "UTF-8");
			con.setRequestProperty("Authorization", "Bearer " + access_token); // [필수] 발급받은 Access Token
			con.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(bilingObj.toString().getBytes());
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();
			
			JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());
			
			if(jsonObject.get("result").equals("A0000")) {
				JSONObject resultInfoObj = (JSONObject) jsonObject.get("info");
				
				resultObj.put("type", jsonObject.get("type"));
				resultObj.put("result", jsonObject.get("result"));
				resultObj.put("message", jsonObject.get("message"));
				resultObj.put("resultUrl", jsonObject.get("resultUrl"));
				resultObj.put("api_id", jsonObject.get("api_id"));
				resultObj.put("api_date", jsonObject.get("api_date"));
				// info Data
				resultObj.put("service_oid", resultInfoObj.get("service_oid"));
				resultObj.put("comments", resultInfoObj.get("comments"));
				resultObj.put("pay_type", resultInfoObj.get("pay_type"));
				resultObj.put("billing_key", resultInfoObj.get("billing_key"));
				resultObj.put("totalAmount", resultInfoObj.get("totalAmount"));
				resultObj.put("currency", resultInfoObj.get("currency"));
				resultObj.put("firstName", resultInfoObj.get("firstName"));
				resultObj.put("lastName", resultInfoObj.get("lastName"));
				resultObj.put("address1", resultInfoObj.get("address1"));
				resultObj.put("locality", resultInfoObj.get("locality"));
				resultObj.put("administrativeArea", resultInfoObj.get("administrativeArea"));
				resultObj.put("postalCode", resultInfoObj.get("postalCode"));
				resultObj.put("country", resultInfoObj.get("country"));
				resultObj.put("email", resultInfoObj.get("email"));
				resultObj.put("phoneNumber", resultInfoObj.get("phoneNumber"));
				resultObj.put("card_number", resultInfoObj.get("card_number"));
				resultObj.put("submitTimeUtc", resultInfoObj.get("submitTimeUtc"));
			} else {
				resultObj.put("result", jsonObject.get("result"));
				resultObj.put("message", jsonObject.get("message"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultObj;
	}
}
