package expamle;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Album;
import model.Photo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
    private static String folderName = "da3da4";
    private static String saveDLPath = "S:\\Download\\Downloads\\";
    private static String photoServerPath = "http://img1.ph.126.net/";

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
        File folder = new File(saveDLPath + folderName);
        if(!folder.exists())
            folder.mkdirs();

        String resp1 = HttpClient.doGet(myUrl);
        //获取到相册列表Json http://s1.ph.126.net/wiJ4StZBQbV9YbtqSdPQrA==/25288783902360.js
        String albumUrl = resp1.substring(resp1.indexOf("albumUrl ")+12, resp1.indexOf("};")-3);

        //下载相册列表js文件
        String saveFileName = albumUrl.substring(albumUrl.lastIndexOf("/"), albumUrl.length());
        try {
            HttpClient.httpDownload(albumUrl,saveDLPath + folderName + saveFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //读取js文件
        String albumUrlJson = getString(saveFileName);

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

    private String getString(String saveFileName) {
        StringBuffer lineJs = new StringBuffer();
        try{
            File file = new File(saveDLPath + folderName + saveFileName);
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
        return lineJs.toString();
    }

    @RequestMapping("/albumDetail")
    public @ResponseBody String albumDetail(String purl, String albumName, Integer count){
        albumName = albumName.trim();
        //根据相册名创建文件夹
        File folder = new File(saveDLPath + folderName + "/" + albumName);
        if(!folder.exists())
            folder.mkdirs();

        //需要下载js http://s2.ph.126.net/igkrJ8WalqfKT9_6QYv5Dg==/271579405663997.js 包含相册中所有普通图片(murl) 原图(ourl)
        String saveFileName = purl.substring(purl.lastIndexOf("/"), purl.length());
        try {
            HttpClient.httpDownload("http://" + purl,saveDLPath + folderName + "/" + albumName + saveFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("下载相册<"+ folderName +">js成功");

        //读取js文件
        String albumUrlJson = getString("/" + albumName + saveFileName);
        //图片Json
        String albumUrlJson2 = albumUrlJson.substring(albumUrlJson.indexOf("[{"), albumUrlJson.indexOf("}]")+2);

        //Json String 转换成list bean
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);    //忽略引号
        List<Photo> photoList = new ArrayList<>();
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Photo.class);
            String j = albumUrlJson2.replaceAll("'", "\"").replaceAll("<","&gt;")
                    .replaceAll(">","&lt;").replaceAll("\\\\","");
            photoList = (List<Photo>)objectMapper.readValue(j, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("开始下载相册<"+ folderName +">中的图片");
        Long l = 0L;
        for (Photo orgPhoto : photoList) {
            l++;
            if(StringUtils.isEmpty(orgPhoto.getDesc()))
                orgPhoto.setDesc("无标题" + l.toString());
            System.out.println("正在下载相册<"+ folderName +">" + l + " " + orgPhoto.getDesc());
            //需要下载js http://s2.ph.126.net/igkrJ8WalqfKT9_6QYv5Dg==/271579405663997.js 包含相册中所有普通图片(murl) 原图(ourl)
            String photoFileUrl = orgPhoto.getOurl().substring(2, orgPhoto.getOurl().length());
            try {
                HttpClient.httpDownload(photoServerPath + photoFileUrl,
                        saveDLPath + folderName + File.separator + albumName + File.separator + orgPhoto.getDesc());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("相册<"+ folderName +">下载完成");

        return "下载完成";
    }


    /**
     * 下载选中的相册
     * @param values 分隔符,sss,隔开每个数据 每个数据中,ss,隔开相片数 相册名 js地址
     */
    @RequestMapping("/getPicks")
    public @ResponseBody String getPicks(String values){
        if(StringUtils.isEmpty(values))
            return null;

        //大数组
        String[] valueArr = values.split(",sss,");
        for (String value:valueArr) {
            //三组数据数组
            String[] arr2 = value.split(",ss,");

            System.out.println(arr2[0] + '\t' + arr2[1] + '\t' + arr2[2]);

            //循环下载选中的相册

            //页面全选功能

            //单个相册里多线程

            //加密或私密的相册



        }

        return "下载完成";
    }


}
