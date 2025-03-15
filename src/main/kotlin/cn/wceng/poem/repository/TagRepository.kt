package cn.wceng.poem.repository

import cn.wceng.poem.entity.Tag
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TagRepository : JpaRepository<Tag, Long> {
    @Query(
        """
        SELECT t 
        FROM tags t 
        JOIN t.poemTags pt 
        WHERE pt.poem.id = :poemId
    """
    )
    fun findTagsByPoemId(poemId: String): List<Tag>

    fun findByName(name: String): Tag?

//    @Cacheable("tags")
//    fun findDistinctName(): List<String>
}