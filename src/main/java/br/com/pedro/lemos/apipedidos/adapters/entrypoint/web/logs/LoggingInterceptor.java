package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.logs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String correlationId = request.getHeader("correlationId");
        MDC.put("correlationId", correlationId);
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());

        logger.info("Iniciando requisição - {} {} | Correlation ID: {}",
                request.getMethod(), request.getRequestURI(), correlationId);

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
