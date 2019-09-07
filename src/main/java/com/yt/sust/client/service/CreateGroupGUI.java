package com.yt.sust.client.service;

import com.yt.sust.client.vo.MessageVo;
import com.yt.sust.utils.CommUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CreateGroupGUI extends Container {
    private JPanel friendlPanel;
    private JTextField groupNameText;
    private JButton confirm;

    private  String  myName;
    private Set<String >  friends;
    private ConnectToServer connectToServer;
    private FriendsList friendsList;

    public CreateGroupGUI(String myName,
                          Set<String > friends,
                          ConnectToServer connectToServer,
                          FriendsList friendsList) {
        this.myName=myName;
        this.friends=friends;
        this.connectToServer=connectToServer;
        this.friendsList=friendsList;
        JFrame frame=new JFrame("创建群组");
        frame.setContentPane(friendlPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //将在线好友以checkBOX展示到界面中

        //纵向展示
        friendlPanel.setLayout(new BoxLayout(friendlPanel,BoxLayout.Y_AXIS));
        JCheckBox[] checkBoxes=new JCheckBox[friends.size()];
        Iterator<String> iterator=friends.iterator();
        int i=0;
        while(iterator.hasNext()){
            String lableName=iterator.next();
            checkBoxes[i]=new JCheckBox(lableName);
            friendlPanel.add(checkBoxes[i]);
            i++;
        }
        friendlPanel.revalidate();
        //点击提交按钮提交信息到服务端
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //1.判断哪些好友选中加入群聊
                Set<String > selectedFriends=new HashSet<>();
                JComponent[] comps= (JComponent[]) friendlPanel.getComponents();
                //遍历组件，判断哪些好友被选中
                for(Component comp:comps){
                    JCheckBox checkBox =(JCheckBox)comp ;
                    if(checkBox.isSelected()){
                        String lableName=checkBox.getText();
                        selectedFriends.add(lableName);
                    }
                }
                selectedFriends.add(myName);
                //2.获取群名输入框的输入的群名称
                String groupName=groupNameText.getText();
                //3.将群名+选中好友信息发送到服务端，
                //type：3
                //content：groupName
                //to:[user1,user2,user3...]
                MessageVo messageVo=new MessageVo();
                messageVo.setType("3");
                messageVo.setContent(groupName);
                messageVo.setTo(CommUtils.object2Json(selectedFriends));

                try {
                    PrintStream out=new PrintStream(connectToServer.getOut(),
                            true,"UTF-8");
                    out.println(CommUtils.object2Json(messageVo));
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                //4.将当前创建群的界面隐藏，刷新好友列表界面的群列表
                frame.setVisible(true);
                //addGroupInfo
                //loadGroup
                friendsList.addGroup(groupName,selectedFriends);
                friendsList.loadGroupLIst();
            }
        });
    }


}
