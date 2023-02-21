package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommomCintroller {
    @Value("${reggie.basePath}")
    String basePath;


    /**
     * 这是上传文件的方法
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info("file,{}",file);
        //首先得到一个文件的名字
        String originalFilename = file.getOriginalFilename();
        //为了防止文件重名
        String filename = UUID.randomUUID().toString();
        //设置文件的路径
        try {
            file.transferTo(new File(basePath+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return R.success(filename);
    }
/**
 * 文件下载浏览器查看
 */

     @GetMapping("/download")
     public void dowmload(String name, HttpServletResponse response){
       //创建一个输入流查看文件的名字
         try {
             //得到相应的输入流
             FileInputStream fileInputStream = new FileInputStream( new File(basePath+ name));
             //创建一个输出流
             ServletOutputStream outputStream = response.getOutputStream();

             //创建一个字节流数组
             byte[] bytes = new byte[1024];
             int len =0;
             while((len=fileInputStream.read(bytes))!=-1){
                 outputStream.write(bytes,0,len);
                 outputStream.flush();
             }
             outputStream.close();
             fileInputStream.close();


         } catch (Exception e) {
             e.printStackTrace();
         }
     }
}
