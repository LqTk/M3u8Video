package download;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import utils.MediaFormat;
import utils.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static utils.Constant.FILESEPARATOR;

/**
 * @author liyaling
 * @email ts_liyaling@qq.com
 * @date 2019/12/14 16:05
 */

public class M3u8DownloadFactory {

    private static M3u8Download m3u8Download;

    private static DownStatue downStatue;
    /**
     *
     * 解决java不支持AES/CBC/PKCS7Padding模式解密
     *
     */
    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    public static class M3u8Download {

        //要下载的m3u8链接
        private String DOWNLOADURL;

        //线程数
        private int threadCount = 1;

        //重试次数
        private int retryCount = 30;

        //链接连接超时时间（单位：毫秒）
        private long timeoutMillisecond = 1000L;

        //合并后的文件存储目录
        private String dir;

        //合并后的视频文件名称
        private String fileName;

        //已完成ts片段个数
        private int finishedCount = 0;

        //解密算法名称
        private String method;

        //密钥
        private String key = "";

        //密钥字节
        private byte[] keyBytes = new byte[16];

        //key是否为字节
        private boolean isByte = false;

        //IV
        private String iv = "";

        //所有ts片段下载链接
        private Set<String> tsSet = new LinkedHashSet<>();

        //解密后的片段
        private Set<File> finishedFiles = new ConcurrentSkipListSet<>(Comparator.comparingInt(o -> Integer.parseInt(o.getName().replace(".xyz", ""))));

        //已经下载的文件大小
        private BigDecimal downloadBytes = new BigDecimal(0);
        private String mediaType = ".mp4";

        /**
         * 开始下载视频
         */
        public void start() {
            checkField();
            String tsUrl = getTsUrl();
            if (StringUtils.isEmpty(tsUrl))
                System.out.println("不需要解密");
            startDownload();
        }


        /**
         * 下载视频
         */
        private void startDownload() {
            //线程池
            final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadCount);
            int i = 0;
            //如果生成目录不存在，则创建
            File file1 = new File(dir);
            if (!file1.exists())
                file1.mkdirs();
            //执行多线程下载
            for (String s : tsSet) {
                i++;
                fixedThreadPool.execute(getThread(s, i));
            }
            fixedThreadPool.shutdown();
            //下载过程监视
            new Thread(() -> {
                int consume = 0;
                //轮询是否下载成功
                while (!fixedThreadPool.isTerminated()) {
                    try {
                        consume++;
                        BigDecimal bigDecimal = new BigDecimal(downloadBytes.toString());
                        Thread.sleep(1000L);
                        System.out.print("已用时" + consume + "秒！\t下载速度：" + StringUtils.convertToDownloadSpeed(new BigDecimal(downloadBytes.toString()).subtract(bigDecimal), 3) + "/s");
                        System.out.print("\t已完成" + finishedCount + "个，还剩" + (tsSet.size() - finishedCount) + "个！");
                        System.out.println(new BigDecimal(finishedCount).divide(new BigDecimal(tsSet.size()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                        downStatue.downLoadProcess(new BigDecimal(finishedCount).divide(new BigDecimal(tsSet.size()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        downStatue.downLoadError();
                    }
                }
                System.out.println("下载完成，正在合并文件！共" + finishedFiles.size() + "个！" + StringUtils.convertToDownloadSpeed(downloadBytes, 3));
                //开始合并视频
                mergeTs();
                //删除多余的ts片段
                deleteFiles();
                downStatue.m3u8ToMp4();
                System.out.println(fileName+" 视频合并完成，欢迎使用!");
                m3u8Download = null;
                downStatue.downLoadThisOk();
            }).start();
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

        /**
         * 开启下载线程
         *
         * @param urls ts片段链接
         * @param i    ts片段序号
         * @return 线程
         */
        private Thread getThread(String urls, int i) {
            return new Thread(() -> {
                int count = 1;
                HttpURLConnection httpURLConnection = null;
                //xy为未解密的ts片段，如果存在，则删除
                File file2 = new File(dir + FILESEPARATOR + i + ".xy");
                if (file2.exists())
                    file2.delete();
                OutputStream outputStream = null;
                InputStream inputStream1 = null;
                FileOutputStream outputStream1 = null;
                byte[] bytes = new byte[1024];
                //重试次数判断
                while (count <= retryCount) {
                    try {
                        //模拟http请求获取ts片段文件
                        URL url = new URL(urls);
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setConnectTimeout((int) timeoutMillisecond);
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setReadTimeout((int) timeoutMillisecond);
                        httpURLConnection.setDoInput(true);
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
                            }
                        }
                        outputStream.flush();
                        inputStream.close();
                        inputStream1 = new FileInputStream(file2);
                        byte[] bytes1 = new byte[inputStream1.available()];
                        inputStream1.read(bytes1);
                        File file = new File(dir + FILESEPARATOR + i + ".xyz");
                        outputStream1 = new FileOutputStream(file);
                        //开始解密ts片段，这里我们把ts后缀改为了xyz，改不改都一样
                        outputStream1.write(decrypt(bytes1, key, iv, method));
                        finishedFiles.add(file);
                        break;
                    } catch (Exception e) {
                        if (e instanceof InvalidKeyException || e instanceof InvalidAlgorithmParameterException) {
                            System.out.println("解密失败！");
                            break;
                        }
//                        System.out.println("第" + count + "获取链接重试！\t" + urls);
                        count++;
//                        e.printStackTrace();
                    } finally {
                        try {
                            if (inputStream1 != null)
                                inputStream1.close();
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
                if (count > retryCount) {
                    //自定义异常
                    downStatue.downLoadError();
                    throw new M3u8Exception("连接超时！");
                }
                finishedCount++;
//                System.out.println(urls + "下载完毕！\t已完成" + finishedCount + "个，还剩" + (tsSet.size() - finishedCount) + "个！");
            });
        }

        /**
         * 获取所有的ts片段下载链接
         *
         * @return 链接是否被加密，null为非加密
         */
        private String getTsUrl() {
            StringBuilder content = getUrlContent(DOWNLOADURL, false);
            //判断是否是m3u8链接
            if (!content.toString().contains("#EXTM3U")) {
                downStatue.downLoadError();
                throw new M3u8Exception(DOWNLOADURL + "不是m3u8链接！");
            }
            String[] split = content.toString().split("\\n");
            String keyUrl = "";
            boolean isKey = false;
            for (String s : split) {
                //如果含有此字段，则说明只有一层m3u8链接
                if (s.contains("#EXT-X-KEY") || s.contains("#EXTINF")) {
                    isKey = true;
                    keyUrl = DOWNLOADURL;
                    break;
                }
                //如果含有此字段，则说明ts片段链接需要从第二个m3u8链接获取
                if (s.contains(".m3u8")) {
                    if (StringUtils.isUrl(s))
                        return s;
                    String relativeUrl = DOWNLOADURL.substring(0, DOWNLOADURL.lastIndexOf("/") + 1);
                    keyUrl = relativeUrl + s;
                    break;
                }
            }
            if (StringUtils.isEmpty(keyUrl)) {
                downStatue.downLoadError();
                throw new M3u8Exception("未发现key链接！");
            }
            //获取密钥
            String key1 = isKey ? getKey(keyUrl, content) : getKey(keyUrl, null);
            if (StringUtils.isNotEmpty(key1))
                key = key1;
            else key = null;
            return key;
        }

        /**
         * 获取ts解密的密钥，并把ts片段加入set集合
         *
         * @param url     密钥链接，如果无密钥的m3u8，则此字段可为空
         * @param content 内容，如果有密钥，则此字段可以为空
         * @return ts是否需要解密，null为不解密
         */
        private String getKey(String url, StringBuilder content) {
            StringBuilder urlContent;
            if (content == null || StringUtils.isEmpty(content.toString()))
                urlContent = getUrlContent(url, false);
            else urlContent = content;
            if (!urlContent.toString().contains("#EXTM3U")) {
                downStatue.downLoadError();
                throw new M3u8Exception(DOWNLOADURL + "不是m3u8链接！");
            }
            String[] split = urlContent.toString().split("\\n");
            for (String s : split) {
                //如果含有此字段，则获取加密算法以及获取密钥的链接
                if (s.contains("EXT-X-KEY")) {
                    String[] split1 = s.split(",");
                    for (String s1 : split1) {
                        if (s1.contains("METHOD")) {
                            method = s1.split("=", 2)[1];
                            continue;
                        }
                        if (s1.contains("URI")) {
                            key = s1.split("=", 2)[1];
                            continue;
                        }
                        if (s1.contains("IV"))
                            iv = s1.split("=", 2)[1];
                    }
                }
            }
            String relativeUrl = url.substring(0, url.lastIndexOf("/") + 1);
            //将ts片段链接加入set集合
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if (s.contains("#EXTINF")) {
                    String s1 = split[++i];
                    tsSet.add(StringUtils.isUrl(s1) ? s1 : relativeUrl + s1);
                }
            }
            if (!StringUtils.isEmpty(key)) {
                key = key.replace("\"", "");
                return getUrlContent(StringUtils.isUrl(key) ? key : relativeUrl + key, true).toString().replaceAll("\\s+", "");
            }
            return null;
        }

        /**
         * 模拟http请求获取内容
         *
         * @param urls  http链接
         * @param isKey 这个url链接是否用于获取key
         * @return 内容
         */
        private StringBuilder getUrlContent(String urls, boolean isKey) {
            int count = 1;
            HttpURLConnection httpURLConnection = null;
            StringBuilder content = new StringBuilder();
            while (count <= retryCount) {
                try {
                    URL url = new URL(urls);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout((int) timeoutMillisecond);
                    httpURLConnection.setReadTimeout((int) timeoutMillisecond);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
                    String line;
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    if (isKey) {
                        byte[] bytes = new byte[128];
                        int len;
                        len = inputStream.read(bytes);
                        isByte = true;
                        if (len == 1 << 4) {
                            keyBytes = Arrays.copyOf(bytes, 16);
                            content.append("isByte");
                        } else
                            content.append(new String(Arrays.copyOf(bytes, len)));
                        return content;
                    }
                    while ((line = bufferedReader.readLine()) != null)
                        content.append(line).append("\n");
                    bufferedReader.close();
                    inputStream.close();
//                    System.out.println(content);
                    break;
                } catch (Exception e) {
//                    System.out.println("第" + count + "获取链接重试！\t" + urls);
                    count++;
//                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
            if (count > retryCount) {
                downStatue.downLoadError();
                throw new M3u8Exception("连接超时！");
            }
            return content;
        }

        /**
         * 解密ts
         *
         * @param sSrc ts文件字节数组
         * @param sKey 密钥
         * @return 解密后的字节数组
         */
        private byte[] decrypt(byte[] sSrc, String sKey, String iv, String method) throws Exception {
            if (StringUtils.isNotEmpty(method) && !method.contains("AES")) {
                downStatue.downLoadError();
                throw new M3u8Exception("未知的算法！");
            }
            // 判断Key是否正确
            if (StringUtils.isEmpty(sKey)) {
                return sSrc;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16 && !isByte) {
                System.out.print("Key长度不是16位");
                return null;
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(isByte ? keyBytes : sKey.getBytes("utf-8"), "AES");
            byte[] ivByte;
            if (iv.startsWith("0x"))
                ivByte = StringUtils.hexStringToByteArray(iv.substring(2));
            else ivByte = iv.getBytes();
            if (ivByte.length != 16)
                ivByte = new byte[16];
            //如果m3u8有IV标签，那么IvParameterSpec构造函数就把IV标签后的内容转成字节数组传进去
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivByte);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return cipher.doFinal(sSrc);
        }

        /**
         * 字段校验
         */
        private void checkField() {
            if ("m3u8".compareTo(MediaFormat.getMediaFormat(DOWNLOADURL)) != 0) {
                downStatue.downLoadError();
                throw new M3u8Exception(DOWNLOADURL + "不是一个完整m3u8链接！");
            }
            if (threadCount <= 0) {
                downStatue.downLoadError();
                throw new M3u8Exception("同时下载线程数只能大于0！");
            }
            if (retryCount < 0) {
                downStatue.downLoadError();
                throw new M3u8Exception("重试次数不能小于0！");
            }
            if (timeoutMillisecond < 0) {
                downStatue.downLoadError();
                throw new M3u8Exception("超时时间不能小于0！");
            }
            if (StringUtils.isEmpty(dir)) {
                downStatue.downLoadError();
                throw new M3u8Exception("视频存储目录不能为空！");
            }
            if (StringUtils.isEmpty(fileName)) {
                downStatue.downLoadError();
                throw new M3u8Exception("视频名称不能为空！");
            }
            finishedCount = 0;
            method = "";
            key = "";
            isByte = false;
            iv = "";
            tsSet.clear();
            finishedFiles.clear();
            downloadBytes = new BigDecimal(0);
        }

        public String getDOWNLOADURL() {
            return DOWNLOADURL;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public void setThreadCount(int threadCount) {
            this.threadCount = threadCount;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }

        public void setMediaType(String type) {
            this.mediaType = type;
        }

        public long getTimeoutMillisecond() {
            return timeoutMillisecond;
        }

        public void setTimeoutMillisecond(long timeoutMillisecond) {
            this.timeoutMillisecond = timeoutMillisecond;
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

        public void setDownUrl(String downUrl){
            this.DOWNLOADURL = downUrl;
        }

        public int getFinishedCount() {
            return finishedCount;
        }

        private M3u8Download(String DOWNLOADURL) {
            this.DOWNLOADURL = DOWNLOADURL;
        }
    }

    /**
     * 获取实例
     *
     * @param downloadUrl 要下载的链接
     * @return 返回m3u8下载实例
     */
    public static M3u8Download getInstance(String downloadUrl, DownStatue downStatue1) {
        if (m3u8Download == null) {
            synchronized (M3u8Download.class) {
                if (m3u8Download == null)
                    m3u8Download = new M3u8Download(downloadUrl);
                    downStatue = downStatue1;
            }
        }
        return m3u8Download;
    }

    public static void destroied() {
        m3u8Download = null;
    }

}
