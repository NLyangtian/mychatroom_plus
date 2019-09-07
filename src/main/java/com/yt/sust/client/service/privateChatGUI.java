package com.yt.sust.client.service;

import com.yt.sust.client.vo.MessageVo;
import com.yt.sust.utils.CommUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * 私聊
 */
public class privateChatGUI {
    private JPanel privateChat;
    private JTextField sendToServer;
    private JTextArea readfromServer;

    private String friendName;
    private String myName;
    private ConnectToServer connectToServer;
    private PrintStream out;
    JFrame frame = new JFrame("与"+friendName+"私聊中...");
    public privateChatGUI (String friendName,
                          String  myName,
                          ConnectToServer connectToServer) throws UnsupportedEncodingException {
        this.friendName=friendName;
        this.myName=myName;
        this.connectToServer =connectToServer;
        this.out=new PrintStream(connectToServer.getOut(),true,
                "UTF-8");

        frame.setContentPane(privateChat);

        //设置窗口关闭的操作，将其设置为隐藏
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400,400);
        frame.setVisible(true);


        //捕捉输入框的键盘输入
        sendToServer.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e){

                StringBuilder sb=new StringBuilder();
                sb.append(sendToServer.getText());

                //1.当捕捉到按下Enter
                if(e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
                    //2.将当前信息发送到服务端
                    String  msg=sb.toString();
                    MessageVo messageVo=new MessageVo();
                    messageVo.setType("2");
                    messageVo.setContent(myName+"-"+msg);
                    messageVo.setTo(friendName);
                    privateChatGUI.this.out.println(CommUtils.object2Json(messageVo));
                    //3.将自己发送的信息展示到当前私聊界面
                    readfromServer(myName+"说："+msg);
                    sendToServer.setText("");
                }
            }
        });
    }

    public void readfromServer(String msg){
        readfromServer.append(msg+"\n");
    }

    public JFrame getFrame() {
        return frame;
    }
}
