package com.yt.sust.client.service;

import com.yt.sust.client.vo.MessageVo;
import com.yt.sust.utils.CommUtils;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

public class groupChatGUI {
    private JPanel groupPanel;
    private JTextArea readFromSever;
    private JTextField sendToServer;
    private JPanel friendsPanel;

    private String groupName;
    private Set<String > friends;
    private String myName;
    private ConnectToServer connectToServer;
    JFrame frame = new JFrame();


    public groupChatGUI(String groupName,
                        Set<String > friends,
                        String myName,
                        ConnectToServer connectToServer) {
        this.groupName=groupName;
        this.friends=friends;
        this.connectToServer=connectToServer;
        this.myName=myName;
        frame.setContentPane(groupPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame=new JFrame(groupName);
        //加载群中的好友列表
        friendsPanel.setLayout(new BoxLayout(friendsPanel,BoxLayout.Y_AXIS));
        Iterator<String> iterator=friends.iterator();
        while(iterator.hasNext()){
            String lableName=iterator.next();
            JLabel jLabel=new JLabel(lableName);
            friendsPanel.add(jLabel);
        }
        sendToServer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder sb=new StringBuilder();
                sb.append(sendToServer.getText());

                //捕捉回车按键
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    String strtoServer=sb.toString();
                    //type:4
                    //content:myName-msg
                    //to:groupName
                    MessageVo messageVo=new MessageVo();
                    messageVo.setType("4");
                    messageVo.setContent(myName+"-"+strtoServer);
                    messageVo.setTo(groupName);
                    try {
                        PrintStream out=new PrintStream(connectToServer.getOut(),
                            true,"UTF-8");
                        out.println(CommUtils.object2Json(messageVo));
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }


    public  void readfromServer(String msg){
        readFromSever.append(msg+"\n");
    }

    public JFrame getFrame() {
        return frame;
    }
}
