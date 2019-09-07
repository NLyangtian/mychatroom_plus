package com.yt.sust.client.service;

import com.yt.sust.client.dao.AccountDao;
import com.yt.sust.client.vo.MessageVo;
import com.yt.sust.entity.User;
import com.yt.sust.utils.CommUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Set;

public class userlogin {
    private JPanel userlogin;
    private JPanel userloginPanel;
    private JTextField usernameText;
    private JTextField passwordText;
    private JPanel buttonPanel;
    private JButton registerButton;
    private JButton loginButton;
    private AccountDao accountDao=new AccountDao();



    public userlogin() {

        JFrame frame = new JFrame("用户登录");
        frame.setContentPane(userloginPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        //注册按钮
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //弹出注册页面
                new UserRegister();
            }
        });

        //登录按钮
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //校验用户信息
                String userName=usernameText.getText();
                String password=passwordText.getText();
                User user = accountDao.userLogin(userName,password);
                if(user != null){
                    //校验成功，加载用户列表
                JOptionPane.showMessageDialog(frame,"登录成功！","提示信息",
                        JOptionPane.INFORMATION_MESSAGE);
                frame.setVisible(false);

                //与服务器建立连接，将当前用户的用户名与密码发送到服务端
                    ConnectToServer connectToServer=new ConnectToServer();
                    MessageVo msgToServer=new MessageVo();
                    msgToServer.setType("1");
                    msgToServer.setContent(userName);
                    String  jsonToSrver=CommUtils.object2Json(msgToServer);
                    try {
                        PrintStream out=new PrintStream(connectToServer.getOut(),
                                true,"UTF-8");
                        out.println(jsonToSrver);
                        //读取服务端发回的所有在线用户信息
                        Scanner in=new Scanner(connectToServer.getIn());
                        if(in.hasNextLine()){
                            String msgFromSever =in.nextLine();
                            MessageVo msgFromServer=(MessageVo) CommUtils.json2Object(msgFromSever,
                                    MessageVo.class);
                            Set<String> users=(Set<String>) CommUtils.json2Object(msgFromSever,Set.class);
                            System.out.println("所有在线用户"+users);

                            //加载用户列表界面
                            //将当前用户名、所有在线好友
                            new FriendsList(userName,users,connectToServer);
                        }
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                }else{
                    //校验失败，停留在当前登录页面，提示用户信息错误
                    JOptionPane.showMessageDialog(frame,"登录失败",
                            "错误信息",JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    }

    public static void main(String[] args) {
        userlogin userlogin=new userlogin();
    }
}
