// PoemAppreciationRepository.kt
package cn.wceng.poem.repository

import cn.wceng.poem.entity.PoemAppreciation
import org.springframework.data.jpa.repository.JpaRepository

interface PoemAppreciationRepository : JpaRepository<PoemAppreciation, Long> {
    // 根据 poem_id 查询并按 order_index 排序
    fun findByPoemIdOrderByOrderIndex(poemId: String): List<PoemAppreciation>
}