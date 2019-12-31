import download.DownStatue;
import download.M3u8DownloadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyView{
    private static JFrame frame;
    private JTextField fileName;
    private JTextField saveRoot;
    private JButton btnFileRoot;
    private JTextField textUrl;
    private JButton btnDown;
    private JPanel homePanel;
    private JProgressBar proDown;
    private JFileChooser fileChooser = new JFileChooser();
    private static int processValue;
    private DownStatue downStatue = new DownStatue() {
        @Override
        public void downLoadThisOk() {
            btnDown.setText("开始下载");
            btnDown.setEnabled(true);
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
        public void m3u8ToMp4() {
        }

        @Override
        public void downLoadError() {
            btnDown.setText("开始下载");
            btnDown.setEnabled(true);
            JOptionPane.showMessageDialog(frame,"下载失败","提示",JOptionPane.INFORMATION_MESSAGE);
        }
    };


    public static void main(String[] args) {
        frame = new JFrame("m3u8下载器");
        frame.setContentPane(new MyView().homePanel);
        frame.setSize(400,200);
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
        proDown.setStringPainted(true);
        //开始下载
        btnDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("down开始下载");
                String url = textUrl.getText().toString();
                String name = fileName.getText().toString();
                String filepath = saveRoot.getText().toString();
                if (url==null || url.equals("")){
                    JOptionPane.showMessageDialog(textUrl.getParent(),"下载地址不能为空","提示",JOptionPane.WARNING_MESSAGE);
                }else if (name==null || name.equals("")){
                    JOptionPane.showMessageDialog(fileName.getParent(),"名称不能为空","提示",JOptionPane.WARNING_MESSAGE);
                }else if (filepath == null || filepath.equals("")) {
                    JOptionPane.showMessageDialog(saveRoot.getParent(),"请选择保持路径","提示",JOptionPane.WARNING_MESSAGE);
                }else {
                    if (!url.endsWith("m3u8")){
                        JOptionPane.showMessageDialog(textUrl.getParent(),"请输入正确的下载地址","提示",JOptionPane.WARNING_MESSAGE);
                    }else {
                        btnDown.setText("正在下载..");
                        btnDown.setEnabled(false);
                        startDownload(url,name);
                    }
                }
            }
        });
        //选择路径
        btnFileRoot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("选择需要保持的路径");
                fileChooser.setApproveButtonText("确定");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fileChooser.showOpenDialog(frame.getContentPane())==JFileChooser.OPEN_DIALOG){
                    saveRoot.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
    }

    private void startDownload(String url,String name){

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
    private void setProcessValue(){
        proDown.setValue(processValue);
    }
}
