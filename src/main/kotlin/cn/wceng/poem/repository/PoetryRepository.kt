package cn.wceng.poem.repository

import cn.wceng.poem.entity.Poem
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

// PoetryRepository.kt
interface PoetryRepository : JpaRepository<Poem, String> {

    @EntityGraph(attributePaths = ["contents", "translations", "tags", "notes", "appreciations"])
    override fun findById(id: String): Optional<Poem>

    // 按标题分页模糊查询（支持分页）
    // 按标题模糊查询（不区分大小写）
//    @EntityGraph(attributePaths = ["contents", "translations", "tags", "notes", "appreciations"])
    @Query("SELECT p FROM poems p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    fun findByTitleContaining(title: String, pageable: Pageable): Page<Poem>

    // 按作者分页查询（支持分页）
    fun findByAuthor(author: String, pageable: Pageable): Page<Poem>

    @Query("SELECT COUNT(p) FROM poems p")
    fun countAllPoems(): Long

    // 按朝代分页查询
    @Cacheable("dynasties")
    fun findByDynasty(dynasty: String, pageable: Pageable): Page<Poem>

    // 按类型分页查询
    @Cacheable("types")
    fun findByType(type: String, pageable: Pageable): Page<Poem>

    @Query("SELECT p FROM poems p JOIN p.tags t WHERE t.name = :tagName")
    fun findByTagName(@Param("tagName") tagName: String, pageable: Pageable): Page<Poem>

    // 按格式分页查询
    fun findByFormat(format: String, pageable: Pageable): Page<Poem>

    // 全文搜索（需数据库支持全文索引）
    @Query(
        value = """
    SELECT p.* FROM poems p 
    JOIN poem_contents pc ON p.id = pc.poem_id 
    WHERE MATCH(p.title, p.author) AGAINST (:keyword IN NATURAL LANGUAGE MODE) 
    OR MATCH(pc.content) AGAINST (:keyword IN NATURAL LANGUAGE MODE)
    """,
        countQuery = """
    SELECT COUNT(*) FROM (
      SELECT p.id FROM poems p 
      JOIN poem_contents pc ON p.id = pc.poem_id 
      WHERE MATCH(p.title, p.author) AGAINST (:keyword IN NATURAL LANGUAGE MODE) 
      OR MATCH(pc.content) AGAINST (:keyword IN NATURAL LANGUAGE MODE)
    ) AS total
    """,
        nativeQuery = true
    )
    fun fullTextSearch(@Param("keyword") keyword: String, pageable: Pageable): Page<Poem>



}