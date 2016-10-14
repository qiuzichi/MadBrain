package com.unipad.io.mina;

import android.widget.Toast;

import com.unipad.brain.App;
import com.unipad.utils.LogUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class LongTcpClient implements ClientSessionHandler.IDataHandler {

    private static LongTcpClient instance;

    private static final String HOSTNAME = "http://221.5.109.34";

    private static final int PORT = 7003;

    private static final long CONNECT_TIMEOUT = 30 * 1000L; // 30 seconds

    /**
     * 15�뷢��һ�������
     */
    private static final int HEARTBEATRATE = 15;

    private ClientSessionHandler handler;

    private static boolean isInstanceed;

    private  NioSocketConnector connector;

    public IoSession session;

    private ClientSessionHandler.IDataHandler dataHandler;

    private LongTcpClient() {

    }

    public static LongTcpClient instant() {
        if (instance == null || !isInstanceed) {
            synchronized (LongTcpClient.class) {
                if (instance == null) {
                    instance = new LongTcpClient();
                    instance.init();
                    isInstanceed = true;
                }
            }
        }

        return instance;
    }
    private boolean isReconnect = true;
    public boolean init() {
        if(!isInstanceed) {
            connector = new NioSocketConnector();
            // Configure the service.
            connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
            handler = new ClientSessionHandler(this);
            connector.setHandler(handler);
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("GBK"))));
//        connector.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new ObjectSerializationCodecFactory()));
            connector.getFilterChain().addLast("log", new LoggingFilter());
            ClientKeepAliveMessageFactoryImp heartBeatFactory = new ClientKeepAliveMessageFactoryImp();

            KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory,
                    IdleStatus.BOTH_IDLE);

            //�����Ƿ�forward����һ��filter
            heartBeat.setForwardEvent(true);
            connector.getFilterChain().addFirst("reconnection", new IoFilterAdapter() {
                @Override
                public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession ioSession) throws Exception {
                    for (; ; ) {
                        if (!isReconnect){
                            break;
                        }
                        try {
                            Thread.sleep(3000);
                            ConnectFuture future = connector.connect();
                            future.awaitUninterruptibly();// 等待连接创建成功
                            session = future.getSession();// 获取会话
                            if (session.isConnected()) {
                                LogUtil.e("断线重连[");
                                break;
                            }
                        } catch (Exception ex) {
                            LogUtil.e("重连服务器登录失败,3秒再连接一次:" + ex.getMessage());
                        }
                    }
                }
            });
            //��������Ƶ��
            heartBeat.setRequestInterval(HEARTBEATRATE);
            connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30000);  //读写都空闲时间:30秒
            connector.getSessionConfig().setWriteTimeout(5000);
            connector.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, 40000);//读(接收通道)空闲时间:40秒
            connector.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 50000);//写(发送通道)空闲时间:50秒
            connector.getFilterChain().addLast("keeplive", new KeepAliveFilter(new ClientKeepAliveMessageFactoryImp(), IdleStatus.READER_IDLE, KeepAliveRequestTimeoutHandler.DEAF_SPEAKER, 10, 5));
            connector.setDefaultRemoteAddress(new InetSocketAddress(HOSTNAME, PORT));
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    for (; ; ) {
                        if (!isReconnect){
                            break;
                        }
                        try {
                            ConnectFuture future = connector.connect();
                            future.awaitUninterruptibly();// 等待连接创建完成
                            session = future.getSession();
                            if (session.isConnected()) {
                                isInstanceed = true;
                                break;
                            } else {

                            }
                        }catch(Exception e){
                            System.err.println("Failed to connect.");
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

        }
        return isInstanceed;
    }

    public void release(){
        disConnect();
        if (instance != null) {
            isInstanceed = false;
            instance = null;
        }
    }


    public void sendMsg(String data) {
        if (session != null) {
            session.write(data);
        }
    }
    public void disConnect() {
            isReconnect = false;
        if (session != null) {
            session.closeNow();
        }
    }
    public void sendMsg(Request request) {
        if (session == null){
            new Throwable(new RuntimeIoException(" seesion is null"));
        }
        if (request != null) {
            request.sendMsg(session);
        }
    }

    @Override
    public void processPack(IPack pack) {
        if (dataHandler != null) {
            dataHandler.processPack(pack);
        }
    }

    public void setDataHandler(ClientSessionHandler.IDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

}
