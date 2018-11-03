import org.junit.Test;
import unit.HttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpTest {

//    @Test
    public  void GetFromHtml() throws IOException {
//		int ret=0;
        String contentEncoding;
        HttpURLConnection url=null;
        String htmladdr="http://s2.ph.126.net/nN-QtfziLzU0CqQV6YvQNA==/267181362483110.js";
        StringBuffer buffer=new StringBuffer("");
        try {
            URL url1 = new URL(htmladdr);


            url =   (HttpURLConnection)url1.openConnection ();
            url.setRequestProperty("User-Agent", "mozlla/5.0");
            url.setRequestProperty("Accept-Encoding", "gzip, deflate");
            url.connect();


            contentEncoding=url.getContentEncoding();
            System.out.println(contentEncoding);

        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }
        if(url!=null){
            InputStream stream;
//		if ((null != contentEncoding)&& (-1 != contentEncoding.indexOf ("gzip"))){
//		                   stream = new GZIPInputStream (
//		                     url.getInputStream ());
//		            }
//		            else if ((null != contentEncoding)
//		                   && (-1 != contentEncoding.indexOf ("deflate")) )
//		            {
//		                   stream = new InflaterInputStream (
//		                      url.getInputStream ());
//		            }
//		            else
//		            {
            stream = url.getInputStream();
//		            }

//		InputStream stream= url.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String temp;
            while (null != (temp = reader.readLine())) {
                 temp=new String(temp.getBytes("iso-8859-1"),"gb2312");
                System.out.println(temp);
                // break;
            }
            reader.close();
        }
    }

    @Test
    public void httpDownload() throws Exception{

        HttpClient.httpDownload(
                "http://s2.ph.126.net/nN-QtfziLzU0CqQV6YvQNA==/267181362483110.js",
                "S:\\Download\\Downloads\\267181362483110.js");

    }

}
