// PoemTagRepository.kt
package cn.wceng.poem.repository

import cn.wceng.poem.entity.PoemTag
import cn.wceng.poem.entity.PoemTagId
import org.springframework.data.jpa.repository.JpaRepository

interface PoemTagRepository : JpaRepository<PoemTag, PoemTagId> {
    // 根据 poem_id 删除所有关联
    fun deleteAllByPoemId(poemId: String)
}