package com.ransibi.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController // @Controller和@ResponseBody注解 （表现层bean注解和响应前端数据注解）
@RequestMapping(value = "rsb/common")
@Slf4j
public class CommonController {

    @Value("${upload.path}")
    private String basePath;    // 把yml属性配置中的路径读取到basePath属性当中
    /**
     * 单文件上传
     *
     * @param
     * @return
     */

    @PostMapping(value = "/upload")
    public void upload(@RequestParam("file") MultipartFile file) {
        //如果是多文件上传，把接收参数改成List<MultipartFile> fils或者MultipartFile[] files即可
        log.info("开始上传");
        /**
         * 避免多人上传文件，文件重名导致文件被替换的解决方案:
         * 截掉文件名后缀,然后通过UUID随机生成的一串字母，然后把这串随机生成的字母再拼接上原始文件后缀，那么这个文件名就随机生成了，
         */
        // 1、先获取前端上传的照片的原始名
        String filename = file.getOriginalFilename();
        if (StringUtils.isEmpty(filename)) {
            return;
        }
        // 2、截取原始文件的后缀名 .
        String s = filename.substring(filename.lastIndexOf("."));
        // 3、通过UUID随机生成文件名 （其实就是随机生成的一串字母，我们把生成的字母再加上上面截取的后缀名，那么不就是一个新的原始照片的名字了嘛~）
        // 随机生成文件名
        String s1 = UUID.randomUUID().toString();
        // 为第三步通过UUID随机生成的文件名拼接加上截取到的原始照片名的后缀名
        String fileName = s1 + s;
        File file1 = new File(basePath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        try {
            // 指定照片上传路径
            file.transferTo(new File(basePath + fileName));   // 直接路径+生成的文件名即可。
            log.info("上传成功");
        } catch (IOException e) {
            e.printStackTrace();
            log.info("上传失败!");
        }
    }

    /**
     * 多文件上传
     *
     * @param
     * @return
     */
    @PostMapping(value = "/upload/more")
    public JSONObject uploadMore(@RequestParam("files") List<MultipartFile> files) {

        JSONObject ajax = new JSONObject();
        for (MultipartFile file : files) {
            // 1、先获取前端上传的照片的原始名
            String filename = file.getOriginalFilename();
            if (StringUtils.isEmpty(filename)) {
                continue;
            }
            log.info(filename + "开始上传");
            // 2、截取原始文件的后缀名 .
            String s = filename.substring(filename.lastIndexOf("."));
            // 3、通过UUID随机生成文件名 （其实就是随机生成的一串字母，我们把生成的字母再加上上面截取的后缀名，那么不就是一个新的原始照片的名字了嘛~）
            // 随机生成文件名
            String s1 = UUID.randomUUID().toString();
            // 为第三步通过UUID随机生成的文件名拼接加上截取到的原始照片名的后缀名
            String fileName = s1 + s;
            File file1 = new File(basePath);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            try {
                // 指定照片上传路径
                log.info("新的文件名称:{}", fileName);
                file.transferTo(new File(basePath + fileName));   // 直接路径+生成的文件名即可。
                log.info(filename + "文件上传成功");
            } catch (IOException e) {
                e.printStackTrace();
                log.info("上传失败!");
                ajax.put("code", 500);
                ajax.put("msg", "文件上传失败!");
            }
        }
        ajax.put("code", 200);
        ajax.put("msg", "文件上传成功!");
        return ajax;
    }

}