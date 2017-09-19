package com.tjresearch.api.core;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//告诉我们的api网关这个方法需要往外暴露出去


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface APIMapping {
	String value();

}
