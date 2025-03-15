package cn.wceng.poem.entity

import jakarta.persistence.*
import java.io.Serializable

// Poem.kt
@Entity(name = "poems")
data class Poem(
    @Id
    val id: String,
    val title: String,
    val dynasty: String,
    val author: String,

    @Column(name = "source_link")
    val sourceLink: String,

    val type: String,
    val format: String,

    @Column(name = "update_at")
    val updateAt: String,

    @OneToMany(cascade = [CascadeType.ALL])  // 添加级联操作
    @JoinColumn(name = "poem_id", referencedColumnName = "id")
    val contents: List<PoemContent> = emptyList(),

    @OneToMany(cascade = [CascadeType.ALL])  // 添加级联操作
    @JoinColumn(name = "poem_id", referencedColumnName = "id")
    val translations: List<PoemTranslation> = emptyList(),

// 关联标签（通过关系表）
    @ManyToMany
    @JoinTable(
        name = "poem_tags",
        joinColumns = [JoinColumn(name = "poem_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    val tags: List<Tag> = emptyList(),

// 关联注释分表
    @OneToMany
    @JoinColumn(name = "poem_id", referencedColumnName = "id")
    val notes: List<PoemNote> = emptyList(),

// 关联赏析分表
    @OneToMany
    @JoinColumn(name = "poem_id", referencedColumnName = "id")
    val appreciations: List<PoemAppreciation> = emptyList()
)





// PoemContent.kt
@Entity(name = "poem_contents")
data class PoemContent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "poem_id")
    val poemId: String,

    val content: String,
    val orderIndex: Int
)

// PoemTranslation.kt
@Entity(name = "poem_translations")
data class PoemTranslation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "poem_id")
    val poemId: String,

    val translation: String,
    val source: String,
    val orderIndex: Int
)

// Tag.kt
@Entity(name = "tags")
data class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,

    @OneToMany(mappedBy = "tag")
    val poemTags: List<PoemTag> = emptyList()
)

// PoemTag.kt (关系表实体)
@Entity(name = "poem_tags")
data class PoemTag(
    @EmbeddedId
    val id: PoemTagId,

    @ManyToOne
    @MapsId("poemId")
    @JoinColumn(name = "poem_id")
    val poem: Poem,

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    val tag: Tag
)

// 复合主键类
@Embeddable
data class PoemTagId(
    val poemId: String,
    val tagId: Long
) : Serializable

// PoemNote.kt
@Entity(name = "poem_notes")
data class PoemNote(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "poem_id")
    val poemId: String,

    val note: String,
    val orderIndex: Int
)

// PoemAppreciation.kt
@Entity(name = "poem_appreciations")
data class PoemAppreciation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "poem_id")
    val poemId: String,

    val appreciation: String,
    val orderIndex: Int
)