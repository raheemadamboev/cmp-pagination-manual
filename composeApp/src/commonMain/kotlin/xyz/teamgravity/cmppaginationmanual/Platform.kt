package xyz.teamgravity.cmppaginationmanual

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform