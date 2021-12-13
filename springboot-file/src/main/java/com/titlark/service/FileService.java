package com.titlark.service;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import com.titlark.code.ErrorCode;
import com.titlark.entity.BaseResponse;
import com.titlark.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Paths;

@Service
public class FileService {

    @Value("${file.upload.url}")
    private String rootDir;
    @Resource
    private ResourceLoader resourceLoader;

    /**
     * 上传文件
     *
     * @param files
     * @return
     */
    public BaseResponse upload(HttpServletRequest request, MultipartFile[] files) {
        // 此处要确保是绝对路径
        String dir = new File(rootDir).getAbsolutePath() + "/" + DateUtil.getCurrentTime();
        createDir(dir);
        for (MultipartFile file : files) {
            File destFile = new File(dir, file.getOriginalFilename());
            try {
                file.transferTo(destFile);
            } catch (IOException e) {
                e.printStackTrace();
                return new BaseResponse(ErrorCode.ERROR.getCode(), ErrorCode.ERROR.getMessage());
            }
        }
        return new BaseResponse(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage());
    }

    /**
     * 文件下载
     *
     * @param response
     * @param filePath
     * @return
     */
    public ResponseEntity download(HttpServletResponse response, String filePath) {
        try {
            File file = new File(rootDir, filePath);
            if (!file.exists()) {
                response.setContentType("text/html;charset=utf-8");
                return new ResponseEntity("未找到该文件", HttpStatus.NOT_FOUND);
            }
            response.reset();
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());

            write(new FileInputStream(file), response.getOutputStream());
            return new ResponseEntity("下载成功", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            response.setContentType("text/html;charset=utf-8");
            return new ResponseEntity("未找到该文件", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 下载文件(推荐这种方式)
     *
     * @param request
     * @param filePath
     * @return
     */
    public ResponseEntity download2(HttpServletRequest request, HttpServletResponse response, String filePath) {
        try {
            File file = new File(rootDir, filePath);
            if (!file.exists()) {
                response.setContentType("text/html;charset=utf-8");
                return new ResponseEntity("未找到该文件", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/octet-stream").header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; " + getDownLoadFileName(request, new File(filePath).getName())).body(resourceLoader.getResource("file:" + Paths.get(rootDir, filePath)));
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html;charset=utf-8");
            return new ResponseEntity("未找到该文件", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 获取下载的文件名，防止文件名乱码
     *
     * @param request
     * @param filename
     * @return
     */
    private String getDownLoadFileName(HttpServletRequest request, String filename) {
        String newFileName = null;
        try {
            newFileName = URLDecoder.decode(filename, "UTF8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String userAgent = request.getHeader("User-Agent");
        // System.out.println(userAgent);
        String result = "filename=\"" + newFileName + "\"";
        // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            // IE浏览器，只能采用URLEncoder编码
            if (userAgent.indexOf("msie") != -1) {
                result = "filename=\"" + newFileName + "\"";
            }
            // Opera浏览器只能采用filename*
            else if (userAgent.indexOf("opera") != -1) {
                result = "filename*=UTF-8''" + newFileName;
            }
            // Safari浏览器，只能采用ISO编码的中文输出
            else if (userAgent.indexOf("safari") != -1) {
                try {
                    result = "filename=\"" + new String(filename.getBytes("UTF-8"), "ISO8859-1") + "\"";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
            else if (userAgent.indexOf("applewebkit") != -1) {
                try {
                    newFileName = MimeUtility.encodeText(filename, "UTF8", "B");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                result = "filename=\"" + newFileName + "\"";
            }
            // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
            else if (userAgent.indexOf("mozilla") != -1) {
                result = "filename*=UTF-8''" + newFileName;
            }
        }
        return result;
    }

    /**
     * 创建目录
     */
    private void createDir(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private boolean write(InputStream is, OutputStream out) throws IOException {
        int len = 0;
        byte[] buffer = new byte[1024];
        while (-1 != (len = is.read(buffer))) {
            out.write(buffer, 0, len);
        }
        out.close();
        is.close();
        return true;
    }
}
