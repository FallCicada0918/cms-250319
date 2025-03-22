
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
project_name/
├──.gradle
├──lib
├──src
│   └──main 
│       ├──java
│       │  └──com
│       │      └──example
│       │          └──project_name
│       │              ├──config    #1. 配置文件
│       │              ├──exception #9. 异常处理
│       │              ├──dao  #3. 持久层(数据层)
│       │              ├──bean  #4. 实体类
│       │              ├──service   #5. 服务层
│       │              ├──util  #6. 工具类
│       │              ├──web 
│       │              │  ├──controller #2. 控制层
│       │              │  └──interceptor #8. 拦截器
│       │              └──Cms250319Application.java #7. 启动类
│       └──resources
│           ├── mapper
│           ├── static #8. 静态资源
│           ├── application.properties #9. 配置文件
│           └── application.yml #9. 配置文件
└── target
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

## 主要关键词
- `article` : 文章
- `category` : 栏目
- `comment` : 评论
- `log` : 日志
- `role` : 角色
- `slideshow` : 轮播图
- `subcomment` : 子评论
- `user` : 用户

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
项目集成了Swagger用于API文档生成，启动项目后访问`http://127.0.0.1:8989/dox.html`查看API文档。

## 用户页面

`http://127.0.0.1:92`

## 管理员后台

`http://127.0.0.1:92`


## 贡献
欢迎提交Pull Request或Issue来贡献代码和反馈问题。

## 许可证
本项目采用MIT许可证，详情请参阅LICENSE文件。
```
