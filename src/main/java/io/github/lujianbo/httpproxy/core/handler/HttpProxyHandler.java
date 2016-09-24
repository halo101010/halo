package io.github.lujianbo.httpproxy.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

public class HttpProxyHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject object) throws Exception {
        /*
         * 正常的request
         * */
        if (object instanceof HttpRequest){
            HttpRequest request=(HttpRequest)object;
            /*
             * 处理 Https代理
             * */
            if (request.getMethod().equals(HttpMethod.CONNECT)){
                ctx.pipeline().remove(HttpProxyHandler.this);//移除
                ctx.pipeline().addLast(new HttpsProxyConnectHandler());
                return;
            }
        }
        /*
         * 非https
         * */
        ctx.pipeline().addLast(new HttpProxyConnectHandler());

    }
}
