package com.example.database.fanyumeta.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * 中控服务消息处理器
 */
@Slf4j
public class HardwareHandler extends SimpleChannelInboundHandler<Object> {

    private Promise<String> promise;

    public HardwareHandler(Promise<String> promise) {
        this.promise = promise;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf message = (ByteBuf) msg;
            String str = message.toString(CharsetUtil.UTF_8);
            log.info("【接收到中控】的消息：{}", str);
            ctx.channel().close();
            this.promise.setSuccess(str);
        }
    }

    public String getMessage() throws ExecutionException, InterruptedException {
        return this.promise.get();
    }
}
