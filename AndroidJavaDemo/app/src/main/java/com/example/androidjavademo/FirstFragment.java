package com.example.androidjavademo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.androidjavademo.databinding.FragmentFirstBinding;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取Tracer
                Tracer tracer = OpenTelemetryUtil.getTracer();
                // 创建Span
                Span span = tracer.spanBuilder("First Fragment Button onClick").startSpan();
                try (Scope scope = span.makeCurrent()) {
                    // 获取traceId
                    System.out.println(span.getSpanContext().getTraceId());
                    // 获取spanId
                    System.out.println(span.getSpanContext().getSpanId());
                    // 设置属性
                    span.setAttribute("key", "value");

                    Attributes eventAttributes = Attributes.of(
                            AttributeKey.stringKey("key"), "value",
                            AttributeKey.longKey("result"), 0L);

                    // 添加事件
                    span.addEvent("onClick", eventAttributes);

                    parentSpan();

                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                } catch (Throwable t) {
                    span.setStatus(StatusCode.ERROR, "Something wrong in onClick");
                    throw t;
                } finally {
                    span.end();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void parentSpan() {
        // 获取Tracer
        Tracer tracer = OpenTelemetryUtil.getTracer();
        // 创建Span
        Span span = tracer.spanBuilder("Parent Span").startSpan();
        try (Scope scope = span.makeCurrent()) {
            // 获取traceId
            System.out.println(span.getSpanContext().getTraceId());
            // 获取spanId
            System.out.println(span.getSpanContext().getSpanId());
            childSpan();
        } finally {
            span.end();
        }
    }

    public void childSpan() {
        // 获取Tracer
        Tracer tracer = OpenTelemetryUtil.getTracer();
        // 创建Span
        Span span = tracer.spanBuilder("Child Span").startSpan();
        try (Scope scope = span.makeCurrent()) {
            // 获取traceId
            System.out.println(span.getSpanContext().getTraceId());
            // 获取spanId
            System.out.println(span.getSpanContext().getSpanId());
        } finally {
            span.end();
        }
    }

}