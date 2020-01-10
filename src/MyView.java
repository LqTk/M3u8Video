import download.DownLoadTsOther;
import download.DownStatue;
import download.M3u8DownloadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static utils.Constant.FILESEPARATOR;

public class MyView{
    private static JFrame frame;
    private JTextField fileName;
    private JTextField saveRoot;
    private JButton btnFileRoot;
    private JTextField textUrl;
    private JButton btnDown;
    private JPanel homePanel;
    private JProgressBar proDown;
    private JComboBox mediaType;
    private JPanel pannelDown;
    private JPanel pannelChange;
    private JTextField outName;
    private JComboBox outType;
    private JTextField outPath;
    private JButton btnOutPath;
    private JTextField inFile;
    private JButton btnInPath;
    private JProgressBar transPro;
    private JButton btnTrans;
    private JPanel panelDownOut;
    private JProgressBar proLoadOther;
    private JTextField otherOutPath;
    private JButton btnChooseOther;
    private JTextField otherUrl;
    private JButton btnDownOther;
    private JPanel pannelOther;
    private JPanel pannelTs;
    private JTextField tsOutName;
    private JComboBox tsOutType;
    private JTextField textTsUrl;
    private JTextField tsOutPath;
    private JButton btnTsPathOut;
    private JButton btnDownTs;
    private JLabel tsFinshed;
    private JLabel tsTime;
    private JTextField textOtherName;
    private JFileChooser fileChooser = new JFileChooser();
    private static int processValue;
    private String mediaTypeString = ".mp4";
    private String mediaOutTypeString = ".mp4";
    private String tsOutTypeString = ".mp4";
    private DownStatue downStatue = new DownStatue() {
        @Override
        public void downLoadThisOk() {
            btnDown.setText("开始下载");
            btnDown.setEnabled(true);
            if (!pannelDown.isVisible()){
                frame.setTitle("视频转换");
            }
            JOptionPane.showMessageDialog(frame,"下载完成","提示",JOptionPane.INFORMATION_MESSAGE);
            processValue = Integer.valueOf(0);
            setProcessValue();
        }

        @Override
        public void downLoadProcess(String process) {
            processValue = Integer.valueOf(process.split("\\.")[0]);
            setProcessValue();
        }

        @Override
        public void downLoadCount(String count) {
        }

        @Override
        public void downLoadError() {
            btnDown.setText("开始下载");
            btnDown.setEnabled(true);
            if (!pannelDown.isVisible()){
                frame.setTitle("视频转换");
            }
            JOptionPane.showMessageDialog(frame,"下载失败","提示",JOptionPane.INFORMATION_MESSAGE);
        }
    };

    private DownStatue downTsStatue = new DownStatue() {
        @Override
        public void downLoadThisOk() {
            btnDownTs.setText("开始下载");
            btnDownTs.setEnabled(true);
            if (pannelTs.isVisible()){
                frame.setTitle("ts下载");
            }
            JOptionPane.showMessageDialog(frame,"下载完成","提示",JOptionPane.INFORMATION_MESSAGE);
            tsTime.setText("");
            tsFinshed.setText("");
        }

        @Override
        public void downLoadProcess(String process) {
            tsTime.setText(process);
        }

        @Override
        public void downLoadCount(String count) {
            tsFinshed.setText(count);
        }

        @Override
        public void downLoadError() {
            btnDownTs.setText("开始下载");
            btnDownTs.setEnabled(true);
            if (pannelTs.isVisible()){
                frame.setTitle("ts下载");
            }
            JOptionPane.showMessageDialog(frame,"下载失败","提示",JOptionPane.INFORMATION_MESSAGE);
            tsTime.setText("");
            tsFinshed.setText("");
        }
    };
    private M3u8DownloadFactory.M3u8Download m3u8Download;
    public static MenuItemListener menuItemListener = new MenuItemListener();

    private MenuItemClick menuItemClick = new MenuItemClick() {
        @Override
        public void clickItem(String str) {
            if (str.equals("视频转换")){
                pannelChange.setVisible(true);
                pannelDown.setVisible(false);
                pannelOther.setVisible(false);
                pannelTs.setVisible(false);
                if (!btnDown.isEnabled() || !btnDownOther.isEnabled()) {
                    frame.setTitle("视频转换(正在下载视频)");
                }else {
                    frame.setTitle("视频转换");
                }
            }else if (str.equals("m3u8视频下载")){
                pannelDown.setVisible(true);
                pannelChange.setVisible(false);
                pannelOther.setVisible(false);
                pannelTs.setVisible(false);
                if (!btnTrans.isEnabled()){
                    frame.setTitle("m3u8下载器(正在转换视频)");
                }else {
                    frame.setTitle("m3u8下载器");
                }
            }else if (str.equals("其它下载")){
                pannelOther.setVisible(true);
                pannelDown.setVisible(false);
                pannelChange.setVisible(false);
                pannelTs.setVisible(false);
                if (!btnTrans.isEnabled()){
                    frame.setTitle("其它下载(正在转换视频)");
                }else {
                    frame.setTitle("其它下载");
                }
            }else if (str.equals("ts下载")){
                pannelTs.setVisible(true);
                pannelOther.setVisible(false);
                pannelDown.setVisible(false);
                pannelChange.setVisible(false);
                if (!btnTrans.isEnabled()){
                    frame.setTitle("ts下载(正在转换视频)");
                }else {
                    frame.setTitle("ts下载");
                }
            }
        }
    };

    public static void main(String[] args) {
        frame = new JFrame("ts下载");
        frame.setSize(400,300);
        frame.setContentPane(new MyView().homePanel);
        initMenuBar(frame);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    public MyView() {
        String[] mediaTypeStr = {".mp4",".3gp",".rmvb",".avi",".wmv",".mpeg",".mov",".flv",".rm"};
        menuItemListener.setClick(menuItemClick);
        pannelChange.setVisible(false);
        pannelOther.setVisible(false);
        pannelDown.setVisible(false);

        for (String str:mediaTypeStr){
            mediaType.addItem(str);
            outType.addItem(str);
            tsOutType.addItem(str);
        }

        tsOutType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // 只处理选中的状态
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    System.out.println("选中: " + tsOutType.getSelectedIndex() + " = " + tsOutType.getSelectedItem());
                    tsOutTypeString = tsOutType.getSelectedItem().toString();
                }
            }
        });
        outType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // 只处理选中的状态
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    System.out.println("选中: " + outType.getSelectedIndex() + " = " + outType.getSelectedItem());
                    mediaOutTypeString = outType.getSelectedItem().toString();
                }
            }
        });
        mediaType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // 只处理选中的状态
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    System.out.println("选中: " + mediaType.getSelectedIndex() + " = " + mediaType.getSelectedItem());
                    mediaTypeString = mediaType.getSelectedItem().toString();
                }
            }
        });
        mediaType.setSelectedIndex(0);
        proDown.setStringPainted(true);
        transPro.setStringPainted(true);
        proLoadOther.setStringPainted(true);
        //开始下载
        btnDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("down开始下载");
                String url = textUrl.getText().toString();
                String name = fileName.getText().toString();
                String filepath = saveRoot.getText().toString();

                if (url == null || url.equals("")) {
                    JOptionPane.showMessageDialog(textUrl.getParent(), "下载地址不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (name == null || name.equals("")) {
                    JOptionPane.showMessageDialog(fileName.getParent(), "名称不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (filepath == null || filepath.equals("")) {
                    JOptionPane.showMessageDialog(saveRoot.getParent(), "请选择保存路径", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                } else {
                    if (!url.endsWith("m3u8")) {
                        JOptionPane.showMessageDialog(textUrl.getParent(), "请输入正确的下载地址", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    } else {
                        btnDown.setText("正在下载..");
                        btnDown.setEnabled(false);
                        startDownload(url, name, filepath, mediaTypeString);
                    }
                }
            }
        });
        //开始下载
        btnDownOther.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("down开始下载");
                String url = otherUrl.getText().toString();
                String filepath = otherOutPath.getText().toString();
                if (url == null || url.equals("")) {
                    JOptionPane.showMessageDialog(otherUrl.getParent(), "下载地址不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (filepath == null || filepath.equals("")) {
                    JOptionPane.showMessageDialog(otherOutPath.getParent(), "请选择保存路径", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        btnDownOther.setText("正在下载..");
                        btnDownOther.setEnabled(false);
                        downloadNet(url,filepath);
                    }
                }).start();
            }
        });
        btnDownTs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = tsOutName.getText().toString();
                String path = tsOutPath.getText().toString();
                String url = textTsUrl.getText().toString();
                if (url == null || url.equals("")) {
                    JOptionPane.showMessageDialog(otherUrl.getParent(), "下载地址不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (path == null || path.equals("")) {
                    JOptionPane.showMessageDialog(otherOutPath.getParent(), "请选择保存路径", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (name == null || name.equals("")) {
                    JOptionPane.showMessageDialog(otherOutPath.getParent(), "请输入名称", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        btnDownTs.setText("正在下载..");
                        btnDownTs.setEnabled(false);
                        downloadTs(url,path,name);
                    }
                }).start();
            }
        });
        //选择路径
        btnFileRoot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("选择存储路径");
                fileChooser.setApproveButtonText("确定");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fileChooser.showOpenDialog(frame.getContentPane())==JFileChooser.OPEN_DIALOG){
                    saveRoot.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        //选择路径
        btnTsPathOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("选择存储路径");
                fileChooser.setApproveButtonText("确定");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fileChooser.showOpenDialog(frame.getContentPane())==JFileChooser.OPEN_DIALOG){
                    tsOutPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        //选择路径
        btnChooseOther.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("选择存储路径");
                fileChooser.setApproveButtonText("确定");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fileChooser.showOpenDialog(frame.getContentPane())==JFileChooser.OPEN_DIALOG){
                    otherOutPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        //选择路径
        btnOutPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("选择输出路径");
                fileChooser.setApproveButtonText("确定");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fileChooser.showOpenDialog(frame.getContentPane())==JFileChooser.OPEN_DIALOG){
                    outPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        //选择路径
        btnInPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("选择文件");
                fileChooser.setApproveButtonText("确定");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (fileChooser.showOpenDialog(frame.getContentPane())==JFileChooser.OPEN_DIALOG){
                    inFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    String[] split = fileChooser.getSelectedFile().getAbsolutePath().split("\\\\");
                    String name = split[split.length-1].split("\\.")[0];
                    outName.setText(name);
                }
            }
        });
        btnTrans.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = outName.getText().toString();
                String outPathStr = outPath.getText().toString();
                String inPath = inFile.getText().toString();
                if (outPathStr==null || outPathStr.equals("")){
                    JOptionPane.showMessageDialog(fileName.getParent(),"请选择输出路径","提示",JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (inPath==null || inPath.equals("")){
                    JOptionPane.showMessageDialog(fileName.getParent(),"请选择需要转换的视频","提示",JOptionPane.WARNING_MESSAGE);
                    return;
                }else if (name==null || name.equals("")){
                    String[] split = inPath.split("\\\\");
                    name = split[split.length-1].split("\\.")[0];
                    outName.setText(name);
                }
                String finalName = name;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        transMedia(frame, finalName,outPathStr,inPath,mediaOutTypeString);
                    }
                }).start();
                btnTrans.setEnabled(false);
                btnTrans.setText("正在转换...");
            }
        });
    }

    private void downloadTs(String url, String path, String name) {
        DownLoadTsOther loadTsOther = DownLoadTsOther.getInstance(downTsStatue);
        loadTsOther.setDir(path);
        loadTsOther.setFileName(name);
        loadTsOther.setMediaType(tsOutTypeString);
        loadTsOther.startDown(url);
    }

    private void startDownload(String url,String name,String filePath,String mediaType){

        m3u8Download =  M3u8DownloadFactory.getInstance(url,downStatue);
        //设置生成目录
        m3u8Download.setDir(filePath);
        //设置视频名称
        m3u8Download.setFileName(name);
        //设置线程数
        m3u8Download.setThreadCount(100);
        //设置重试次数
        m3u8Download.setRetryCount(100);
        //设置连接超时时间（单位：毫秒）
        m3u8Download.setMediaType(mediaType);
        m3u8Download.setTimeoutMillisecond(10000L);
        m3u8Download.start();

    }
    private void setProcessValue(){
        proDown.setValue(processValue);
    }

    //菜单
    private static void initMenuBar(JFrame mFrame){
        final JMenuBar menuBar = new JMenuBar();

        JMenu functionMenu = new JMenu("功能");

        JMenuItem functionMenuItem4 = new JMenuItem("ts下载");
        JMenuItem functionMenuItem1 = new JMenuItem("m3u8视频下载");
        JMenuItem functionMenuItem2 = new JMenuItem("其它下载");
        JMenuItem functionMenuItem3 = new JMenuItem("视频转换");

        functionMenuItem4.addActionListener(menuItemListener);
        functionMenuItem1.addActionListener(menuItemListener);
        functionMenuItem2.addActionListener(menuItemListener);
        functionMenuItem3.addActionListener(menuItemListener);
        functionMenu.add(functionMenuItem4);
        functionMenu.add(functionMenuItem1);
        functionMenu.add(functionMenuItem2);
        functionMenu.add(functionMenuItem3);

        menuBar.add(functionMenu);
//        menuBar.add(mediaTrans);

        mFrame.setJMenuBar(menuBar);
    }

    static class MenuItemListener implements ActionListener{

        MenuItemClick click;

        public void setClick(MenuItemClick menuItemClick){
            this.click = menuItemClick;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("JMenu Item clicked:" + e.getActionCommand());
            String menuItemStr = e.getActionCommand();
            click.clickItem(menuItemStr);
        }
    }

    interface MenuItemClick{
        void clickItem(String str);
    }


    private void transMedia(Frame frame,String outName,String outPath,String inPath,String outType) {
        try {
            File readFile = new File(inPath);
            if (!readFile.exists()){
                JOptionPane.showMessageDialog(frame,"视频不存在","提示",JOptionPane.WARNING_MESSAGE);
                return;
            }

            File file = new File(outPath+File.separator+outName+outType);
            if (file.exists())
                file.delete();
            else file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] b = new byte[4096];
            FileInputStream fileInputStream = new FileInputStream(readFile);
            long fileLength = readFile.length();
            int len;
            long readLen = 0;
            long timeMillis = System.currentTimeMillis();
            while ((len = fileInputStream.read(b)) != -1) {
                fileOutputStream.write(b, 0, len);
                readLen = readLen + len;
                int pro = (int) (readLen*100/fileLength);
                if (System.currentTimeMillis()-timeMillis>1000){
                    timeMillis = System.currentTimeMillis();
                    if (pro!=transPro.getValue()) {
                        setTransPro(pro);
                    }
                }
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            setTransPro(100);
            btnTrans.setText("开始转换");
            btnTrans.setEnabled(true);
            JOptionPane.showMessageDialog(frame,"转换完成","提示",JOptionPane.WARNING_MESSAGE);
            if (!pannelChange.isVisible()){
                frame.setTitle("m3u8下载器");
            }
            setTransPro(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTransPro(int val){
        transPro.setValue(val);
    }

    //https://d11.baidupcs.com/file/54b83c76d0d9e0da6e688223159d66b4?bkt=en-c58a217c5b5bf7b20dda11c55435cf8c6d9acdb6fb9441e0d164f6124195eb8faa4e79b241695efb&xcode=4ce5e6d5dbfda5001f1c92a14f6614acd13614a7c9a5d3e74548cdbd7e1b97ad7dbd0371821445f9b31065d27a7a4c3d316128a2cdfcce4d&fid=1691762375-250528-56904358877775&time=1578291838&sign=FDTAXGERLQlBHSKf-DCb740ccc5511e5e8fedcff06b081203-2CaGjyGnT9u%2BTKhK3sxYYOXHE1s%3D&to=d11&size=49030114&sta_dx=49030114&sta_cs=47030&sta_ft=zip&sta_ct=5&sta_mt=5&fm2=MH%2CYangquan%2CAnywhere%2C%2Csichuan%2Ccnc&ctime=1568682120&mtime=1568682199&resv0=-1&resv1=0&resv2=rlim&resv3=5&resv4=49030114&vuk=2085848807&iv=0&htype=&randtype=&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=en-3419f1d1f3dfa9765c9f02f348a1dd0dea045feaaf67d6a90720959561e9099c22cc59107b397c39&sl=76480590&expires=8h&rt=sh&r=607968464&vbdid=342718709&fin=xlxpjb_34789.zip&fn=xlxpjb_34789.zip&rtype=1&dp-logid=129879138136908391&dp-callid=0.1&hps=1&tsl=80&csl=80&fsl=-1&csign=NISt3kUfvEs4U0UhNp7HaDXG9dk%3D&so=0&ut=6&uter=4&serv=0&uc=532253427&ti=b788fa7610674275bfa72ee381bab35d6875ad8b7f538634&reqlabel=250528_f_df9d62231f8653986ef68b09c6989fa3_-1_cec57775da399eb85adea959800669c4&by=themis
    //http://fastsoft.onlinedown.net/down/ha_innosetup5500_skygz.rar
    //https://www.apponic.com/phome/download/95409-2-1578286573-20613479658197d82d1f35ccd299eefa/
    private void downloadNet(String urlDown, String path){
        // 下载网络文件
        long bytesum = 0;
        int byteread = 0;
        FileOutputStream fs = null;
        InputStream inStream = null;
        try {
            String[] urlSplites = urlDown.split("/");
            String name = urlSplites[urlSplites.length-1];
            URL url = new URL(urlDown);
            URLConnection conn = url.openConnection();
            long fileLong = conn.getHeaderFieldLong("Content-Length",0);
            String disposition = conn.getHeaderField("Content-Disposition");
            if (disposition!=null && disposition.contains("ame")) {
                String[] disSplit = disposition.split(";");
                for (String disItem : disSplit) {
                    if (disItem.contains("ame")) {
                        if (disItem.endsWith("\"")) {
                            name = disItem.substring(disItem.lastIndexOf("ame") + 5, disItem.length() - 1);
                        }else {
                            name = disItem.substring(disItem.lastIndexOf("ame") + 4, disItem.length());
                        }
                        break;
                    }
                }
            }
            if (name==null || name.equals("") || !name.contains(".")){
                JOptionPane.showMessageDialog(frame,"下载错误，文件名不正确","提示",JOptionPane.WARNING_MESSAGE);
                btnDownOther.setText("开始下载");
                btnDownOther.setEnabled(true);
                return;
            }
            System.out.println("总大小："+fileLong+",name="+name);
            path = path+File.separator+name;
            inStream = conn.getInputStream();
            fs = new FileOutputStream(path);

            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                proLoadOther.setValue((int) (bytesum*100/fileLong));
                fs.write(buffer, 0, byteread);
            }
            fs.close();
            inStream.close();
            btnDownOther.setText("开始下载");
            btnDownOther.setEnabled(true);
            JOptionPane.showMessageDialog(frame,"下载完成","提示",JOptionPane.WARNING_MESSAGE);
            proLoadOther.setValue(0);
        } catch (Exception e) {
            e.printStackTrace();
            btnDownOther.setText("开始下载");
            btnDownOther.setEnabled(true);
            JOptionPane.showMessageDialog(frame,e.toString(),"下载出错",JOptionPane.WARNING_MESSAGE);
            proLoadOther.setValue(0);
        }finally {
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
