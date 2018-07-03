package com.pg.im.controller;

import com.pg.im.comm.FileType;
import com.pg.im.exception.ERROR;
import com.pg.im.service.FileService;
import com.pg.im.util.FileSaveUtils;
import com.pg.im.util.HttpResult;
import com.pg.im.util.TextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by zah on 2018/6/19.
 */
@RestController
@RequestMapping("/files")
@Api(value = "文件上传", tags = {"文件上传"})
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping("upload")
    @ApiOperation(value = "文件上传", notes = "文件上传")
    public ModelMap uploadFile(MultipartFile file,int type,String username) {
        if (file == null) {
            return HttpResult.getResultMap(ERROR.ERR_FILE_UPLOAD);
        }


        String fileName = file.getOriginalFilename();
        if (TextUtils.isEmpty(fileName)) {
            return HttpResult.getResultMap(ERROR.ERR_FILE_UPLOAD);
        }

        if (type < 0 || type >= FileType.values().length) {
            return HttpResult.getResultMap(ERROR.ERR_FILE_UPLOAD);
        }

        String end = fileName.substring(fileName.lastIndexOf("."));
        if (end.equalsIgnoreCase(".JPG") || end.equalsIgnoreCase(".JPGE") || end.equalsIgnoreCase(".PNG") || end.equalsIgnoreCase(".GIF")) {
            type = FileType.IMG.ordinal();
        }
        try {
            String localName = FileSaveUtils.saveImg(file);
            String id = fileService.saveFile(username, localName, FileType.values()[type]);
            return HttpResult.getResultOKMap(id);
        } catch (IOException e) {
            e.printStackTrace();
            return HttpResult.getResultMap(ERROR.ERR_FILE_UPLOAD, e.getMessage());
        }
    }

}
