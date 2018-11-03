package expamle;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Album;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import unit.HttpClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {
    @RequestMapping("/index")
    public String index(){
        return "welcome";
    }

    private static String myUrl = "http://photo.163.com/da3da4/";
    private static String saveDLPath = "S:\\Download\\Downloads\\";

    @RequestMapping("/list")
    public @ResponseBody List<Album> list(){
        List<Album> albumList = getAlbums(myUrl);
        return albumList;
    }

    @RequestMapping("/list2")
    public String list2(Model model){
        List<Album> albumList = getAlbums(myUrl);
        model.addAttribute("list", albumList);
        return "album";
    }

    private List<Album> getAlbums(String myUrl) {
        String resp1 = HttpClient.doGet(myUrl);
        //获取到相册列表Json http://s2.ph.126.net/nN-QtfziLzU0CqQV6YvQNA==/267181362483110.js
        String albumUrl = resp1.substring(resp1.indexOf("albumUrl ")+12, resp1.indexOf("};")-3);

        //下载相册列表js文件
        String albumUrlJson = null;
        String saveFileName = albumUrl.substring(albumUrl.lastIndexOf("/"), albumUrl.length());
        try {
            HttpClient.httpDownload(albumUrl,saveDLPath + saveFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //读取js文件
        StringBuffer lineJs = new StringBuffer();
        try{
            File file = new File(saveDLPath + saveFileName);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "GBK");//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = "";
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    lineJs.append(lineTxt);
                }
                read.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        albumUrlJson = lineJs.toString();

        //查询出相册Json
        String albumUrlJson2 = albumUrlJson.substring(albumUrlJson.indexOf("[{"), albumUrlJson.indexOf("}]")+2);

        //Json String 转换成list bean
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);    //忽略引号
        List<Album> albumList = new ArrayList<>();
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Album.class);
            String j = albumUrlJson2.replaceAll("'", "\"").replaceAll("<","&gt;")
                    .replaceAll(">","&lt;").replaceAll("\\\\","");
            albumList = (List<Album>)objectMapper.readValue(j, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return albumList;
    }
}
