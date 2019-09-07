package com.yt.sust.client.service;

import com.yt.sust.client.dao.AccountDao;
import com.yt.sust.entity.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserRegister {
    private JPanel userRegPanel;
    private JTextField userNameText;
    private JTextField passwordText;
    private JTextField briefText;
    private JButton btn;
    private AccountDao accountDao=new AccountDao();

    public static void main(String[] args) {
        JFrame frame = new JFrame("UserRegister");
        frame.setContentPane(new UserRegister().userRegPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public UserRegister(){
       JFrame frame = new JFrame("用户注册");
       frame.setContentPane(userRegPanel);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setLocationRelativeTo(null);
       frame.pack();
       frame.setVisible(true);


       //点击注册按钮将信息持久化到数据库中，成功弹出提示框
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                //弹出提示框
//                JOptionPane.showMessageDialog(frame,"注册成功","提示信息",
//                        JOptionPane.INFORMATION_MESSAGE);
                //1.获取用户输入的注册信息
                String userName=userNameText.getText();
                String password=String.valueOf(passwordText.getText());
                String brief=briefText.getText();

                //将输入信息包装为user类，保存到数据库中
                User user=new User();
                user.setUserName(userName);
                user.setPassWord(password);
                user.setBrief(brief);

                //调用dao对象
                if(accountDao.userReg(user)){
                    JOptionPane.showMessageDialog(frame,"注册成功！",
                            "提示信息",JOptionPane.INFORMATION_MESSAGE);

                    //将当前页面置为不可见
                    frame.setVisible(false);
                }else{
                    //保留当前页面
                    JOptionPane.showMessageDialog(frame,"注册失败！",
                            "错误信息",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
