package com.example.androidjavademo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.androidjavademo.databinding.FragmentSecondBinding;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 获取Tracer
                Tracer tracer = OpenTelemetryUtil.getTracer();
                // 创建Span
                Span span = tracer.spanBuilder("Second Fragment Button onClick").startSpan();
                try (Scope scope = span.makeCurrent()) {
                    // 获取traceId
                    System.out.println(span.getSpanContext().getTraceId());
                    // 获取spanId
                    System.out.println(span.getSpanContext().getSpanId());

                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_FirstFragment);
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

}