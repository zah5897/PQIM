package com.pg.db.exception;

public enum ERROR {
    ERR_SYS(-2, "系統错误"),    ERR_FAILED(-1, "操作失败"), ERR_NO_ERR(0, "操作成功"), ERR_DB_NOT_EXIST(1, "电表不存在"), ERR_DB_NO_DEGREE(3, "电表剩余电量未及时上报"), ERR_DB_TOKEN_HAD_USED(4, "TOKEN 已被使用");
    private int value;
    private String errorMsg;

    private ERROR(int code, String errorMsg) {
        this.value = code;
        this.errorMsg = errorMsg;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
