package cn.wceng.poem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class PoemApplication

fun main(args: Array<String>) {
	runApplication<PoemApplication>(*args)
}
