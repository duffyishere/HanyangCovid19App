package crawl;

import java.sql.Connection;
import java.util.Calendar;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;

class Api {
//	public String result;

	public int ApiExploere() throws IOException{
		Calendar cal = Calendar.getInstance();
		int year = cal.get(cal.YEAR);
		int month = cal.get(cal.MONTH)+1;
		int date = cal.get(cal.DATE);
		String now_year = Integer.toString(year);
		String now_month = Integer.toString(month);
		String now_date = Integer.toString(date);
		String now = now_year + now_month + now_date;
		StringBuilder urlBuilder = new StringBuilder("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=w4d0CAknq2zZ062cWDUPEyqQFR0fOWNn%2FlwQ0VLZ7%2B%2F%2FjILxJT%2Fs5GutiXjSOP5pE6iKqjGxvD0BLPleW3DllQ%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("startCreateDt","UTF-8") + "=" + URLEncoder.encode(now, "UTF-8")); /*검색할 생성일 범위의 시작*/
        urlBuilder.append("&" + URLEncoder.encode("endCreateDt","UTF-8") + "=" + URLEncoder.encode(now, "UTF-8")); /*검색할 생성일 범위의 종료*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
//        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        
        String result1 = sb.toString();
        String result2 = result1.substring(result1.indexOf("<item>", 7000) ,result1.indexOf("</item>", 7300));  
        int location = result2.indexOf(">", 70);
        String result3 = result2.substring(location+9, result2.indexOf("</defCnt>"));
//        System.out.println(result3);
        int result = Integer.parseInt(result3);
        return result;
	}
	
}

class MyDB {
	static Api api = new Api();
	
	public MyDB() throws IOException {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(cal.YEAR);
		int month = cal.get(cal.MONTH)+1;
		int date = cal.get(cal.DATE);
		String now_year = Integer.toString(year);
		String now_month = Integer.toString(month);
		String now_date = Integer.toString(date);
		String now = now_year + now_month + now_date;
		int now2 = Integer.parseInt(now);
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC";
		String uid = "root";
		String upw = "s2717244";
//		String query = "insert into macintosh values(?, ?)";
//		int date = 20221123;
		int defCnt = api.ApiExploere();
		
		
		try {
			Class.forName(driver);
			System.out.println("Driver Loading Ok");
		
			Connection con = DriverManager.getConnection(url, uid, upw);
			System.out.println("DB Conneck Ok");
			
			Statement statement = con.createStatement();
			String sql;
			
			PreparedStatement pstmt = null;
			sql = "insert into macintosh values (? ,?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, now2);
			pstmt.setInt(2, defCnt);
			pstmt.executeUpdate();
			int affectedCount = statement.executeUpdate(sql);
			System.out.println("affectedCount = " + affectedCount);
			sql = "select date, defCnt from macintosh";
			ResultSet rs = statement.executeQuery(sql);
			
			int count=0;
			while (rs.next()) {
				int dates = rs.getInt("date");
				int def = rs.getInt("date");
				
				String result ="";
				result = result.format("t%d/t%d", dates, def);
				System.out.println(result);
			}
			

		}catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
public class Main{
	static Api api = new Api();
	public static void main(String[] args) throws IOException {
		
		System.out.println(api.ApiExploere());
		new MyDB();
	}
}
