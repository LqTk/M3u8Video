package download;

import utils.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static utils.Constant.FILESEPARATOR;

public class DownLoadTsOther {
    String urls="";
    private int name0;
    private boolean isOk = false;
    private String dir;
    private Set<File> finishedFiles = new ConcurrentSkipListSet<>(Comparator.comparingInt(o -> Integer.parseInt(o.getName().replace(".ts", ""))));
    private String fileName;
    private String mediaType;
    private static DownStatue statue;
    private static DownLoadTsOther downLoadOther;
    private int nameLength;
    private String nameHead;
    //已经下载的文件大小
    private BigDecimal downloadBytes = new BigDecimal(0);
    private boolean error = false;

    public static DownLoadTsOther getInstance(DownStatue loadStatue){
        if (downLoadOther==null) {
            synchronized (DownLoadTsOther.class) {
                if (downLoadOther==null){
                    downLoadOther = new DownLoadTsOther();
                    statue = loadStatue;
                }
            }
        }
        return downLoadOther;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void startDown(String url0){
        try {
            finishedFiles.clear();
            urls = "";
            isOk = false;
            int retryCount = 10;
            nameLength = 0;
            error = false;
            String[] urlSplits = url0.split("/");
            urls = url0.substring(0,url0.indexOf(urlSplits[urlSplits.length-1]));
            String[] split = urlSplits[urlSplits.length - 1].split("\\.");

            nameLength = split[0].length()-split[0].indexOf("00");
            nameHead = split[0].substring(0,split[0].indexOf("00"));
            name0 = Integer.valueOf(split[0].substring(split[0].indexOf("00"),split[0].length()));
            new Thread(() -> {
                int count = 0;
                HttpURLConnection httpURLConnection = null;
                FileOutputStream outputStream = null;
                FileOutputStream outputStream1 = null;
                byte[] bytes = new byte[1024];
                //重试次数判断
                while (!isOk || count < retryCount) {
                    try {
                        String urlEnd = String.valueOf(name0);
                        while (urlEnd.length()<nameLength){
                            urlEnd = "0"+urlEnd;
                        }
                        //模拟http请求获取ts片段文件
                        URL url = new URL(urls+nameHead+urlEnd+".ts");
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setConnectTimeout(10000);
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setReadTimeout(60000);
                        httpURLConnection.setDoInput(true);
                        if (httpURLConnection.getResponseCode()==200) {
                            count = 0;
                            File file2 = new File(dir + FILESEPARATOR + urlEnd+".ts");
                            if (file2.exists())
                                file2.delete();
                            InputStream inputStream = httpURLConnection.getInputStream();
                            try {
                                outputStream = new FileOutputStream(file2);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            int len;
                            //将未解密的ts片段写入文件
                            while ((len = inputStream.read(bytes)) != -1) {
                                outputStream.write(bytes, 0, len);
                                synchronized (this) {
                                    downloadBytes = downloadBytes.add(new BigDecimal(len));
                                } }

                            outputStream.flush();
                            inputStream.close();
                            outputStream.close();
                            finishedFiles.add(file2);
                            System.out.println(fileName+"\t已完成" + finishedFiles.size() + "个");
                            statue.downLoadCount("    已完成" + finishedFiles.size() + "个");
                            name0++;
                        }else {
                            count++;
                            if (count>=retryCount){
                                isOk = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        count++;
                        if (count>=retryCount){
                            isOk = true;
                            error = true;
                            break;
                        }
                    } finally {
                        try {
                            if (outputStream1 != null)
                                outputStream1.close();
                            if (outputStream != null)
                                outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                    }
                }
                if (error) {
                    //自定义异常
                    deleteFiles();
                    statue.downLoadError();
                    throw new M3u8Exception("连接超时！");
                }
//                System.out.println(urls + "下载完毕！\t已完成" + finishedCount + "个，还剩" + (tsSet.size() - finishedCount) + "个！");
            }).start();
            new Thread(new Runnable() {
                int consume = 0;
                @Override
                public void run() {
                    while (!isOk) {
                        try {
                            consume++;
                            BigDecimal bigDecimal = new BigDecimal(downloadBytes.toString());
                            Thread.sleep(1000);
                            String strR = "\t已用时" + secondToString(consume) + "\t下载速度：" + StringUtils.convertToDownloadSpeed(new BigDecimal(downloadBytes.toString()).subtract(bigDecimal), 3) + "/s";
                            System.out.println(fileName + strR);
                            statue.downLoadProcess("    "+strR);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (isOk && !error) {
                        if (finishedFiles.size()==0){
                            statue.downLoadError();
                            throw new M3u8Exception(fileName+"下载失败！！！！！！！！");
                        }
                        System.out.println("下载完成，正在合并文件！共" + finishedFiles.size() + "个！");
                        //开始合并视频
                        mergeTs();
                        //删除多余的ts片段
                        deleteFiles();
                        System.out.println(fileName + " 视频合并完成，欢迎使用!");
                        downLoadOther = null;
                        statue.downLoadThisOk();
                    }
                }
            }).start();
        }catch (Exception e){
            statue.downLoadError();
        }
    }


    /**
     * 合并下载好的ts片段
     */
    private void mergeTs() {
        try {
            File file = new File(dir + FILESEPARATOR + fileName + mediaType);
            if (file.exists())
                file.delete();
            else file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] b = new byte[4096];
            for (File f : finishedFiles) {
                FileInputStream fileInputStream = new FileInputStream(f);
                int len;
                while ((len = fileInputStream.read(b)) != -1) {
                    fileOutputStream.write(b, 0, len);
                }
                fileInputStream.close();
                fileOutputStream.flush();
            }
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除下载好的片段
     */
    private void deleteFiles() {
        File file = new File(dir);
        for (File f : file.listFiles()) {
            if (!f.getName().endsWith(mediaType))
                f.delete();
        }
    }

    private String secondToString(int count){
        int hour=count/3600;
        int minite=(count-hour*3600)/60;
        int second=count-hour*3600-minite*60;
        String time;
        if (hour!=0){
            time = hour+"时"+minite+"分"+second+"秒！";
        }else if (minite!=0){
            time = minite+"分"+second+"秒！";
        }else {
            time = second+"秒！";
        }
        return time;
    }

}
