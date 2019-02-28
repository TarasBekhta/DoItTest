package com.taras_bekhta.doittest.retrofit

class TasksResponse {
    var tasks = listOf<Task>()
    var meta: Meta = Meta()

    class Meta {
        var current: Int = 0
        var limit: Int = 0
        var count: Int = 0
    }
}