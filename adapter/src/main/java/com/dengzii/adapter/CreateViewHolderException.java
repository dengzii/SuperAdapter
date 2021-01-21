package com.dengzii.adapter;

public class CreateViewHolderException extends RuntimeException {

    CreateViewHolderException(Throwable cause){
        super(cause);
    }

    CreateViewHolderException(String msg){
        super(msg);
    }
}
