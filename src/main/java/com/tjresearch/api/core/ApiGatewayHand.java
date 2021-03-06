package com.tjresearch.api.core;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tjresearch.api.common.ApiException;
import com.tjresearch.api.common.UtilJson;
import com.tjresearch.api.core.ApiStore.ApiRunnable;
@Component
public class ApiGatewayHand implements InitializingBean, ApplicationContextAware {
	//private static final Logger logger = LoggerFactory.getLogger(ApiGatewayHand.class);

	private static final String METHOD = "method";
	private static final String PARAMS = "params";

	ApiStore apiStore;
	ParameterNameDiscoverer parameterUtil;

	public ApiGatewayHand() {
		parameterUtil = new LocalVariableTableParameterNameDiscoverer();
	}
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		apiStore = new ApiStore(context);

	}
	@Override
	public void afterPropertiesSet() throws Exception {
		apiStore.loadApiFromSpringBeans();

	}
	
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getParameter(PARAMS);
		String method = request.getParameter(METHOD);
		Object result;
		ApiRunnable apiRun = null;
		try{
			apiRun = sysParamsValidate(request);
			//logger.info("请求接口={"+method+"}参数="+params);
			Object[] args = buildParams(apiRun,params,request,response);
			result = apiRun.run(args);
		}catch(ApiException e){
			response.setStatus(500);
			//logger.error("调用接口={"+method+"}异常 参数="+params +"",e);
			result = handleError(e);
		}catch(InvocationTargetException e) {
			response.setStatus(500);
			//logger.error("调用接口={"+method+"}异常 参数="+params+"",e.getTargetException());
			result = handleError(e.getTargetException());
		}catch(Exception e){
			response.setStatus(500);
			//logger.error("其他异常",e);
			result = handleError(e);
		}
		returnResult(result,response);
		
	}
	
	private Object handleError(Throwable throwable){
		String code = "";
		String message = "";
		if(throwable instanceof ApiException){
			code = "0001";
			message = throwable.getMessage();
		}else{
			code = "0002";
			message = throwable.getMessage();
		}
		Map<String,Object> result = new HashMap<>();
		result.put("code", code);
		result.put("msg", message);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		throwable.printStackTrace();
		return result;
	}
	//验证系统参数
	private ApiRunnable sysParamsValidate(HttpServletRequest request) throws ApiException{
		String apiName = request.getParameter(METHOD);
		String json = request.getParameter(PARAMS);
		ApiRunnable api;
		if(apiName == null || apiName.trim().equals("")){
			throw new ApiException("method为空");
		}else if(json == null){
			throw new ApiException("params为空");
		}else if((api = apiStore.findApiRunnable(apiName)) == null){
			throw new ApiException("调用失败,指定api不存在，api:"+apiName);
		}
		return api;
		
		
		
	}
	private Object[] buildParams(ApiRunnable run,String paramJson,HttpServletRequest request,HttpServletResponse response) throws ApiException{
		Map<String,Object> map = null;
		try{
			map = UtilJson.toMap(paramJson);
			
		}catch(IllegalArgumentException e){
			throw new ApiException("调用失败，json字符串格式异常");
		}
		if(map==null){
			map = new HashMap<>();
		}
		Method method = run.getTargetMethod();
		List<String> paramNames = Arrays.asList(parameterUtil.getParameterNames(method));
		Class<?>[] paramTypes = method.getParameterTypes();
		for(Map.Entry<String, Object> m: map.entrySet()){
			if(!paramNames.contains(m.getKey())){
				throw new ApiException("调用失败，接口不存在");
			}
		}
		Object[] args = new Object[paramTypes.length];
		for(int i = 0;i<paramTypes.length;i++){
			if(paramTypes[i].isAssignableFrom(HttpServletResponse.class)){
				args[i] = request;
			}else if(map.containsKey(paramNames.get(i))){
				try{
					args[i] = converJsonToBean(map.get(paramNames.get(i)),paramTypes[i]);
					
				}catch(Exception e){
					throw new ApiException("调用失败，指定参数格式错误或值错误");
					
				}
				
			}else{
				args[i] = null;
			}
		}
		return args;
	}
	
	
	
	private void returnResult(Object result, HttpServletResponse response) {
		try {
			//UtilJson.JSON_MAPPER.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true);
			String json = UtilJson.writeValueAsString(result);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html/json：charaset=utf-8");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			if (json != null) {
				response.getWriter().write(json);
			}
		} catch (Exception e) {
			//logger.error("服务中心响应异常", e);
			throw new RuntimeException(e);
		}

	}
	
	private <T> Object converJsonToBean(Object val,Class<T> targetClass){
		Object result = null;
		if(val == null){
			return null;
		}else if(Integer.class.equals(targetClass)){
			result = Integer.parseInt(val.toString());
		}else if(Long.class.equals(targetClass)){
			result = Long.parseLong(val.toString());
		}else if(Date.class.equals(targetClass)){
			if(val.toString().matches("[0-9]+")){
				result = new Date(Long.parseLong(val.toString()));
			}else{
				throw new IllegalArgumentException("日期必须是长整型的时间戳");
			}
		}else if(String.class.equals(targetClass)){
			if(val instanceof String){
				result = val;
			}else{
				throw new IllegalArgumentException();
			}
		}
		return result;
		
		
	}

	

	


}
