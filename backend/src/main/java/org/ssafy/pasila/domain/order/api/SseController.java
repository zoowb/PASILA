package org.ssafy.pasila.domain.order.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.ssafy.pasila.domain.apihandler.ErrorCode;
import org.ssafy.pasila.domain.apihandler.RestApiException;
import org.ssafy.pasila.domain.order.dto.OrderDto;
import org.ssafy.pasila.domain.order.event.StockChangeEvent;
import org.ssafy.pasila.domain.order.service.SseEmitterService;
import retrofit2.http.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/real-time")
@Tag(name = "Real-time check stock", description = "Stock API")
public class SseController {

    private final SseEmitterService sseEmitterService;

    /*
    response.setHeader("X-Accel-Buffering", "no"); // NGINX PROXY 에서의 필요설정
    response.setCharacterEncoding("UTF-8");
     */

    @Operation(summary = "Subscribe live", description = "라이브 정보를 구독")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SseEmitter.class)))
    })
    @GetMapping("/subscribe/{liveId}")
    public SseEmitter getStockUpdates(@PathVariable String liveId, @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId){
        return sseEmitterService.subscribe(liveId, lastEventId);
    }

}
