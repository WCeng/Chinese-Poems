package cn.wceng.poem.controller

import cn.wceng.poem.dto.PoetryResponse
import cn.wceng.poem.service.PoetryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "诗词接口", description = "古典诗词查询接口")
@RestController
@RequestMapping("/api/poetry")
class PoetryController(private val poetryService: PoetryService) {

    // 随机获取一首诗
    @GetMapping("/random")
    @Operation(
        summary = "随机获取诗词",
        description = "从数据库中随机获取一首诗词的完整信息",
        responses = [
            ApiResponse(responseCode = "200", description = "成功获取诗词"),
            ApiResponse(responseCode = "404", description = "没有找到诗词")
        ]
    )
    fun getRandomPoetry(): ResponseEntity<PoetryResponse> {
        val poetry = poetryService.getRandomPoetry()
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(poetry)
    }

    // 根据ID获取诗词详情
    @GetMapping("/{id}")
    fun getPoemById(@PathVariable id: String): ResponseEntity<PoetryResponse> {
        val poetry = poetryService.getPoemById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(poetry)
    }

    // 分页按标题搜索
    @GetMapping("/search/title")
    fun searchByTitle(
        @RequestParam title: String,
        @RequestParam(defaultValue = "0")
        @Min(0, message = "页码不能小于0") page: Int,
        @RequestParam(defaultValue = "10")
        @Min(1, message = "每页至少1条")
        @Max(100, message = "每页最多100条") size: Int
    ): ResponseEntity<Page<PoetryResponse>> {
        val pageable = PageRequest.of(page, size)
        return ResponseEntity.ok(poetryService.searchByTitle(title, pageable))
    }

    // 分页按作者搜索
    @GetMapping("/search/author")
    fun searchByAuthor(
        @RequestParam author: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PoetryResponse>> {
        val pageable = PageRequest.of(page, size)
        return ResponseEntity.ok(poetryService.searchByAuthor(author, pageable))
    }

    // 按朝代分页查询
    @GetMapping("/dynasty/{dynasty}")
    fun searchByDynasty(
        @PathVariable dynasty: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PoetryResponse>> {
        val pageable = PageRequest.of(page, size)
        return ResponseEntity.ok(poetryService.searchByDynasty(dynasty, pageable))
    }

    // 按类型分页查询
    @GetMapping("/type/{type}")
    fun searchByType(
        @PathVariable type: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PoetryResponse>> {
        val pageable = PageRequest.of(page, size)
        return ResponseEntity.ok(poetryService.searchByType(type, pageable))
    }

    // 根据标签查询
    @GetMapping("/tag/{tagName}")
    fun searchByTag(
        @PathVariable tagName: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PoetryResponse>> {
        val pageable = PageRequest.of(page, size)
        return ResponseEntity.ok(poetryService.searchByTag(tagName, pageable))
    }

    // 按格式分页查询
    @GetMapping("/format/{format}")
    fun searchByFormat(
        @PathVariable format: String,
        @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) size: Int
    ): ResponseEntity<Page<PoetryResponse>> {
        val pageable = PageRequest.of(page, size)
        return ResponseEntity.ok(poetryService.searchByFormat(format, pageable))
    }

    // 全文搜索
    @GetMapping("/fulltext")
    fun fullTextSearch(
        @RequestParam keyword: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PoetryResponse>> {
        val pageable = PageRequest.of(page, size)
        return ResponseEntity.ok(poetryService.fullTextSearch(keyword, pageable))
    }

}