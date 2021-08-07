package me.zp.generator.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zzpp
 * @date 2021/4/1.
 */
public class CustomOptional<T> {

    private final T value;

    public static <T> CustomOptional<T> of(T value) {
        return new CustomOptional<>(value);
    }

    public CustomOptional(T value) {
        this.value = value;
    }

    public CustomOptional<T> orElse(T other) {
        return value != null ? new CustomOptional<>(value) : new CustomOptional<>(other);
    }


    public void isPresent(Running<T> running) {
        if (null != value) {
            running.run(value);
        }
    }

    public void isPresentStr(Running<T> running) {
        if (null != value && StringUtils.isNotBlank((String) value)) {
            running.run(value);
        }
    }


    @FunctionalInterface
    public interface Running<T> {
        /**
         * 执行方法
         *
         * @param value 执行
         */
        void run(T value);
    }

}
