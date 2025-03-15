package cn.wceng.poem.dto

import cn.wceng.poem.entity.Poem

data class PoetryResponse(
    val id: String,
    val title: String,
    val dynasty: String,
    val author: String,
    val sourceLink: String,
    val type: String,
    val format: String,
    val updateAt: String,

    // 分表数据
    val contents: List<String>,
    val translations: List<TranslationDto>,
    val tags: List<String>,
    val notes: List<String>,
    val appreciations: List<String>
)

data class TranslationDto(
    val text: String,
    val source: String
)


