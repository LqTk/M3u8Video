import download.DownStatue;
import download.M3u8DownloadFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static utils.Constant.FILESEPARATOR;

public class M3u8Main {

    private static final String M3U8URL = "https://ykm3u8.ylybz.cn/data/iqiyi/6d9b571f4828b0d6941c22c046749a49.m3u8";

    private static int count=1;

    private static DownStatue downStatue = new DownStatue() {
        @Override
        public void downLoadThisOk() {
            count++;
            if (count>urls.length){
                System.out.println("下载完成");
                return;
            }
            startDownload(urls[count-1],"庆年余 第"+count+"集");
        }

        @Override
        public void downLoadProcess(String process) {

        }

        @Override
        public void m3u8ToMp4() {

        }

        @Override
        public void downLoadError() {

        }
    };

    private static String[] urls = {
            "https://youku.cdn4-okzy.com/20191216/3400_018830bb/index.m3u8"
    };

    public static void main(String[] args) {

        /*M3u8DownloadFactory.M3u8Download m3u8Download =  M3u8DownloadFactory.getInstance(urls[count-1],downStatue);
        //设置生成目录
        m3u8Download.setDir("F://m3u8JavaTest");
        //设置视频名称
        m3u8Download.setFileName("庆余年 第33集");
        //设置线程数
        m3u8Download.setThreadCount(100);
        //设置重试次数
        m3u8Download.setRetryCount(100);
        //设置连接超时时间（单位：毫秒）
        m3u8Download.setTimeoutMillisecond(10000L);
        m3u8Download.start();*/
        mergeTs();

    }

    private static void mergeTs() {
        try {
            File file = new File("F:\\m3u8JavaTest" + FILESEPARATOR + "test.3gp");
            if (file.exists())
                file.delete();
            else file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] b = new byte[4096];
            File readFile = new File("F:\\m3u8JavaTest\\庆余年 第33集.mp4");
            FileInputStream fileInputStream = new FileInputStream(readFile);
            int len;
            while ((len = fileInputStream.read(b)) != -1) {
                fileOutputStream.write(b, 0, len);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startDownload(String url,String name){

        M3u8DownloadFactory.M3u8Download m3u8Download =  M3u8DownloadFactory.getInstance(url,downStatue);
        //设置生成目录
        m3u8Download.setDir("F://m3u8JavaTest");
        //设置视频名称
        m3u8Download.setFileName(name);
        //设置线程数
        m3u8Download.setThreadCount(100);
        //设置重试次数
        m3u8Download.setRetryCount(100);
        //设置连接超时时间（单位：毫秒）
        m3u8Download.setTimeoutMillisecond(10000L);
        m3u8Download.start();
    }
}
