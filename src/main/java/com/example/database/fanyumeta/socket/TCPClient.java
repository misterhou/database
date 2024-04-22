package com.example.database.fanyumeta.socket;

import com.example.database.fanyumeta.socket.exception.SocketException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * TCP 客户端
 */
@Slf4j
public class TCPClient {

    /**
     * 接收数据超时消息
     */
    public static final String REQUEST_TIMEOUT_MESSAGE = "-200";

    private Channel channel;

    private NioEventLoopGroup worker = new NioEventLoopGroup();

    private ChannelFuture channelFuture;
    public TCPClient(String host, int port, ChannelHandler... handlers) throws SocketException {
        try {
            channelFuture = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(handlers);
                        }
                    }).connect(host, port).sync();
            this.channel = channelFuture.channel();
            this.channel.closeFuture().addListener((ChannelFutureListener) future -> {
                worker.shutdownGracefully();
                log.info("tcp 连接已断开");
            });
            log.info("tcp 连接已建立");
        } catch (InterruptedException e) {
            throw new SocketException("TCP 连接建立异常",e);
        }
    }

    /**
     * 发送消息
     * @param message 消息内容
     * @param timeout 超时时间（秒）
     * @param timeoutPromise 超时回调
     */
    public void sendMessage(Object message, Long timeout, Promise<String> timeoutPromise) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes(message.toString().getBytes(CharsetUtil.UTF_8));
        this.channel.writeAndFlush(byteBuf);
        this.worker.submit(() -> {
            try {
                Thread.sleep(timeout * 1000);
            } catch (InterruptedException e) {
                log.error("等待 TCP 响应数据出错", e);
            }
            if (!timeoutPromise.isDone()) {
                log.info("等待 TCP 响应数据超时：{}", timeoutPromise);
                timeoutPromise.setSuccess(REQUEST_TIMEOUT_MESSAGE);
            }
        });
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (null != this.channel) {
            this.channel.close();
        }
    }
}
