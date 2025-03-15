package cn.wceng.poem.service

import cn.wceng.poem.dto.PoetryResponse
import cn.wceng.poem.dto.TranslationDto
import cn.wceng.poem.entity.Poem
import cn.wceng.poem.repository.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class PoetryService(
    private val poetryRepository: PoetryRepository,
    private val contentRepository: PoemContentRepository,
    private val translationRepository: PoemTranslationRepository,
    private val tagRepository: TagRepository,
    private val noteRepository: PoemNoteRepository,
    private val appreciationRepository: PoemAppreciationRepository
) {

    private fun Poem.toResponse(): PoetryResponse {
        return PoetryResponse(
            id = id,
            title = title,
            dynasty = dynasty,
            author = author,
            sourceLink = sourceLink,
            type = type,
            format = format,
            updateAt = updateAt,
            contents = contents.map { it.content }, // 直接使用已加载的关联数据
            translations = translations.map { TranslationDto(it.translation, it.source) },
            tags = tags.map { it.name },
            notes = notes.map { it.note },
            appreciations = appreciations.map { it.appreciation }
        )
    }

    // 随机获取一首诗
    fun getRandomPoetry(): PoetryResponse? {
        val total = poetryRepository.countAllPoems()
        if (total == 0L) return null
        val randomPageable = PageRequest.of(Random.nextInt(total.toInt()), 1)
        return poetryRepository.findAll(randomPageable)
            .firstOrNull()
            ?.toResponse()
    }

    // 按标题分页搜索
    fun searchByTitle(title: String, pageable: Pageable): Page<PoetryResponse> =
        poetryRepository.findByTitleContaining(title, pageable)
            .map { it.toResponse() }

    // 按作者分页搜索
    fun searchByAuthor(author: String, pageable: Pageable): Page<PoetryResponse> =
        poetryRepository.findByAuthor(author, pageable)
            .map { it.toResponse() }



    // 根据ID查询
    fun getPoemById(id: String): PoetryResponse? {
        return poetryRepository.findById(id)
            .map { it.toResponse() }
            .orElse(null)
    }

    // 按朝代分页查询
    fun searchByDynasty(dynasty: String, pageable: Pageable): Page<PoetryResponse> {
        return poetryRepository.findByDynasty(dynasty, pageable)
            .map { it.toResponse() }
    }

    // 按类型分页查询
    fun searchByType(type: String, pageable: Pageable): Page<PoetryResponse> {
        return poetryRepository.findByType(type, pageable)
            .map { it.toResponse() }
    }

    // PoetryService.kt
    fun searchByTag(tagName: String, pageable: Pageable): Page<PoetryResponse> {
        return poetryRepository.findByTagName(tagName, pageable)
            .map { it.toResponse() }
    }

    // 按格式分页查询
    fun searchByFormat(format: String, pageable: Pageable): Page<PoetryResponse> {
        return poetryRepository.findByFormat(format, pageable)
            .map { it.toResponse() }
    }

    // 全文搜索（需配置全文索引）
    fun fullTextSearch(keyword: String, pageable: Pageable): Page<PoetryResponse> {
        return poetryRepository.fullTextSearch(keyword, pageable)
            .map { it.toResponse() }
    }

}