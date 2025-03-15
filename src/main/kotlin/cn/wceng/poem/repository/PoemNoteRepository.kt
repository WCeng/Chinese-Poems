// PoemNoteRepository.kt
package cn.wceng.poem.repository

import cn.wceng.poem.entity.PoemNote
import org.springframework.data.jpa.repository.JpaRepository

interface PoemNoteRepository : JpaRepository<PoemNote, Long> {
    // 根据 poem_id 查询并按 order_index 排序
    fun findByPoemIdOrderByOrderIndex(poemId: String): List<PoemNote>
}