
# CMS新闻资讯管理系统

## 项目简介
CMS新闻资讯管理系统是一个基于Spring Boot的Java Web应用程序，旨在提供一个高效、便捷的新闻资讯管理平台。该系统支持文件上传、用户认证、新闻管理等功能。

## 技术栈
- Java
- Spring Boot
- MySQL
- MyBatis-Plus
- 七牛云OSS
- Swagger
- Lombok

## 项目结构
```
src
├── main
│   ├── java
│   │   └── com
│   │       └── briup
│   │           └── cms
│   │               ├── config
│   │               │   ├── UploadProperties.java
│   │               │   └── UploadUtils.java
│   │               ├── web
│   │               │   └── controller
│   │               │       └── UploadController.java
│   │               └── util
│   │                   └── Result.java
│   └── resources
│       ├── application.yml
│       └── mapper
└── test
```

## 配置文件
### `application.yml`

```yml
server:
  port: 8989

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/241118-cms?characterEncoding=utf8&useUnicode=true&useSSL=false&serverTimezone=GMT%2B8
    username: root
    password: 1234
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 100MB

upload:
  OSS:
    accessKey: "KuZHcjj75tYneZjparxp6hyQOcBoZTAJllvy1hzz"
    secretKey: "7rXshOv5buImn3-u3GgS1mbJGNzX8wDILEe40qge"
    bucket: "fallcicada-workspace"
    baseUrl: "https://portal.qiniu.com/cdn/domain/sswdsbu7d.hd-bkt.clouddn.com"

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

mybatis:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true
  mapper-locations: classpath:mapper/**/*.xml
```

## 主要功能
### 文件上传
文件上传功能使用七牛云OSS进行存储，相关代码如下：

#### `UploadUtils.java`
```java
@Slf4j
@Component
public class UploadUtils {

    @Autowired
    private UploadProperties uploadProperties;
    @Autowired
    private Gson gson;

    public String fileToOSS(MultipartFile file) throws Exception {
        log.info("文件上传到七牛云OSS:{}", file.getOriginalFilename());

        Configuration configuration = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(configuration);
        Auth auth = Auth.create(uploadProperties.getAccessKey(), uploadProperties.getSecretKey());
        String upToken = auth.uploadToken(uploadProperties.getBucket());
        String fileName = generateFilePath(file);
        Response response = uploadManager.put(file.getInputStream(), fileName, upToken, null, null);
        DefaultPutRet putRet = gson.fromJson(response.bodyString(), DefaultPutRet.class);

        log.info("文件上传成功,文件地址:{}", uploadProperties.getBaseUrl() + fileName);
        return uploadProperties.getBaseUrl() + fileName;
    }

    private String generateFilePath(MultipartFile file) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/");
        String datePate = format.format(new Date());
        String filename = file.getOriginalFilename();
        assert filename != null;
        return datePate + UUID.randomUUID() + filename.substring(filename.lastIndexOf("."));
    }
}
```

#### `UploadController.java`
```java
@Api(tags = "文件上传模块")
@Slf4j
@RestController
public class UploadController {
    @Autowired
    private UploadUtils uploadUtils;

    @ApiOperation("文件上传")
    @ApiImplicitParam(name = "Authorization", value = "用户令牌", required = true, paramType = "header")
    @PostMapping("/auth/upload")
    @SneakyThrows
    public Result upload(@RequestPart MultipartFile img){
        return Result.success(uploadUtils.fileToOSS(img));
    }
}
```

## 运行项目
1. 克隆项目到本地：
   ```bash
   git clone <repository-url>
   ```
2. 进入项目目录：
   ```bash
   cd cms-news-management
   ```
3. 使用Maven构建项目：
   ```bash
   mvn clean install
   ```
4. 运行Spring Boot应用：
   ```bash
   mvn spring-boot:run
   ```

## API文档
项目集成了Swagger用于API文档生成，启动项目后访问`http://localhost:8989/swagger-ui.html`查看API文档。

## 贡献
欢迎提交Pull Request或Issue来贡献代码和反馈问题。

## 许可证
本项目采用MIT许可证，详情请参阅LICENSE文件。
```
