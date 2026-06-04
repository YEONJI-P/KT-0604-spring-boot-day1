package com.sesac.aibackend.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message){
        super(message);
    }
//    of 정적 팩토리로 message 를 일정한 format 으로 ...
//    호출부가 깔끔해지고 메시지 형식이 한곳에 모임
    public static NotFoundException of(String resource,Object id){
        return new NotFoundException(resource+" not found: "+id);
    }
}
