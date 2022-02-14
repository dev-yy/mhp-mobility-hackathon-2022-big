package com.mhp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Object, Object> {


    @Override
    public Object handleRequest(Object input, Context context) {
        context.getLogger().log("Received event: " + input);
        return input;
    }
    
}
