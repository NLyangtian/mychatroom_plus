package com.yt.sust.server;

import com.yt.sust.client.vo.MessageVo;
import com.yt.sust.utils.CommUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadSever {
    private static final String IP;
    private static final int PORT;
    //缓存服务器当前所有在线客户端的用户信息
    private static Map<String ,Socket> clients=new ConcurrentHashMap();

    //缓存当前服务器注册的所有群名称以及群好友
    private  Map<String,Set<String>> group=new ConcurrentHashMap<>();

    static{
        Properties pros=CommUtils.loadProperties("socket.properties");
        IP =pros.getProperty("address");
        PORT=Integer.parseInt(pros.getProperty("port"));
    }
    //处理每个客户端的连接
    private static class ExcecuteClient implements  Runnable{
        private Socket client;
        private Scanner in;
        private PrintStream out;
        //缓存当前服务器注册的所有群名称以及群好友
        private  Map<String,Set<String>> groups=new ConcurrentHashMap<>();

        public ExcecuteClient(Socket client) {
            this.client = client;
            try {
                this.in=new Scanner(client.getInputStream());
                this.out=new PrintStream(client.getOutputStream(),
                        true,"UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            while(true){
                if(in.hasNextLine()){
                    String jsonStrFromClient=in.nextLine();
                    MessageVo msgFromClient=(MessageVo)CommUtils.json2Object(jsonStrFromClient,
                            MessageVo.class);
                    if(msgFromClient.getType().equals("1")){
                        //新用户注册到服务端
                        String userName=msgFromClient.getContent();

                        //将当前在线的所有用户名发回客户端
                        MessageVo msgClient=new MessageVo();
                        msgClient.setType("1");
                        msgClient.setContent(CommUtils.object2Json(clients.keySet()));
                        out.println(CommUtils.object2Json(msgClient));

                        //将新上线的用户信息发回给当前已在线的所有用户
                        sendUserLogin("newLogin:"+userName);

                        //将当前的新用户注册到服务端缓存
                        clients.put(userName,client);
                        System.out.println(userName+"上线了！");
                        System.out.println("当前聊天室共有"+clients.size()+"人");

                    }else if(msgFromClient.getType().equals("2")){
                        //用户私聊
                        String senderName=msgFromClient.getContent().split("-")[0];
                        String msg=msgFromClient.getContent().split("-")[1];
                        String recieverName=msgFromClient.getContent().split("-")[2];
                        Socket clientSocket=clients.get(recieverName);
                        try {
                            PrintStream out=new PrintStream(clientSocket.getOutputStream(),true,"UTF-8");
                            MessageVo msgToClient=new MessageVo();
                            msgToClient.setType("2");
                            msgToClient.setContent(msgFromClient.getContent());
                            System.out.println("收到私聊信息，内容为"+msgFromClient.getContent());
                            out.println(CommUtils.object2Json(msgFromClient));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(msgFromClient.getType().equals("3")){
                        //注册群
                        String groupName=msgFromClient.getContent();
                        //获取群成员
                        Set<String > friends= (Set<String>) CommUtils.json2Object(
                                msgFromClient.getTo(),
                                Set.class);
                        groups.put(groupName,friends);
                        System.out.println("有新的群注册成功，群名称为："+
                        groupName+",一共有"+groups.size()+"个群");

                    }else if(msgFromClient.getType().equals("4")){
                        //群聊信息

                        String groupName=msgFromClient.getTo();
                        Set<String> names=groups.get(groupName);
                        Iterator<String > iterator=names.iterator();
                        while(iterator.hasNext()){
                            String socketName=iterator.next();
                            Socket client=clients.get(socketName);
                            try {
                                PrintStream out=new PrintStream(client.getOutputStream(),
                                        true,"UTF-8");
                                MessageVo messageVo=new MessageVo();
                                messageVo.setType("4");
                                messageVo.setContent(msgFromClient.getContent());

                                //群名-[]
                                messageVo.setTo(groupName+"-"+CommUtils.object2Json(names));
                                out.println(CommUtils.object2Json(messageVo));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    }
                }
            }


        /**
         * 向所有在线用户发送新用户上线信息
         * @param msg
         */
        private static void sendUserLogin(String msg){
            for(Map.Entry<String ,Socket> entry:clients.entrySet()){
                Socket socket=entry.getValue();
                try {
                    PrintStream out=new PrintStream(socket.getOutputStream(),
                            true,"UTF-8");
                    out.println(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket=new ServerSocket(PORT);
        ExecutorService executors= Executors.newFixedThreadPool(30);
        for(int i=0;i<30;i++){
            System.out.println("等待客户端的连接");
            Socket client=serverSocket.accept();
            System.out.println("有新的连接，端口号为："+client.getPort());
            executors.submit(new ExcecuteClient(client));
        }
    }
}
