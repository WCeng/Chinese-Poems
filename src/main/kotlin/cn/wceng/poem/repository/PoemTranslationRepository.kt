// PoemTranslationRepository.kt
package cn.wceng.poem.repository

import cn.wceng.poem.entity.PoemTranslation
import org.springframework.data.jpa.repository.JpaRepository

interface PoemTranslationRepository : JpaRepository<PoemTranslation, Long> {
    // 根据 poem_id 查询并按 order_index 排序
    fun findByPoemIdOrderByOrderIndex(poemId: String): List<PoemTranslation>
}