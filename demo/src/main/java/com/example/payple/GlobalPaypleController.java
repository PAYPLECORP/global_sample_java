package com.example.payple;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GlobalPaypleController {


	/**
	 * 파트너 인증 토큰 발급 메소드
	 * 
	 * @return JSONObject // 파트너 인증 토큰 발급 응답값
	 */
	public JSONObject partnerTokenRequest() {
		JSONObject jsonObject = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		try {

			// 파트너 인증 토큰 발급 Request URL
			String pURL = "https://demo-api.payple.kr/gpay/oauth/1.0/token"; // TEST (테스트)
			// String pURL = "https://api.payple.kr/gpay/oauth/1.0/token"; // REAL (운영)

			// 계정정보(service_id, service_key), 파트너용 토큰 확인 코드(code)
			String service_id = "demo";
			String service_key = "abcd1234567890";
			String code = "as12345678";
			
			JSONObject obj = new JSONObject();
			obj.put("service_id", service_id);
			obj.put("service_key", service_key);
			obj.put("code", code);
			 
			System.out.println("파트너 인증 토큰 발급 Request: " + obj.toString());

			URL url = new URL(pURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("content-type", "application/json");
			con.setRequestProperty("charset", "UTF-8");
			con.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(obj.toString().getBytes());
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

			jsonObject = (JSONObject) jsonParser.parse(response.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;

	}
	
}
