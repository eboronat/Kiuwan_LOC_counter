package eb.kiuwan;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


public class counterMaxLOC {
	
	public static void main(String[] args) throws ParseException, IOException, URISyntaxException {
		
		if (args.length != 2) {
			System.out.println("Please pass username and password as arguments.");
			return;
		}
		
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet("https://api.kiuwan.com/applications");
		RequestConfig params = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
		get.setConfig(params);
		
		String name = args[0];
		String password = args[1];
		String authString = name + ":" + password;
        byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		
		get.addHeader("Authorization", "Basic "+authStringEnc);
		get.addHeader("Content-Type", "application/json");
		
        HttpResponse response = httpclient.execute(get);
        String json_response = EntityUtils.toString(response.getEntity());
        JSONArray jsonArr = new JSONArray(json_response);
        
		long sum = 0;

        for (int i = 0; i < jsonArr.length(); i++) {
            String app = jsonArr.getJSONObject(i).get("name").toString();
            System.out.println(app);
			String str = "https://api.kiuwan.com/apps/"+app;
			get = new HttpGet(str.replace(" ", "%20"));
			get.addHeader("Authorization", "Basic "+authStringEnc);
			get.addHeader("Content-Type", "application/json");
			
			response = httpclient.execute(get);
			json_response = EntityUtils.toString(response.getEntity());
			JSONObject jsonobj = new JSONObject(json_response);
			try {
				String date = jsonobj.getString("date");
				long lines = jsonobj.getJSONArray("Main metrics").getJSONObject(5).getLong("value");
				System.out.println(lines+" on " + date +"\n");
				sum = sum + lines;
			} catch(Exception e) {
				System.out.println("No analysis\n");
			}
        }
        
		System.out.println("TOTAL LOC = "+sum);
	}
}