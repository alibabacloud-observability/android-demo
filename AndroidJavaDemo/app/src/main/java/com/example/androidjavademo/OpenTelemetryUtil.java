package com.example.androidjavademo;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

public class OpenTelemetryUtil {
    public static void init() {
        Resource otelResource = Resource.getDefault().merge(
            Resource.create(
                Attributes.of(
                    // 请将 <your-service-name> 替换为您的应用名
                    ResourceAttributes.SERVICE_NAME, "<your-service-name>",
                    // 请将 <your-host-name> 替换为您的主机名
                    ResourceAttributes.HOST_NAME, "<your-host-name>"
                )
            )
        );

        /* 使用gRPC协议上报链路数据 */
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create())) // 可选，将链路数据打印到日志/命令行，如不需要请注释这一行
                // 请将<gRPC-endpoint> 替换为从前提条件中获取的接入点，<gRPC-token> 替换为鉴权Token
                .addSpanProcessor(BatchSpanProcessor.builder(
                    OtlpGrpcSpanExporter.builder()
                        .setEndpoint("<gRPC-endpoint>") // 例如 http://tracing-analysis-dc-hz.aliyuncs.com:8090
                        .addHeader("Authentication", "<gRPC-token>") // 例如 xxxx@xxxx_xxxx@xxxx
                        .build()).build()
                )
                .setResource(otelResource)
                .build();

        /* 使用HTTP协议上报链路数据
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create())) // 可选，将链路数据打印到日志/命令行，如不需要请注释这一行
                // 请将<HTTP-endpoint> 替换为从前提条件中获取的接入点
                .addSpanProcessor(BatchSpanProcessor.builder(
                        OtlpHttpSpanExporter.builder()
                                .setEndpoint("<HTTP-endpoint>") // 例如 http://tracing-analysis-dc-hz.aliyuncs.com/adapt_xxxx@xxxx_xxxx@xxxx/api/otlp/traces
                                .build()).build()
                )
                .setResource(otelResource)
                .build();
         */

        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();

        // 获取tracer，用来创建Span
        tracer = openTelemetry.getTracer("android-tracer", "1.0.0");
    }

    private static Tracer tracer;

    public static Tracer getTracer() {
        return tracer;
    }
}
