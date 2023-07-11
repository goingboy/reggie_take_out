package com.zsx.reggie.controller;

import com.zsx.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上传和下载
 */

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.basePath}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置，否则在本次请求结束后，临时文件就消会删除
        log.info(file.toString());

        //获取原始文件名 aaa.jpg
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀 .jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID生成文件名，防止文件名重复造成文件覆盖
        String fileName = UUID.randomUUID() + suffix;

        //判断目录是否存在
        File dir = new File(basePath);
        if( !dir.exists() ){
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //把文件名返回给前端，用作前端后续传给后端数据，保存到数据库中
        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，读取文件内容
            FileInputStream inputStream = new FileInputStream(new File(basePath + name));

            //输出流，通过输出流将文件写回浏览器，展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //读取、写入流
            int len = 0;
            byte[] bytes = new byte[1024];
            while( (len = inputStream.read(bytes)) != -1 ){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            //关闭资源
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
