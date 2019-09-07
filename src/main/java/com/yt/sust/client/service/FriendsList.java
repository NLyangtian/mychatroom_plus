package com.yt.sust.client.service;

import com.yt.sust.client.vo.MessageVo;
import com.yt.sust.utils.CommUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FriendsList {
    private JPanel friendsPanel;
    private JLabel friendsOnline;
    private JScrollPane friendlist;
    private JLabel friendGroup;
    private JButton createButtonGroup;
    private JFrame frame;

    private String userName;


    //存储所有在线好友
    private Set<String> users;

    //存储所有的群名称以及相应的群成员
    private Map<String ,Set<String >> groupList=new ConcurrentHashMap<>();
    private ConnectToServer connectToServer;
    //缓存所有群聊界面
    private Map<String,privateChatGUI> privateChatGUIList=new ConcurrentHashMap<>();

    private Map<String ,groupChatGUI> groupChatGUIList=new ConcurrentHashMap<>();
    //好友列表后台任务，不断监听服务器发来的信息
    //好友上线信息、用户私聊、群聊

    private  class DaemonTask implements Runnable{
        private Scanner in=new Scanner(connectToServer.getIn());
        @Override
        public void run() {
            //收到服务器发送来的信息
            while(true){
                if(in.hasNextLine()){
                String strFromServer=in.nextLine();
                //此时服务器发来的是一个json字符串
                if(strFromServer.startsWith("{")){
                    MessageVo messageVo=(MessageVo) CommUtils.json2Object(strFromServer,MessageVo.class);
                    if(messageVo.getType().equals("2")){
                        //服务器发来的私聊信息
                        String friendName=messageVo.getContent().split("-")[0];
                        String msg=messageVo.getContent().split("-")[1];
                        //判断此私聊是否是第一次创建
                        if(privateChatGUIList.containsKey(friendName)){
                            privateChatGUI privateChatGUI=privateChatGUIList.get(friendName);
                            privateChatGUI.getFrame().setVisible(true);
                            privateChatGUI.readfromServer(friendName+"说："+msg);
                        }else {
                            try {
                                privateChatGUI privateChatGUI=new privateChatGUI(friendName,
                                        userName,connectToServer);
                                privateChatGUIList.put(friendName,privateChatGUI);
                                privateChatGUI.readfromServer(friendName+"说"+msg)   ;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else if(messageVo.getType().equals("4")){
                        //收到服务器发来的群聊信息
                        //type:4
                        //content:sender-msg
                        //to:group-[1,2,3...]
                        String groupName=messageVo.getTo().split("-")[0];
                        String senderName=messageVo.getContent().split("-")[0];
                        String groupMsg=messageVo.getContent().split("-")[1];
                        //若此名称在群聊列表
                        if(groupList.containsKey(groupName)){
                            if(groupChatGUIList.containsKey(groupName)){
                                //群聊界面弹出
                                groupChatGUI groupChatGUI=groupChatGUIList.get(groupName);
                                groupChatGUI.getFrame().setVisible(true);
                                groupChatGUI.readfromServer(senderName+"说"+groupMsg);
                            }else{
                                Set<String > names=groupList.get(groupName);
                                groupChatGUI groupChatGUI=new groupChatGUI(groupName,
                                        names,userName,connectToServer);
                                groupChatGUIList.put(groupName,groupChatGUI);
                                groupChatGUI.readfromServer(senderName+"说"+groupMsg);
                            }
                        }else {
                            //若群成员第一次收到群聊信息
                            //1.将群信息以及群成员保存到当前客户端群聊列表
                            Set<String > friends= (Set<String>) CommUtils.json2Object(messageVo.getTo().split("-")[1],Set.class);
                            groupList.put(groupName,friends);
                            loadGroupLIst();
                            //2.弹出群聊界面
                            groupChatGUI  groupChatGUI=new groupChatGUI(groupName,
                                    friends,userName,connectToServer);
                            groupChatGUIList.put(groupName,groupChatGUI);
                            groupChatGUI.readfromServer(senderName+"说："+groupMsg);
                        }
                    }
                }
                    String newFriendName=strFromServer.split(":")[1];
                    users.add(newFriendName);
                    //弹框提示用户上线
                    JOptionPane.showMessageDialog(frame,
                            newFriendName+"上线了",
                    "上线提醒",JOptionPane.INFORMATION_MESSAGE);

                    //刷新好友列表
                    loadUsers();
                }

            }
        }
    }

    //私聊点击事件
    private class PrivateLabelAction implements MouseListener{
        private String friendName;

        public PrivateLabelAction(String friendName) {
            this.friendName=friendName;

        }

        //鼠标点击事件
        @Override
        public void mouseClicked(MouseEvent e) {
            //判断好友列表私聊界面缓存是否已经有指定标签
            if(privateChatGUIList.containsKey(friendName)){
                privateChatGUI privateChatGUI=privateChatGUIList.get(friendName);
                privateChatGUI.getFrame().setVisible(true);
            }else{
                //第一次点击，创建私聊界面
                privateChatGUI privateChatGUI= null;
                try {
                    privateChatGUI = new privateChatGUI(
                            friendName,
                            userName,
                            connectToServer
                    );
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                privateChatGUIList.put(friendName,privateChatGUI);

            }

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    //群聊点击事件
    private  class GroupLabelActioon implements MouseListener{
        private String groupName;

        public GroupLabelActioon(String groupName) {
            this.groupName = groupName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(groupChatGUIList.containsKey(groupName)){
                groupChatGUI groupChatGUI=groupChatGUIList.get(groupName);
                groupChatGUI.getFrame().setVisible(true);

            }else {
                Set<String > names=groupList.get(groupName);
                groupChatGUI groupChatGUI=new groupChatGUI(groupName,
                        names,userName,connectToServer);
                //保存到自己的缓存中
                groupChatGUIList.put(groupName,groupChatGUI);
            }


        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
    public FriendsList(String userName, Set<String>users,
                       ConnectToServer connectToServer){
        this.connectToServer=connectToServer;
        this.userName=userName;
        this.users=users;
        JFrame frame = new JFrame("FriendsList");
        frame.setContentPane(friendsPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(400,300);
        frame.setVisible(true);

        loadUsers();
        //启动后台线程不断监听服务器发来的消息
        Thread  daemonThread = new Thread(new DaemonTask());
        daemonThread.setDaemon(true);
        daemonThread.start();

        //创建群组
        createButtonGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateGroupGUI(userName,users,connectToServer,FriendsList.this);
            }
        });
    }

    //加载所有在线的用户信息
    public void loadUsers(){
        JLabel[] userLabels=new JLabel[users.size()];
        JPanel  friends= new JPanel();
        friends.setLayout(new BoxLayout(friends,BoxLayout.Y_AXIS));
        //set集合遍历
        Iterator<String> iterator=users.iterator();
        int i=0;
        while(iterator.hasNext()){
            String userName =iterator.next();
            userLabels[i] =new JLabel(userName);
            //添加标签的点击事件
            userLabels[i].addMouseListener(new PrivateLabelAction(userName));
            friends.add(userLabels[i]);
            i++;
        }

        friendlist.setViewportView(friends);
        //设置滚动条垂直滚动
        friendlist.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //刷新列表
        friends.revalidate();
        friendlist.revalidate();
    }

    public  void loadGroupLIst(){
        //存储所有标签JPanel
        JPanel groupNamePanel=new JPanel();
        groupNamePanel.setLayout(new BoxLayout(groupNamePanel,
                BoxLayout.Y_AXIS));

        JLabel[] labels=new JLabel[groupList.size()];
        //Map遍历
        Set<Map.Entry<String ,Set<String>>> entries=groupList.entrySet();
        Iterator<Map.Entry<String ,Set<String>>> iterator=entries.iterator();
        int i=0;
        while(iterator.hasNext()){
            Map.Entry<String ,Set<String>> entry=iterator.next();
            labels[i]=new JLabel(entry.getKey());
            labels[i].addMouseListener(new GroupLabelActioon(entry.getKey()));
            groupNamePanel.add(labels[i]);
            i++;
        }
        friendlist.setViewportView(groupNamePanel);
        friendlist.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        friendlist.revalidate();
    }

    public void addGroup(String groupName,Set<String> friends){
        groupList.put(groupName,friends);
    }
}
