package com.titlark.controller;

import com.titlark.service.FileService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 上传文件
     *
     * @param files
     * @return
     */
    @RequestMapping("/uploadFile")
    public Object uploadFile(HttpServletRequest request, @RequestParam("files") MultipartFile[] files) {
        return fileService.upload(request, files);
    }

    /**
     * 文件下载
     *
     * @param response
     * @param filePath
     * @return
     */
    @RequestMapping("/download")
    public Object download(HttpServletResponse response, @RequestParam("filePath") String filePath) {
        return fileService.download(response, filePath);
    }

    @RequestMapping("/download2")
    public Object download2(HttpServletRequest request, HttpServletResponse response, @RequestParam("filePath") String filePath) {
        return fileService.download2(request, response, filePath);
    }
}
