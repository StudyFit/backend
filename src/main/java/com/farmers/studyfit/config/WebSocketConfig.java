package com.farmers.studyfit.config;

import com.farmers.studyfit.domain.chat.handler.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final ChatWebSocketHandler chatWebSocketHandler;
    
    @Value("${spring.websocket.max-session-idle-timeout:1800000}")
    private long maxSessionIdleTimeout;
    
    @Value("${spring.websocket.connection-timeout:300000}")
    private long connectionTimeout;
    
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // 세션 유지 시간 설정
        container.setMaxSessionIdleTimeout(maxSessionIdleTimeout);
        // 연결 타임아웃 설정
        container.setAsyncSendTimeout(connectionTimeout);
        // 최대 텍스트 메시지 크기 설정
        container.setMaxTextMessageBufferSize(8192);
        // 최대 바이너리 메시지 크기 설정
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOriginPatterns(
                        "http://localhost:*",
                        "https://localhost:*",
                        "https://*.cloudtype.app",
                        "https://*.vercel.app",
                        "https://*.netlify.app"
                )
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(org.springframework.http.server.ServerHttpRequest request,
                                                 org.springframework.http.server.ServerHttpResponse response,
                                                 org.springframework.web.socket.WebSocketHandler wsHandler,
                                                 Map<String, Object> attributes) throws Exception {
                        String origin = request.getHeaders().getFirst("Origin");
                        if (origin == null) {
                            return true;
                        }
                        return true;
                    }

                    @Override
                    public void afterHandshake(org.springframework.http.server.ServerHttpRequest request,
                                             org.springframework.http.server.ServerHttpResponse response,
                                             org.springframework.web.socket.WebSocketHandler wsHandler,
                                             Exception exception) {
                    }
                });
    }
}
