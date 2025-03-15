// PoemContentRepository.kt
package cn.wceng.poem.repository

import cn.wceng.poem.entity.PoemContent
import org.springframework.data.jpa.repository.JpaRepository

interface PoemContentRepository : JpaRepository<PoemContent, Long> {
    // 根据 poem_id 查询并按 order_index 排序
    fun findByPoemIdOrderByOrderIndex(poemId: String): List<PoemContent>
}