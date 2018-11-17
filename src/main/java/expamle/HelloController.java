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

    private static String myUrl = "http://photo.163.com/";
    private static String saveDLPath = "S:\\Download\\Downloads\\";
    private static String photoServerPath = "http://img1.ph.126.net/";
    private static String photoServerPath2 = "http://img5.bimg.126.net/";

    @RequestMapping("/list")
    public @ResponseBody List<Album> list(String url){
        if(!StringUtils.isEmpty(url)) {
            String folderName = url.substring(url.lastIndexOf("/"));
            List<Album> albumList = getAlbums(url, folderName);
            return albumList;
        }
        return null;
    }

    @RequestMapping("/list2")
    public String list2(Model model, String url){
        if(!StringUtils.isEmpty(url)) {
            String folderName = url.substring(url.lastIndexOf("/"));
            List<Album> albumList = getAlbums(url, folderName);
            //创建文件夹
            File folder = new File(saveDLPath + folderName);
            if(!folder.exists())
                folder.mkdirs();
            model.addAttribute("list", albumList);
            model.addAttribute("folderName", folderName);
        }
        return "album";
    }

    private List<Album> getAlbums(String myUrl, String folderName) {
        String saveFileName = getAlbumList(myUrl);
        //读取js文件
        String albumUrlJson = getString(saveFileName, folderName);

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

    private String getAlbumList(String myUrl) {
        String resp1 = HttpClient.doGet(myUrl);
        //获取到相册列表Json http://s1.ph.126.net/wiJ4StZBQbV9YbtqSdPQrA==/25288783902360.js
        String albumUrl = resp1.substring(resp1.indexOf("albumUrl ")+12, resp1.indexOf("};")-3);

        //下载相册列表js文件
        String saveFileName = albumUrl.substring(albumUrl.lastIndexOf("/"), albumUrl.length());
        try {
            String folderName = myUrl.substring(myUrl.lastIndexOf("/"));
            HttpClient.httpDownload(albumUrl,saveDLPath + folderName + saveFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveFileName;
    }

    private String getString(String saveFileName, String folder) {
        StringBuffer lineJs = new StringBuffer();
        try{
            File file = new File(saveDLPath + folder + saveFileName);
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

//    Integer reOpen = 0;
    @RequestMapping("/albumDetail")
    public @ResponseBody String albumDetail(String purl, String albumName, Integer count, String id , String folder){
        albumName = albumName.trim().replaceAll("\\.", "x");
        //根据相册名创建文件夹
        File folderFile = new File(saveDLPath + folder + "/" + albumName);
        if(!folderFile.exists())
            folderFile.mkdirs();

        //需要下载js http://s2.ph.126.net/igkrJ8WalqfKT9_6QYv5Dg==/271579405663997.js 包含相册中所有普通图片(murl) 原图(ourl)
        String saveFileName = purl.substring(purl.lastIndexOf("/"), purl.length());
        try {
            HttpClient.httpDownload("http://" + purl,saveDLPath + folder + "/" + albumName + saveFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("下载相册<"+ folder +">js成功");

        //处理js有问题 需要重新打开一次这个页面
//        if(reOpen<2 && saveFileName.length() != 15) {
//            reOpen++;
//            //http://photo.163.com/da3da4/#m=1&aid=50546110&p=1
//            HttpClient.doGet(myUrl + "#m=1&aid=" + id + "&amp;p=1");    //打开一次相册页面
//            getAlbumList(myUrl);        //重新打开所有相册重新下载js
//            return albumDetail(purl, albumName, count, id);    //重新调用本方法
//        }
//        reOpen = 0;

        List<Photo> photoList = new ArrayList<>();
        try {
            //读取js文件
            String albumUrlJson = getString("/" + albumName + saveFileName, folder);
            //图片Json
            String albumUrlJson2 = albumUrlJson.substring(albumUrlJson.indexOf("[{"), albumUrlJson.indexOf("}]")+2);

            //Json String 转换成list bean
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);    //忽略引号

            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Photo.class);
            String j = albumUrlJson2.replaceAll("'", "\"").replaceAll("<","&gt;")
                    .replaceAll(">","&lt;").replaceAll("\\\\","");
            photoList = (List<Photo>)objectMapper.readValue(j, javaType);
        } catch (IOException e) {
            e.printStackTrace();
            return "解析相册中图片js文件错误";
        }

        //多线程下载 总数除以线程数得出每个线程需要下载photoList中几到几的图片 如9张图片 3个线程 9/3 线程一:1-3 线程二:4-6 线程三: 7-9
        final String albumNameFinal = albumName;
        final List<Photo> photoListFinal = new ArrayList<>();
        photoListFinal.addAll(photoList);

        Integer threadCount = 5;
        if(photoList.size()<=10){        //少于等于10张用一个线程 直接全部复制到一个list 并且一次执行
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("开始下载相册<" + albumNameFinal + ">中的图片");
                    download(albumNameFinal, photoListFinal, folder);
                    System.out.println("相册<" + albumNameFinal + ">下载完成");
                }
            }).start();
        }else{                          //超过10张循环线程
            Integer per = photoListFinal.size()/threadCount;    //除以线程数得到每个list放多少条记录
            List<Photo> threadList1 = new ArrayList<>();
            List<Photo> threadList2 = new ArrayList<>();
            List<Photo> threadList3 = new ArrayList<>();
            List<Photo> threadList4 = new ArrayList<>();
            List<Photo> threadList5 = new ArrayList<>();
            List<Photo> threadList6 = new ArrayList<>();
            final List<List<Photo>> threadListAll = new ArrayList<>();
            threadListAll.add(threadList1);
            threadListAll.add(threadList2);
            threadListAll.add(threadList3);
            threadListAll.add(threadList4);
            threadListAll.add(threadList5);
            threadListAll.add(threadList6);
            //循环所有list每个放除以3的平均数 如果有余数放在list4中
            for (int l=0; l<threadListAll.size(); l++) {
                for (int o=per*l; o<per*(l+1); o++) {     //写入每个list
                    if(o==photoListFinal.size())
                        break;
                    threadListAll.get(l).add(photoListFinal.get(o));
                }
            }
            //循环线程数0开始到3 总共4个 对应4个list 内部类不能循环 因为获取不了for的变量
            if(threadListAll.get(0)!=null) {
            new Thread(new Runnable() {     //线程一
                @Override
                public void run() {
                        System.out.println("开始下载相册<" + albumNameFinal + ">中的图片 线程1" + Thread.currentThread());
                        download(albumNameFinal, threadListAll.get(0), folder);
                        System.out.println("相册下载完成<" + albumNameFinal + ">下载完成 线程1" + Thread.currentThread());
                    }
                }).start();
            }
            if(threadListAll.get(1)!=null) {
                new Thread(new Runnable() {     //线程二
                    @Override
                    public void run() {
                        System.out.println("开始下载相册<" + albumNameFinal + ">中的图片 线程2" + Thread.currentThread());
                        download(albumNameFinal, threadListAll.get(1), folder);
                        System.out.println("相册下载完成<" + albumNameFinal + ">下载完成 线程2" + Thread.currentThread());
                    }
                }).start();
            }
            if(threadListAll.get(2)!=null) {
                new Thread(new Runnable() {     //线程三
                    @Override
                    public void run() {
                        System.out.println("开始下载相册<" + albumNameFinal + ">中的图片 线程3" + Thread.currentThread());
                        download(albumNameFinal, threadListAll.get(2), folder);
                        System.out.println("相册下载完成<" + albumNameFinal + ">下载完成 线程3" + Thread.currentThread());
                    }
                }).start();
            }
            if(threadListAll.get(3)!=null) {
                new Thread(new Runnable() {     //线程四
                    @Override
                    public void run() {
                        System.out.println("开始下载相册<" + albumNameFinal + ">中的图片 线程4" + Thread.currentThread());
                        download(albumNameFinal, threadListAll.get(3), folder);
                        System.out.println("相册下载完成<" + albumNameFinal + ">下载完成 线程4" + Thread.currentThread());
                    }
                }).start();
            }
            if(threadListAll.get(4)!=null) {
                new Thread(new Runnable() {     //线程五
                    @Override
                    public void run() {
                        System.out.println("开始下载相册<" + albumNameFinal + ">中的图片 线程5" + Thread.currentThread());
                        download(albumNameFinal, threadListAll.get(4), folder);
                        System.out.println("相册下载完成<" + albumNameFinal + ">下载完成 线程5" + Thread.currentThread());
                    }
                }).start();
            }
            if(threadListAll.get(5)!=null) {
                new Thread(new Runnable() {     //线程六
                    @Override
                    public void run() {
                        System.out.println("开始下载相册<" + albumNameFinal + ">中的图片 线程" + Thread.currentThread());
                        download(albumNameFinal, threadListAll.get(5), folder);
                        System.out.println("相册下载完成<" + albumNameFinal + ">下载完成 线程" + Thread.currentThread());
                    }
                }).start();
            }
        }
        return "开始异步下载";
    }

    private void download(String albumName, List<Photo> photoList, String folder) {
        Long l = 0L;
        for (Photo orgPhoto : photoList) {
            l++;
            String ext =  orgPhoto.getOurl().substring(orgPhoto.getOurl().lastIndexOf("."), orgPhoto.getOurl().length());   //后缀
            if(StringUtils.isEmpty(orgPhoto.getDesc())) {
                orgPhoto.setDesc(orgPhoto.getId() + ext.trim());
            }
            System.out.println("正在下载相册<"+ albumName +">" + l + " " + orgPhoto.getDesc());
            //需要下载js http://s2.ph.126.net/igkrJ8WalqfKT9_6QYv5Dg==/271579405663997.js 包含相册中所有普通图片(murl) 原图(ourl)
            String photoFileUrl = orgPhoto.getOurl().substring(2, orgPhoto.getOurl().length());
            try {
                String saveFileName = orgPhoto.getDesc().trim()
                        .replaceAll("[*]","x").replaceAll("[?]","x")
                        .replaceAll(":","x").replaceAll("\\|","x")
                        .replaceAll("<","x").replaceAll(">","x")
                        .replaceAll("'","x").replaceAll("\"","x")
                        .replaceAll("\\\\","x").replaceAll("/","x") + ext.trim();
                String saveFilePath = saveDLPath + folder + File.separator + albumName + File.separator;
                //检查文件名是否已存在 如果已存在后面加东西
//                File sFile = new File(saveFilePath + saveFileName);
//                if(sFile.exists()){
//                    saveFileName = saveFileName.substring(0, saveFileName.lastIndexOf(".")) + "x" + saveFileName.substring(saveFileName.lastIndexOf("."));
//                }

                HttpClient.httpDownload(photoServerPath + photoFileUrl, saveFilePath + saveFileName);

                //检查文件是否下载成功 有可能存在另一个服务器上 如果文件是1.84 KB (1,892 字节)的读取失败图片 重新换地址下载
                File checkFile = new File(saveFilePath + saveFileName);
                if(checkFile.length()<=1892){
                    HttpClient.httpDownload(photoServerPath2 + photoFileUrl, saveFilePath + saveFileName);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

            //判断是否已下载 检查文件夹和文件数

            //加密或私密的相册



        }

        return "下载完成";
    }


}
