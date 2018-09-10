package com.pg.db.util;

import com.pg.db.exception.ERROR;
import org.springframework.ui.ModelMap;


public class HttpResult {
    public static ModelMap getResultMap(ERROR error, String msg) {
        ModelMap result = new ModelMap();
        result.addAttribute("code", error.getValue());
        result.addAttribute("msg", msg);
        return result;
    }

    public static ModelMap getResultMap(ERROR error) {
        ModelMap result = new ModelMap();
        result.addAttribute("code", error.getValue());
        result.addAttribute("msg", error.getErrorMsg());
        return result;
    }

    public static ModelMap getResultOKMap(String msg, Object data) {
        ModelMap result = new ModelMap();
        result.addAttribute("code", ERROR.ERR_NO_ERR.getValue());
        result.addAttribute("msg", msg);
        return result;
    }

    public static ModelMap getResultOKMap() {
        ModelMap result = new ModelMap();
        result.addAttribute("code", ERROR.ERR_NO_ERR.getValue());
        result.addAttribute("msg", ERROR.ERR_NO_ERR.getErrorMsg());
        return result;
    }

    public static ModelMap getResultOKMap(Object data) {
        ModelMap result = new ModelMap();
        result.addAttribute("code", ERROR.ERR_NO_ERR.getValue());
        result.addAttribute("msg", ERROR.ERR_NO_ERR.getErrorMsg());
        result.addAttribute("data", data);
        return result;
    }

}
