# Slib

###这是一个java开发工具包
####SLib is a general lib for java development

***
####Maven address

https://mvnrepository.com/artifact/com.isoops/SLib

***
包内容如下
``````
├── annotation                              自定义注解
│   ├── Logger.java                             日志注解
│   ├── LoggerEnum.java                         日志注解枚举
│   ├── SFieldAlias.java                        对象字段别名
│   └── comtract                            注解实现
│       ├── BasicContract.java                  公共注解实现
│       └── LoggerContract.java                 日志注解实现
├── pojo                                    领域对象
│   ├── AbstractObject.java                     领域父类
│   ├── BeanCopierUtils.java                    对象复制工具
│   ├── CloneDirection.java                     领域复制方向类
│   ├── DomainType.java                         领域类型枚举
│   └── IFunction.java                          自定义Function
├── redis                                   Redis工具类
│   ├── SRedis.java                             Redis常规工具类
│   ├── SRedisMod.java                          RedisMod工具类
│   └── source                              Redis工具包
│       ├── ProtoStuffSerializerUtil.java       序列化工具
│       ├── RedisConfig.java                    配置
│       ├── RedisLock.java                      锁
│       └── RedisUtil.java                      工具类
├── support                                 层级支持工具
│   └── SuperDaoServiceImpl.java                Dao层自定义父类(基于MybatisPlus)
└── utils                                   工具集合
├── SBeanUtil.java                              对象操作工具
├── SFieldUtil.java                             Class/Field/Lambok操作工具
├── SIdCardUtil.java                            身份证操作工具
├── SObjectUtil.java                            废弃
└── SUtil.java                                  判断工具类
``````
