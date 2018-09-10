package com.pg.db.exception;

import com.pg.db.util.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zah on 2018/4/26.
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 所有异常报错
     *
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    public ModelMap allExceptionHandler(HttpServletRequest request,
                                        Exception exception) throws Exception {
         exception.printStackTrace();
        if (exception instanceof AppException) {
            LOGGER.error(exception.getMessage());
            return HttpResult.getResultMap(((AppException) exception).getError());
        } else {
            LOGGER.error(exception.getMessage());
            return HttpResult.getResultMap(ERROR.ERR_SYS);
        }
    }

}
