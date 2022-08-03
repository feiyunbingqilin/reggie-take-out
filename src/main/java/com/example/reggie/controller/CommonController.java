package com.example.reggie.controller;

import com.example.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @date 2022/7/14- 23:10
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.toString());
        String originfilename = file.getOriginalFilename();
        String substring = originfilename.substring(originfilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString()+substring;
        //判断路径是否存在
        File file1 = new File(basePath);
        if(!file1.exists()){
            file1.mkdirs();
        }
        try {
            file.transferTo(new File(basePath+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(filename);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse servletResponse){
        try {
            FileInputStream inputStream = new FileInputStream(new File(basePath + name));
            ServletOutputStream outputStream = servletResponse.getOutputStream();

            servletResponse.setContentType("image/jpeg");
            byte[] bytes = new byte[1024];
            int i;
            while ((i=inputStream.read(bytes))!=-1){
                outputStream.write(bytes, 0, i);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
