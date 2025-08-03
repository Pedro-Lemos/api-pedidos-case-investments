package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.logs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());

        logger.info("Iniciando requisição - {} {} | Request ID: {}",
                request.getMethod(), request.getRequestURI(), requestId);

        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        if (ex != null) {
            logger.error("Requisição finalizada com erro - Status: {} | Duração: {}ms | Erro: {}",
                    response.getStatus(), duration, ex.getMessage());
        } else {
            logger.info("Requisição finalizada com sucesso - Status: {} | Duração: {}ms",
                    response.getStatus(), duration);
        }

        MDC.clear();
    }

}
