package com.example.androidkotlindemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidkotlindemo.databinding.FragmentFirstBinding
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.api.trace.Tracer

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            // 获取Tracer
            val tracer: Tracer = OpenTelemetryUtil.getTracer()!!
            // 创建Span
            val span = tracer.spanBuilder("First Fragment Button onClick").startSpan()
            try {
                span.makeCurrent().use { scope ->
                    // 获取traceId
                    println(span.spanContext.traceId)
                    // 获取spanId
                    println(span.spanContext.spanId)
                    // 设置属性
                    span.setAttribute("key", "value")
                    val eventAttributes =
                        Attributes.of(
                            AttributeKey.stringKey("key"), "value",
                            AttributeKey.longKey("result"), 0L
                        )

                    // 添加事件
                    span.addEvent("onClick", eventAttributes)
                    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                    parentSpan()
                }
            } catch (t: Throwable) {
                // 为Span设置状态
                span.setStatus(StatusCode.ERROR, "Something wrong in onClick")
                throw t
            } finally {
                span.end()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 嵌套Span
    fun parentSpan() {
        // 获取Tracer
        val tracer: Tracer = OpenTelemetryUtil.getTracer()!!
        // 创建Span
        val span = tracer.spanBuilder("Parent Span").startSpan()
        try {
            span.makeCurrent().use { scope ->
                // 获取traceId
                println(span.spanContext.traceId)
                // 获取spanId
                println(span.spanContext.spanId)
                childSpan()
            }
        } finally {
            span.end()
        }
    }

    // 嵌套Span
    fun childSpan() {
        // 获取Tracer
        val tracer: Tracer = OpenTelemetryUtil.getTracer()!!
        // 创建Span
        val span = tracer.spanBuilder("Child Span").startSpan()
        try {
            span.makeCurrent().use { scope ->
                // 获取traceId
                println(span.spanContext.traceId)
                // 获取spanId
                println(span.spanContext.spanId)
            }
        } finally {
            span.end()
        }
    }
}

