package com.farmers.studyfit.config;

import com.farmers.studyfit.domain.chat.handler.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final ChatWebSocketHandler chatWebSocketHandler;
    
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
