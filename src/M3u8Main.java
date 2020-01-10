import download.DownLoadTsOther;
import download.DownStatue;
import download.M3u8DownloadFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static utils.Constant.FILESEPARATOR;

public class M3u8Main {

    private static final String M3U8URL = "https://ykm3u8.ylybz.cn/data/iqiyi/6d9b571f4828b0d6941c22c046749a49.m3u8";

    private static int count=15;

    private static DownStatue downStatue = new DownStatue() {
        @Override
        public void downLoadThisOk() {
            count++;
            if (count>urls.length){
                System.out.println("下载完成");
                return;
            }
            startDownloadOther(urls[count-1],"闪电侠 第"+count+"集");
        }

        @Override
        public void downLoadProcess(String process) {

        }

        @Override
        public void downLoadCount(String count) {

        }

        @Override
        public void downLoadError() {

        }
    };

    private static void startDownloadOther(String url, String name) {
        DownLoadTsOther loadTsOther = DownLoadTsOther.getInstance(downStatue);
        loadTsOther.setDir("C:\\Users\\admin\\Desktop\\闪电侠第五季");
        loadTsOther.setFileName(name);
        loadTsOther.setMediaType(".mp4");
        loadTsOther.startDown(url);
    }

    //第四季
    private static String[] urls2 = {
            "https://yiyi.55zuiday.com/20171011/PC8LYxFB/1000kb/hls/CpKgiq4989009.ts",
            "https://cdn.kuyunbo.club/20171019/EwbvtvlM/hls/Jx2ol5672000.ts",
            "https://cdn.kuyunbo.club/20171026/kxq3BqbZ/hls/zLdCz9de4830000.ts",
            "https://cdn.kuyunbo.club/20171101/pjZtlefm/hls/PQOpYlNc7552000.ts",
            "https://cdn.kuyunbo.club/20171109/yRstmcoa/hls/G7xX2823000.ts",
            "https://yiyi.55zuiday.com/20171115/iG0TwBPX/800kb/hls/YVPePFax1259007.ts",
            "https://yiyi.55zuiday.com/20171122/oZgbJgk1/800kb/hls/34fRA4311009.ts",
            "https://yiyi.55zuiday.com/20171129/qp5DyL8H/800kb/hls/e5kfYX5076009.ts",
            "https://yiyi.55zuiday.com/20171206/HOITRnEw/800kb/hls/chmwA3vr6174009.ts",
            "https://leshi.cdn-zuyida.com/20180118/dwznFPlw/800kb/hls/ij5766016007.ts",
            "https://cn2.zuidadianying.com/20180125/0NYLuHoa/800kb/hls/1a7S1261007.ts",
            "https://yushou.qitu-zuida.com/20180201/6ZrjfLrR/800kb/hls/KLB55d1328007.ts",
            "https://yushou.qitu-zuida.com/20180208/9HWkQZbQ/800kb/hls/qEclq2909007.ts",
            "https://yushou.qitu-zuida.com/20180301/gPJ9hwAJ/800kb/hls/wAqXMJpW3851007.ts",
            "https://cn2.zuidadianying.com/20180308/e41R3YU5/800kb/hls/g40r8132007.ts",
            "https://yushou.qitu-zuida.com/20180315/SZCTlCo3/800kb/hls/JHFz1613007.ts",
            "https://yiyi.55zuiday.com/20180412/nvr9Biym/800kb/hls/8nIoxO4960007.ts",
            "https://yiyi.55zuiday.com/20180412/nvr9Biym/800kb/hls/8nIoxO4960000.ts",
            "https://yiyi.55zuiday.com/20180419/7cF7UPHo/800kb/hls/Psq9xTe5945008.ts",
            "https://yiyi.55zuiday.com/20180426/CbICjZAo/800kb/hls/klvzJ4GF7503007.ts",
            "https://cn2.zuidadianying.com/20180503/s5xbls5F/800kb/hls/Vuad5940007.ts",
            "https://yushou.qitu-zuida.com/20180509/5163_ed368627/800k/hls/6f71aaf4950007.ts",
            "https://cn2.zuidadianying.com/20180517/lAPdo0po/800kb/hls/QdEk7815007.ts",
            "https://yiyi.55zuiday.com/20180524/voEtoYWT/800kb/hls/vzx5C3605007.ts",
    };

    //http://www.tt27.tv/dianshiju/oumei/shandianxiadiliuji/ 闪电侠
    //第五季 第六集
    private static String[] urls = {
            "https://cdn.letv-cdn.com/2018/12/13/wvewgyglz06gkWQm/out000.ts",
            "https://cdn.letv-cdn.com/2018/12/13/q97Zq2kXrSa7EmRm/out000.ts",
            "https://cdn.letv-cdn.com/2018/12/13/VrJa6ec7zsqSzakP/out000.ts",
            "https://cdn.letv-cdn.com/2018/12/13/0nFeJFwUPgaLdaqT/out000.ts",
            "http://feifei.feifeizuida.com/20181114/701_f7dbb63c/800k/hls/ec88c71c2bd000.ts",
            "https://cdn.letv-cdn.com/2018/12/13/rgosPBaR0D6BP7ZX/out004.ts",
            "https://www3.yuboyun.com/hls/2018/11/28/OOJWTM0f/out000.ts",
            "https://www3.yuboyun.com/hls/2018/12/05/NLsl9YZK/out000.ts",
            "https://cdn.letv-cdn.com/2018/12/11/NzMHKZGnud9LDnaG/out000.ts",
            "https://cdn.letv-cdn.com/2019/01/17/71bu9mPQbVabN0YU/out000.ts",
            "https://cdn.letv-cdn.com/2019/01/24/2scYpHJlIwlHTHMX/out000.ts",
            "https://cdn.letv-cdn.com/2019/01/31/baWQOHqwaK1cRTCH/out000.ts",
            "https://cdn.letv-cdn.com/2019/02/07/MVpdpsfgcKTcemhl/out000.ts",
            "https://cdn.letv-cdn.com/2019/02/14/DqQsN1MP2qZKA2zR/out000.ts",
            "https://pptv.com-h-pptv.com/20190307/15707_bb6587fa/1000k/hls/2a6594b51a2000007.ts",
            "https://www4.yuboyun.com/hls/2019/03/13/I8tv8eR4/out003.ts",
            "https://cdn.letv-cdn.com/2019/03/21/8d5ks2ZOHST7cs49/out003.ts",
            "https://156zy.suboyouku.com/2019/04/17/iTocTqf4fpN3XiuK/out001.ts",
            "https://cdn.letv-cdn.com/2019/04/25/ViktdfOP28AR0ktn/out003.ts",
            "https://156zy.suboyouku.com/2019/05/01/KmRPfBlHIrTbsQ4c/out001.ts",
            "http://feifei.feifeizuida.com/20190508/10266_7a168e7e/800k/hls/a755449724f000000.ts",
            "http://bili.meijuzuida.com/20190515/14457_58bcb13e/800k/hls/a9a6cad1302000001.ts",
    };
    //https://lmbsy.qq.com/uwMROfz2r5zAoaQXGdGnC2dfKb6Dd5n5TJOTU0jlQFcO2-vB/n00110watse.p202.1.mp4?sdtfrom=v1010&guid=58b6ccf4b48843f948d594566223b123&vkey=3869CEFCD57428DE1213C8677CB97A1B9E9179740DB67D6D418AC1F49F01D2ECDE154A2A4969913B0B1807D663056D0E26C89453C648308B9C9A594A2B7D51F4854B1CF84C9736DC453937460859438950F371FE1D575E1CE15FA21EF414A4A378E8371E2772661AD91FB270534F28E0778D3DEFF73784EFCF3EB74DDBF362ED
    //https://lmbsy.qq.com/uwMROfz2r5zAoaQXGdGnC2dfKb6Dd5n5TJOTU0jlQFcO2-vB/n00110watse.p202.2.mp4?sdtfrom=v1010&guid=58b6ccf4b48843f948d594566223b123&vkey=B96D00211CC1AA10C295907E79CA64B42C07BF92B8765776781369AC3EBB5A1FD6EE6EB5A020F052A1977C351B1F6DB54A1811ED9D0FAD7680499FD4B2139948CEF1ADBE8BD8B536B8B703635F14827FE8C9BCE18711E55CFC174108A2A448849ED42A00CCC8E5E14BBC70D48D16C0DB2E40F48E9836E55A9625A43C37817D06
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
        m3u8Download.start();
        mergeTs();*/
        DownLoadTsOther loadTsOther = DownLoadTsOther.getInstance(downStatue);
        loadTsOther.setDir("C:\\Users\\admin\\Desktop\\闪电侠第五季");
        loadTsOther.setFileName("闪电侠 第"+count+"集");
        loadTsOther.setMediaType(".mp4");
        loadTsOther.startDown(urls[count-1]);
//        loadTsOther.startDown("https://youku.cdn1-okzy.com/20191204/10051_1d8383ab/1000k/hls/e2ec8b050ee000000.ts");
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
