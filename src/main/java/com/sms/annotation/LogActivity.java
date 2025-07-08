package com.sms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sms.entity.ActivityLog.ActivityType;
import com.sms.entity.ActivityLog.EntityType;
import com.sms.entity.ActivityLog.LogSeverity;

/**
 * Annotation untuk menandai method yang perlu di-log aktivitasnya
 * 
 * @author pinaa
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogActivity {

    /**
     * Deskripsi aktivitas yang akan dicatat
     */
    String description() default "";

    /**
     * Tipe aktivitas
     */
    ActivityType activityType() default ActivityType.VIEW;

    /**
     * Tipe entity yang terlibat
     */
    EntityType entityType() default EntityType.SYSTEM;

    /**
     * Tingkat keparahan log
     */
    LogSeverity severity() default LogSeverity.LOW;

    /**
     * Apakah harus log meskipun method gagal
     */
    boolean logOnError() default true;

    /**
     * Apakah log harus asynchronous
     */
    boolean async() default false;
}