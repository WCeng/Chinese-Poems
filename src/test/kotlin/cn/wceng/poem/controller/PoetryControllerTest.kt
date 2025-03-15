// src/test/kotlin/cn/wceng/poem/controller/PoetryControllerIntegrationTest.kt
package cn.wceng.poem.controller

import cn.wceng.poem.entity.Poem
import cn.wceng.poem.entity.PoemContent
import cn.wceng.poem.entity.Tag
import cn.wceng.poem.repository.PoemContentRepository
import cn.wceng.poem.repository.PoetryRepository
import cn.wceng.poem.repository.TagRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class PoetryControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var poetryRepository: PoetryRepository

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Autowired
    private lateinit var contentRepository: PoemContentRepository

    @BeforeEach
    fun setup() {
        // 插入测试数据
        poetryRepository.saveAll(
            listOf(
                Poem(
                    id = "1",
                    title = "静夜思",
                    dynasty = "唐",
                    author = "李白",
                    sourceLink = "link1",
                    type = "诗",
                    format = "五言",
                    updateAt = "2024-05-20"
                ),
                Poem(
                    id = "2",
                    title = "春晓",
                    dynasty = "唐",
                    author = "孟浩然",
                    sourceLink = "link2",
                    type = "诗",
                    format = "五言",
                    updateAt = "2024-05-20"
                ),
                Poem(
                    id = "3",
                    title = "水调歌头",
                    dynasty = "宋",
                    author = "苏轼",
                    sourceLink = "link3",
                    type = "词",
                    format = "七言",
                    updateAt = "2024-05-20"
                )
            )
        )
    }

    @Test
    fun `getRandomPoetry should return 404 when database is empty`() {
        // 清空所有数据
        poetryRepository.deleteAll()

        mockMvc.get("/api/poetry/random")
            .andExpect { status { isNotFound() } }  // 预期 404
    }

    @Test
    fun `getRandomPoetry should return 200 OK`() {
        mockMvc.get("/api/poetry/random")
            .andExpect { status { isOk() } }
    }

    @Test
    fun `searchByTitle should return empty when database is empty`() {
        poetryRepository.deleteAll()

        mockMvc.get("/api/poetry/search/title") {
            param("title", "任意关键词")
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(0) }  // 空列表
        }
    }

    @Test
    fun `searchByTitle should return matched poems`() {
        mockMvc.get("/api/poetry/search/title") {
            param("title", "水调")
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content[0].title") { value("水调歌头") }
        }.andDo {
            print()  // 打印响应内容
        }
    }

    @Test
    fun `searchByTitle should return bad request for invalid page parameter`() {
        // 页数为负数
        mockMvc.get("/api/poetry/search/title") {
            param("title", "水调")
            param("page", "-1")  // 非法值
            param("size", "10")
        }.andExpect {
            status { isBadRequest() }  // 预期 400
        }
    }

    @Test
    fun `searchByTitle should return bad request for invalid size parameter`() {
        // 每页数量超过最大值（假设最大允许 100）
        mockMvc.get("/api/poetry/search/title") {
            param("title", "水调")
            param("page", "0")
            param("size", "101")  // 非法值
        }.andExpect {
            status { isBadRequest() }  // 预期 400
        }
    }

    @Test
    fun `searchByTitle should be case-insensitive`() {
        // 插入大小写混合标题
        poetryRepository.save(
            Poem(
                id = "4",
                title = "MixedCasePoem",
                dynasty = "现代",
                author = "测试作者",
                sourceLink = "link4",
                type = "诗",
                format = "自由",
                updateAt = "2024-05-20"
            )
        )

        // 使用小写关键词搜索
        mockMvc.get("/api/poetry/search/title") {
            param("title", "mixedcase")
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content[0].title") { value("MixedCasePoem") }
        }
    }

    @Test
    fun `searchByTitle should use default pageable when parameters omitted`() {
        // 不传递 page 和 size（使用默认值 page=0, size=10）
        mockMvc.get("/api/poetry/search/title") {
            param("title", "水调")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(1) }  // 默认返回第一页的10条中的1条
        }
    }

    @Test
    fun `getPoemById should return 404 when not found`() {
        mockMvc.get("/api/poetry/999")
            .andExpect { status { isNotFound() } }
    }

    // 测试按朝代分页查询（唐朝诗词）
    @Test
    fun `searchByDynasty should return poems of specified dynasty`() {
        mockMvc.get("/api/poetry/dynasty/唐") {
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(2) } // 预期返回2首唐诗
            jsonPath("$.content[0].dynasty") { value("唐") }
            jsonPath("$.content[1].dynasty") { value("唐") }
        }
    }

    // 测试不存在的朝代
    @Test
    fun `searchByDynasty should return empty for unknown dynasty`() {
        mockMvc.get("/api/poetry/dynasty/元") {
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(0) } // 预期无数据
        }
    }

    // 测试分页参数
    @Test
    fun `searchByDynasty should respect pageable parameters`() {
        // 请求第二页（每页1条）
        mockMvc.get("/api/poetry/dynasty/唐") {
            param("page", "1")
            param("size", "1")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(1) } // 第二页有1条
            jsonPath("$.content[0].title") { value("春晓") } // 按默认排序（需与Repository一致）
        }
    }

    // src/test/kotlin/cn/wceng/poem/controller/PoetryControllerTest.kt
    @Test
    fun `searchByType should return poems of specified type`() {
        // 测试数据已在 @BeforeEach 中插入
        mockMvc.get("/api/poetry/type/诗") {
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(2) } // 预期返回2首类型为"诗"的诗词
            jsonPath("$.content[0].type") { value("诗") }
            jsonPath("$.content[1].type") { value("诗") }
        }
    }

    @Test
    fun `searchByType should return empty for unknown type`() {
        mockMvc.get("/api/poetry/type/曲") {
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(0) } // 类型"曲"无数据
        }
    }

    @Test
    fun `searchByType should respect pageable parameters`() {
        // 请求第二页（每页1条）
        mockMvc.get("/api/poetry/type/诗") {
            param("page", "1")
            param("size", "1")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(1) } // 第二页有1条
            jsonPath("$.content[0].title") { value("春晓") } // 按默认排序（需与Repository一致）
        }
    }

    @Test
    fun `searchByTag should return empty for unknown tag`() {
        // 插入一个不相关的标签和诗词（避免干扰）
        val existingTag = tagRepository.save(Tag(name = "唐诗"))
        poetryRepository.save(
            Poem(
                id = "1",
                title = "静夜思",
                tags = listOf(existingTag),
                dynasty = "唐",
                author = "王维",
                sourceLink = "link1",
                type = "诗",
                format = "五言",
                updateAt = "2024-05-20"
            )
        )

        // 查询不存在的标签
        mockMvc.get("/api/poetry/tag/宋词") {
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(0) } // 预期无数据
        }
    }

    @Test
    fun `searchByTag should respect pageable parameters`() {
        // 插入测试标签和3条关联诗词
        val tag = tagRepository.save(Tag(name = "山水"))
        val poems = (0..2).map { index ->
            Poem(
                id = "poem-$index",
                title = "山水诗-$index",
                dynasty = "唐",
                author = "王维",
                tags = listOf(tag),
                sourceLink = "link-$index",
                type = "诗",
                format = "五言",
                updateAt = "2024-05-20"
            )
        }
        poetryRepository.saveAll(poems)

        // 请求第二页（每页2条）
        mockMvc.get("/api/poetry/tag/山水") {
            param("page", "1")
            param("size", "2")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(1) } // 总3条，第二页剩1条
            jsonPath("$.content[0].title") { value("山水诗-2") } // 按ID升序排序
        }
    }

    @Test
    fun `searchByTag should return empty when tag exists but no linked poems`() {
        // 插入标签但未关联到任何诗词
        tagRepository.save(Tag(name = "未使用标签"))

        mockMvc.get("/api/poetry/tag/未使用标签") {
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(0) }  // 预期无数据
        }
    }

    // 在 PoetryControllerTest 类中添加以下测试方法

    @Test
    fun `searchByFormat should return poems of specified format`() {
        // 测试数据已在 @BeforeEach 中插入（包含2首五言诗）
        mockMvc.get("/api/poetry/format/五言") {
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(2) }  // 预期返回2首五言诗
            jsonPath("$.content[0].format") { value("五言") }
            jsonPath("$.content[1].format") { value("五言") }
        }
    }

    @Test
    fun `searchByFormat should return empty for unknown format`() {
        // 搜索不存在的格式
        mockMvc.get("/api/poetry/format/九言") {
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(0) }  // 预期无数据
        }
    }

    @Test
    fun `searchByFormat should respect pageable parameters`() {
        // 插入更多五言诗以测试分页
        poetryRepository.saveAll(
            listOf(
                Poem(
                    id = "4",
                    title = "测试五言诗1",
                    dynasty = "现代",
                    author = "测试作者",
                    sourceLink = "link4",
                    type = "诗",
                    format = "五言",
                    updateAt = "2024-05-20"
                ),
                Poem(
                    id = "5",
                    title = "测试五言诗2",
                    dynasty = "现代",
                    author = "测试作者",
                    sourceLink = "link5",
                    type = "诗",
                    format = "五言",
                    updateAt = "2024-05-20"
                )
            )
        )

        // 请求第二页（每页2条）
        mockMvc.get("/api/poetry/format/五言") {
            param("page", "1")
            param("size", "2")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(2) }  // 总4条，第二页剩2条
            jsonPath("$.content[0].title") { value("测试五言诗1") }
            jsonPath("$.content[1].title") { value("测试五言诗2") }
        }
    }


    @Test
    @Disabled("H2 不支持全文搜索，需使用 MySQL 测试")
    fun `fullTextSearch should return poems containing keyword in title, author, or content`() {
        // 插入测试数据：包含关键词的诗歌和内容
        val poemWithKeywordInTitle = poetryRepository.save(
            Poem(
                id = "search-1",
                title = "明月几时有",
                dynasty = "宋",
                author = "苏轼",
                sourceLink = "link-search1",
                type = "词",
                format = "七言",
                updateAt = "2024-05-20"
            )
        )
        contentRepository.save(
            PoemContent(
                poemId = "search-1",
                content = "把酒问青天，不知天上宫阙。",
                orderIndex = 1
            )
        )

        val poemWithKeywordInAuthor = poetryRepository.save(
            Poem(
                id = "search-2",
                title = "静夜思",
                dynasty = "唐",
                author = "李明月",  // 作者名含关键词
                sourceLink = "link-search2",
                type = "诗",
                format = "五言",
                updateAt = "2024-05-20"
            )
        )
        contentRepository.save(
            PoemContent(
                poemId = "search-2",
                content = "床前明月光，疑是地上霜。",
                orderIndex = 1
            )
        )

        val poemWithKeywordInContent = poetryRepository.save(
            Poem(
                id = "search-3",
                title = "无题",
                dynasty = "唐",
                author = "李商隐",
                sourceLink = "link-search3",
                type = "诗",
                format = "七言",
                updateAt = "2024-05-20"
            )
        )
        contentRepository.save(
            PoemContent(
                poemId = "search-3",
                content = "明月出天山，苍茫云海间。",  // 内容含关键词
                orderIndex = 1
            )
        )

        // 执行全文搜索
        mockMvc.get("/api/poetry/fulltext") {
            param("keyword", "明月")
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(3) }  // 预期返回3条结果
            jsonPath("$.content[0].title") { value("明月几时有") }
            jsonPath("$.content[1].author") { value("李明月") }
            jsonPath("$.content[2].contents[0]") { value("明月出天山，苍茫云海间。") }
        }.andDo {
            print()  // 打印响应内容
        }
    }

    @Test
    @Disabled("H2 不支持全文搜索，需使用 MySQL 测试")
    fun `fullTextSearch should return empty when no matches`() {
        // 插入不含关键词的数据
        poetryRepository.save(
            Poem(
                id = "no-match",
                title = "春晓",
                dynasty = "唐",
                author = "孟浩然",
                sourceLink = "link-nomatch",
                type = "诗",
                format = "五言",
                updateAt = "2024-05-20"
            )
        )
        contentRepository.save(
            PoemContent(
                poemId = "no-match",
                content = "春眠不觉晓，处处闻啼鸟。",
                orderIndex = 1
            )
        )

        // 搜索不存在的关键词
        mockMvc.get("/api/poetry/fulltext") {
            param("keyword", "秋风")
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(0) }  // 预期空结果
        }
    }

    @Test
    @Disabled("H2 不支持全文搜索，需使用 MySQL 测试")
    fun `fullTextSearch should be case-insensitive`() {
        // 插入大小写混合内容
        poetryRepository.save(
            Poem(
                id = "case-insensitive",
                title = "MixedCasePoem",
                dynasty = "现代",
                author = "TestAuthor",
                sourceLink = "link-case",
                type = "诗",
                format = "自由",
                updateAt = "2024-05-20"
            )
        )
        contentRepository.save(
            PoemContent(
                poemId = "case-insensitive",
                content = "This is a TEST content with MIXED CASE keywords.",
                orderIndex = 1
            )
        )

        // 使用小写关键词搜索
        mockMvc.get("/api/poetry/fulltext") {
            param("keyword", "test")
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.size()") { value(1) }
            jsonPath("$.content[0].title") { value("MixedCasePoem") }
        }
    }

}