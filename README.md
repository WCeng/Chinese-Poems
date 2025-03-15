# 中华古典诗词检索系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green)](https://spring.io/projects/spring-boot)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.8.22-blue)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

基于Spring Boot和Kotlin构建的古典诗词检索系统，提供多维度诗歌查询和全文检索功能。

## 🌟 功能特性

- **多元检索**
    - 标题/作者模糊搜索
    - 朝代/类型/格式分类查询
    - 标签关联检索
    - 全文语义搜索（支持MySQL全文索引）

- **数据展示**
    - 诗词正文分句展示
    - 历代注释/译文对照
    - 专业赏析内容集成

- **技术特性**
    - JPA关联数据加载（EntityGraph优化）
    - Caffeine缓存加速（朝代/类型分类缓存）
    - 自动生成OpenAPI 3.0文档
    - 分页查询支持（Pageable）

## 🛠️ 技术栈

| 技术                 | 用途                   | 版本        |
|----------------------|-----------------------|------------|
| Spring Boot          | 核心框架               | 3.1.5      |
| Kotlin               | 开发语言               | 1.8.22     |
| MySQL                | 数据存储               | 8.0+       |
| Spring Data JPA      | ORM框架               | 3.1.5      |
| SpringDoc OpenAPI    | API文档生成           | 2.2.0      |
| Caffeine             | 本地缓存               | 3.1.8      |

## 🚀 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.9+

### 启动步骤
1. 克隆仓库
```bash
git clone https://github.com/yourusername/chinese-poetry-api.git