import download.DownStatue;
import download.M3u8DownloadFactory;

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
    };

    private static String[] urls = {
            "https://ykm3u8.ylybz.cn/data/iqiyi/6d9b571f4828b0d6941c22c046749a49.m3u8",
            "https://qq-qiyi-youku.gms-lighting.com/m3u8/1565.m3u8",
            "https://qq-qiyi-youku.gms-lighting.com/m3u8/1566.m3u8",
            "https://qq-qiyi-youku.gms-lighting.com/m3u8/1567.m3u8",
            "https://txxs.mahua-yongjiu.com/20191127/5459_3350d9ce/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191127/5458_6699dc8a/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191128/5590_c1928f1f/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191128/5589_fa7428c8/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191202/6316_67ca1323/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191202/6315_961c796f/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191202/6317_41a153d4/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191203/6465_deb18b37/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191203/6464_9b5e55b5/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191204/6619_d8394e05/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191204/6620_e7b8197b/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191205/6778_9ae70792/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191205/6779_2a44ff2e/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191209/7041_a52f82ba/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191209/7042_9c5429a7/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191210/15350_d4fe5458/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191210/15354_dfed49db/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191211/7294_be77a065/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191211/7301_e9044898/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191211/7300_f0fcdb7c/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191211/7299_02c2f50a/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191211/7297_c6f36eab/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191211/7298_c97275a4/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191216/15970_4c6e6326/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191216/15969_983ed338/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191216/15986_f4179b0a/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191216/15987_d582b9da/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191216/15993_60cf4513/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191216/15992_018830bb/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191219/8385_78b370b0/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191219/8386_52107412/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191219/8388_caa7a87a/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191219/8387_628492df/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191219/8389_825eeb96/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191219/8390_c2c9df0b/index.m3u8",
            "https://txxs.mahua-yongjiu.com/20191219/8391_6af65bdb/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191219/16271_08d3d5ce/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191219/16272_404f65df/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191219/16273_2e1c9bb5/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191219/16274_43434bc4/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191219/16275_14171b42/index.m3u8",
            "https://yingxiong.qiling-yongjiu.com/20191219/16276_84c70d21/index.m3u8"
    };

    public static void main(String[] args) {

        M3u8DownloadFactory.M3u8Download m3u8Download =  M3u8DownloadFactory.getInstance(urls[count-1],downStatue);
        //设置生成目录
        m3u8Download.setDir("F://m3u8JavaTest");
        //设置视频名称
        m3u8Download.setFileName("庆余年 第1集");
        //设置线程数
        m3u8Download.setThreadCount(100);
        //设置重试次数
        m3u8Download.setRetryCount(100);
        //设置连接超时时间（单位：毫秒）
        m3u8Download.setTimeoutMillisecond(10000L);
        m3u8Download.start();
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
