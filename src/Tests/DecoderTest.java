package Tests;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DecoderTest {

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		String url = "https://www.baidu.com/s?wd=A%26B&rsv_spt=1&rsv_iqid=0xe55612b20061b224&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=cn&tn=baiduhome_pg&rsv_enter=1&oq=URLDecoder%20java&rsv_t=10e5N2I7wXM6tyaOSvxvHV%2BsIJ9xJP8hDjiRwbe5sSbfJ7kO62q9e85SXfzhqx%2BADmdK&sug=decoder&inputT=3673&rsv_pq=97ce165d005dbf02&rsv_sug3=47&rsv_sug1=33&rsv_sug7=101&rsv_sug2=0&rsv_sug4=3673";
		url = URLDecoder.decode(url, "UTF-8");
		System.out.println(url);
		
		String str1 = "hello";
		String str2 = "world";
		System.out.println(str1.concat(str2));
	}

}
