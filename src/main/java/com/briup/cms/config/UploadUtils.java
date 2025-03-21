package com.briup.cms.config;

import com.google.gson.Gson;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class UploadUtils {

    @Autowired
    private UploadProperties uploadProperties;
    @Autowired
    private Gson gson;

    /**
     * @param file 待上传的文件
     * @return 上传成功后回显的url路径
     */
    public String fileToOSS(MultipartFile file) throws Exception {
        log.info("文件上传到七牛云OSS:{}", file.getOriginalFilename());

        //构造一个带指定 Region 对象的配置类(根据七牛云服务器地区进行设置,
        //这里autoRegion会自动匹配相应地区七牛云服务)
        Configuration configuration = new Configuration(Region.autoRegion());

        //将配置传入UploadManager
        UploadManager uploadManager = new UploadManager(configuration);

        //验证AK与SK,AK与SK从配置类中获取
        Auth auth = Auth.create(uploadProperties.getAccessKey(),
                uploadProperties.getSecretKey());

        //指定桶 从配置类中获取
        String upToken = auth.uploadToken(uploadProperties.getBucket());

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String fileName = generateFilePath(file);

        //上传文件
        Response response = uploadManager.put(file.getInputStream(), fileName,
                upToken, null, null);

        //解析上传成功的结果
        DefaultPutRet putRet = gson.fromJson(response.bodyString(),
                DefaultPutRet.class);

        log.info("文件上传成功,文件地址:{}", uploadProperties.getBaseUrl() + fileName);

        return uploadProperties.getBaseUrl() + fileName;
    }


    /**
     * 随机生成文件在服务器上的名字, 使用UUID作为名称保证文件的唯一性(同时避免文件名乱码)
     *
     * @param file 源文件,用于获取文件 原名
     * @return 文件的新名
     */
    private String generateFilePath(MultipartFile file) {
        //通过上传日期,对文件进行分类
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/");
        String datePate = format.format(new Date());

        //获取文件名称
        String filename = file.getOriginalFilename();

        //给文件做唯一标识
        assert filename != null;
        return datePate + UUID.randomUUID() + filename.substring(filename.lastIndexOf("."));
    }

}