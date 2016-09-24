package io.github.lujianbo.httpproxy.core.handler;

import io.github.lujianbo.httpproxy.core.util.ProxyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;

import java.net.URI;

public class HttpProxyConnectHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * 目标 bound
     * */
    private Channel outboundChannel;

    public HttpProxyConnectHandler(){

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        /**
         * 将目标的channel的数据进行转移
         * */
        outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
        outboundChannel.pipeline().addLast(new HttpRequestEncoder());//encode
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject object) throws Exception {

        //如果尚未连接,则发起通向目标端口的连接

        /*
         * 修剪头部
         * */
        if (object instanceof HttpRequest){
            HttpRequest request=(HttpRequest)object;
            URI uri=new URI(request.getUri());
            request.setUri(uri.getPath());
        }
        /*
         * 输出数据
         * */
        outboundChannel.write(object);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (outboundChannel.isActive()) {
            ProxyUtil.closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ProxyUtil.closeOnFlush(outboundChannel);
    }
}
